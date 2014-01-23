package com.pawelmaslyk.gerritintegration4sonar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;

@RunWith(PowerMockRunner.class)
public class SonarResultEvaluatorTest {

	private final static String ADDRESS = "http://10.10.10.10:9090/web/host";

	@Mock
	Logger logger;

	@Mock
	SensorContext context;

	@Test
	public void resultShouldBePositiveForNoErrorsNorWarnings() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, null, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.OK, "Message2"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message3")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger, ADDRESS);

		verify(logger, never()).error(anyString());
		assertEquals(SonarAnalysisStatus.NO_PROBLEMS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  No alerts.", result.getMessage());
	}

	@Test
	public void resultShouldBeWarningsForNoErrorsButWarnings() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message2")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger, ADDRESS);

		verify(logger, times(1)).warn(anyString());
		assertEquals(SonarAnalysisStatus.WARNINGS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  Warnings:\n    Coverage<80", result.getMessage());
	}

	@Test
	public void resultShouldBeErrorsForErrorsButNoWarnings() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.ERROR, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message2")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger, ADDRESS);

		verify(logger, times(1)).error(anyString());
		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  Errors:\n    Coverage<80", result.getMessage());
	}

	@Test
	public void resultShouldBeErrorsForErrorAndNoWarnings() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.ERROR, "Complexity>20")));

		SonarAnalysisResult result = SonarResultEvaluator.getResult(context, logger, ADDRESS);

		verify(logger, times(1)).error(anyString());
		verify(logger, times(1)).warn(anyString());
		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  Warnings:\n    Coverage<80\n  Errors:\n    Complexity>20",
				result.getMessage());
	}

	private Measure newMeasure(Metric metric, Metric.Level level, String label) {
		return new Measure(metric).setAlertStatus(level).setAlertText(label);
	}

}
