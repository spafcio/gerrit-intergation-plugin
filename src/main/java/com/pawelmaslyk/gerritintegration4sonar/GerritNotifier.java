package com.pawelmaslyk.gerritintegration4sonar;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import com.pawelmaslyk.gerritintegration4sonar.gerrit.EmptyGerritCommit;
import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommand;
import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommit;
import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommitFactory;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.EmptyGerritConnection;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnectionFactory;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;

/**
 * I'm core of the Gerrit Integration plugin
 * 
 * @author pawel
 * 
 */
public class GerritNotifier extends BuildBreaker {

	private final Settings settings;

	/**
	 * I create {@link GerritNotifier} and apply sonar settings
	 * 
	 * @param settings
	 *            the settings
	 */
	public GerritNotifier(Settings settings) {
		this.settings = settings;
	}

	public void executeOn(Project project, SensorContext context) {
		Logger logger = LoggerFactory.getLogger(getClass());
		try {
			analyseMeasures(context, logger);
		} catch (IOException e) {
			logger.error("Could not notify gerrit about the results of the analysis");
			e.printStackTrace();
		}
	}

	private void analyseMeasures(SensorContext context, Logger logger) throws IOException {
		GerritConnection connection = GerritConnectionFactory.createGerritConnectionFromSonarSettings(settings);

		if (connection instanceof EmptyGerritConnection) {
			logger.info("Gerrit has not been notified, because the gerrit connection has not been defined, please check plugin settings");
			return;
		}

		GerritCommit commit = GerritCommitFactory.createGerritCommitFromSonarSettings(settings);
		SonarAnalysisResult mark = SonarResultEvaluator.markCommit(context, logger);

		if (commit instanceof EmptyGerritCommit) {
			logger.info("Gerrit has not been notified, because the commit information is missing, please check if all parameters are passed while running sonar");
			return;
		} else {

			logger.info(String.format("Sending results to gerrit for %s: %s", commit, mark));

			SshConnection ssh = SshConnectionFactory.getConnection(connection);
			String command = GerritCommand.createCodeReview(commit, mark);
			ssh.executeCommand(command);

			logger.info("Results sent successfully");
		}
	}
}
