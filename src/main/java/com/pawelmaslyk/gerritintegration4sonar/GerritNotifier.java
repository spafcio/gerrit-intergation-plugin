package com.pawelmaslyk.gerritintegration4sonar;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;

import java.io.IOException;

/**
 * I'm core of the Gerrit Integration plugin
 * 
 * @author pawel
 * 
 */
public class GerritNotifier extends BuildBreaker {

	private static final Logger logger = LoggerFactory.getLogger(GerritNotifier.class);

	private final Settings settings;

	private final Server server;

	private final SonarResultEvaluator sonarResultEvaluator;

	/**
	 * I create {@link GerritNotifier} and apply sonar settings
	 * 
	 * @param settings
	 *            the settings
	 */
	public GerritNotifier(Settings settings, Server server, SonarResultEvaluator sonarResultEvaluator) {
		this.settings = settings;
		this.server = server;
		this.sonarResultEvaluator = sonarResultEvaluator;
	}

	@Override
	public void executeOn(Project project, SensorContext context) {
		try {
			analyseMeasures(project, context, logger);
		} catch (IOException e) {
			logger.error("Could not notify gerrit about the results of the analysis");
			e.printStackTrace();
		}
	}

	private void analyseMeasures(Project project, SensorContext context, Logger logger) throws IOException {
		GerritConnection connection = GerritConnectionFactory.createGerritConnectionFromSonarSettings(settings);

		if (connection instanceof EmptyGerritConnection) {
			logger.info("Gerrit has not been notified, because the gerrit connection has not been defined, please check plugin settings");
			return;
		}

		GerritCommit commit = GerritCommitFactory.createGerritCommitFromSonarSettings(settings);
		String dashboardUrl = getDashboardUrl(project);
		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, dashboardUrl);

		if (commit instanceof EmptyGerritCommit) {
			logger.info("Gerrit has not been notified, because the commit information is missing, please check if all parameters are passed while running sonar");
			return;
		} else {

			logger.info(String.format("Sending results to gerrit for %s: %s", commit, result));

			SshConnection ssh = SshConnectionFactory.getConnection(connection);
			String command = GerritCommand.createCodeReview(commit, result);
			ssh.executeCommand(command);

			logger.info("Results sent successfully");
		}
	}

	/**
	 * Copied from Sonar's {@link UpdateStatusJob#logSuccess(Logger)}
	 * 
	 * @return The URL for the Sonar dashboard
	 */
	private String getDashboardUrl(Project project) {
		String baseUrl = settings.getString(CoreProperties.SERVER_BASE_URL);
		if (baseUrl.equals(settings.getDefaultValue(CoreProperties.SERVER_BASE_URL))) {
			// If server base URL was not configured in Sonar server then is is
			// better to take URL configured on batch side
			baseUrl = server.getURL();
		}
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		return baseUrl + "dashboard/index/" + project.getKey();
	}
}
