package com.pawelmaslyk.gerritintegration4sonar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilters;
import org.sonar.api.measures.Metric;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;

/**
 * I contain logic to assess sonar analysis success
 * 
 * @author pawel
 * 
 */
public class SonarResultEvaluator {

	/**
	 * I determine how successful was sonar analysis
	 * 
	 * @param context
	 *            the sensor context
	 * @param logger
	 *            the logger
	 * @return the sonar analysis result
	 */
	public static SonarAnalysisResult getResult(SensorContext context, Logger logger) {
		SonarAnalysisStatus status = SonarAnalysisStatus.NO_PROBLEMS;
		StringBuilder messageBuilder = new StringBuilder("Sonar analysis:\n");

		List<Measure> errors = getErrors(context, logger);
		List<Measure> warnings = getWarnings(context, logger);
		if (warnings.size() > 0) {
			status = SonarAnalysisStatus.WARNINGS;
			messageBuilder.append("  Warnings:");
			for (Measure warning : warnings) {
				messageBuilder.append("\n    ");
				messageBuilder.append(warning.getAlertText());
			}
		}
		if (errors.size() > 0) {
			if (status == SonarAnalysisStatus.WARNINGS) {
				messageBuilder.append("\n");
			}

			status = SonarAnalysisStatus.ERRORS;
			messageBuilder.append("  Errors:");
			for (Measure error : errors) {
				messageBuilder.append("\n    ");
				messageBuilder.append(error.getAlertText());
			}
		}
		if (status == SonarAnalysisStatus.NO_PROBLEMS) {
			messageBuilder.append("  No alerts.");
		}

		return new SonarAnalysisResult(messageBuilder.toString(), status);
	}

	private static List<Measure> getErrors(SensorContext context, Logger logger) {
		List<Measure> errors = new ArrayList<Measure>();

		Collection<Measure> measures = context.getMeasures(MeasuresFilters.all());
		for (Measure measure : measures) {
			if (isErrorAlert(measure)) {
				logger.error(measure.getAlertText());
				errors.add(measure);
			}
		}

		return errors;
	}

	private static List<Measure> getWarnings(SensorContext context, Logger logger) {
		List<Measure> warnings = new ArrayList<Measure>();

		Collection<Measure> measures = context.getMeasures(MeasuresFilters.all());
		for (Measure measure : measures) {
			if (isWarningAlert(measure)) {
				logger.warn(measure.getAlertText());
				warnings.add(measure);
			}
		}
		return warnings;
	}

	private static boolean isWarningAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS)
				&& Metric.Level.WARN.equals(measure.getAlertStatus());
	}

	private static boolean isErrorAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS)
				&& Metric.Level.ERROR.equals(measure.getAlertStatus());
	}
}
