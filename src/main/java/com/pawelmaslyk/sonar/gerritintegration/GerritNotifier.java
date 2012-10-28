package com.pawelmaslyk.sonar.gerritintegration;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilters;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;

public class GerritNotifier extends BuildBreaker {

	private final Settings settings;

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

	protected void analyseMeasures(SensorContext context, Logger logger) throws IOException {
		GerritConnection connection = GerritConnectionFactory.createGerritConnectionFromSonarSettings(settings);
		GerritCommit commit = GerritCommitFactory.createGerritCommitFromSonarSettings(settings);
		
		int mark = markCommit(context, logger);
		
		logger.info(String.format("Sending results to gerrit for %s: %d", commit, mark));
		
		SshConnection ssh = SshConnectionFactory.getConnection(connection.getSshHostName(), connection.getSshPort(), connection.getAuthentication());
		
		ssh.executeCommand(String.format("gerrit approve --project %s --message \"Sonar analysis\" --code-review %s %s,%s", commit.getProjectName(),  mark, commit.getChange(), commit.getPatch()));
		
		logger.info("Results sent successfully");
	}

	private int markCommit(SensorContext context, Logger logger) {
		int mark = 1;
		
		if(countErrors(context, logger) > 0)
			mark = -2;
		else if(countWarnings(context, logger) > 0)
			mark = -1;
		return mark;
	}

	private int countErrors(SensorContext context, Logger logger) {
		Collection<Measure> measures = context.getMeasures(MeasuresFilters.all());
		int count = 0;
		for (Measure measure : measures) {
			if (isErrorAlert(measure)) {
				logger.error(measure.getAlertText());
				count++;
			}
		}
		return count;
	}

	private int countWarnings(SensorContext context, Logger logger) {
		Collection<Measure> measures = context.getMeasures(MeasuresFilters.all());
		int count = 0;
		for (Measure measure : measures) {
			if (isWarningAlert(measure)) {
				logger.warn(measure.getAlertText());
			}
		}
		return count;
	}

	private boolean isWarningAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS)
				&& Metric.Level.WARN.equals(measure.getAlertStatus());
	}

	private boolean isErrorAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS)
				&& Metric.Level.ERROR.equals(measure.getAlertStatus());
	}
}
