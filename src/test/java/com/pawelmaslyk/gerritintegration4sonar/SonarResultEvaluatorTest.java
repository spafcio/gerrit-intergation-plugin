package com.pawelmaslyk.gerritintegration4sonar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;

public class SonarResultEvaluatorTest {

	@Test
	public void resultShouldBePositiveForNoErrorsNorWarnings() {
		// given
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);

		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, null, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.OK, "Message2"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message3")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger);

		verify(logger, never()).error(anyString());
		assertEquals(SonarAnalysisStatus.NO_PROBLEMS, result.getStatus());
		assertEquals("Sonar analysis:\n  No alerts.", result.getMessage());
	}

	@Test
	public void resultShouldBeWarningsForNoErrorsButWarnings() {
		// given
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);

		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message2")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger);

		verify(logger, times(1)).warn(anyString());
		assertEquals(SonarAnalysisStatus.WARNINGS, result.getStatus());
		assertEquals("Sonar analysis:\n  Warnings:\n    Coverage<80", result.getMessage());
	}

	@Test
	public void resultShouldBeErrorsForErrorsButNoWarnings() {
		// given
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);

		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.ERROR, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message2")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger);

		verify(logger, times(1)).error(anyString());
		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis:\n  Errors:\n    Coverage<80", result.getMessage());
	}

	@Test
	public void resultShouldBeErrorsForErrorAndNoWarnings() {
		// given
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);

		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.ERROR, "Complexity>20")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger);

		verify(logger, times(1)).error(anyString());
		verify(logger, times(1)).warn(anyString());
		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis:\n  Warnings:\n    Coverage<80\n  Errors:\n    Complexity>20", result.getMessage());
	}

	private Measure newMeasure(Metric metric, Metric.Level level, String label) {
		return new Measure(metric).setAlertStatus(level).setAlertText(label);
	}

}
