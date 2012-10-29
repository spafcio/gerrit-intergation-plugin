package com.pawelmaslyk.gerritintegration4sonar;

import java.util.Collection;

import org.slf4j.Logger;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilters;
import org.sonar.api.measures.Metric;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;

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
	public static SonarAnalysisResult markCommit(SensorContext context, Logger logger) {
		SonarAnalysisResult result = SonarAnalysisResult.NO_PROBLEMS;

		if (countErrors(context, logger) > 0)
			result = SonarAnalysisResult.ERRORS;
		if (countWarnings(context, logger) > 0) {
			if (result == SonarAnalysisResult.NO_PROBLEMS) {
				result = SonarAnalysisResult.WARNINGS;
			}
		}
		return result;
	}

	private static int countErrors(SensorContext context, Logger logger) {
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

	private static int countWarnings(SensorContext context, Logger logger) {
		Collection<Measure> measures = context.getMeasures(MeasuresFilters.all());
		int count = 0;
		for (Measure measure : measures) {
			if (isWarningAlert(measure)) {
				logger.warn(measure.getAlertText());
				count++;
			}
		}
		return count;
	}

	private static boolean isWarningAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS) && Metric.Level.WARN.equals(measure.getAlertStatus());
	}

	private static boolean isErrorAlert(Measure measure) {
		return !measure.getMetric().equals(CoreMetrics.ALERT_STATUS) && Metric.Level.ERROR.equals(measure.getAlertStatus());
	}
}
