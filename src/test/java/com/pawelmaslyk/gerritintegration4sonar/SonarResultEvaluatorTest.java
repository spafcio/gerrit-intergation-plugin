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

public class SonarResultEvaluatorTest {

	@Test
	public void resultShouldBePositiveForNoErrorsNorWarnings() {
		//given		
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);
		
		//when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
			Arrays.<Measure> asList(
					newMeasure(CoreMetrics.LINES, null, null), 
					newMeasure(CoreMetrics.COVERAGE, Metric.Level.OK, null),
					newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, null)));

		SonarAnalysisResult result = SonarResultEvaluator.markCommit(context, logger);

		verify(logger, never()).error(anyString());
		assertEquals(SonarAnalysisResult.NO_PROBLEMS, result);
	}
	
	@Test
	public void resultShouldBeWarningsForNoErrorsButWarnings() {
		//given		
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);
		
		//when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(
						newMeasure(CoreMetrics.LINES, Metric.Level.OK, null), 
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, null)));
		
		SonarAnalysisResult result = SonarResultEvaluator.markCommit(context, logger);
		
		verify(logger, times(1)).warn(anyString());
		assertEquals(SonarAnalysisResult.WARNINGS, result);
	}
	
	@Test
	public void resultShouldBeErrorsForErrorsButNoWarnings() {
		//given		
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);
		
		//when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(
						newMeasure(CoreMetrics.LINES, Metric.Level.OK, null), 
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.ERROR, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, null)));
		
		SonarAnalysisResult result = SonarResultEvaluator.markCommit(context, logger);
		
		verify(logger, times(1)).error(anyString());
		assertEquals(SonarAnalysisResult.ERRORS, result);
	}
	
	@Test
	public void resultShouldBeErrorsForErrorAndWarnings() {
		//given		
		Logger logger = mock(Logger.class);
		SensorContext context = mock(SensorContext.class);
		
		//when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
				Arrays.<Measure> asList(
						newMeasure(CoreMetrics.LINES, Metric.Level.OK, null), 
						newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
						newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.ERROR, "Complexity>20")));
		
		SonarAnalysisResult result = SonarResultEvaluator.markCommit(context, logger);
		
		verify(logger, times(1)).error(anyString());
		assertEquals(SonarAnalysisResult.ERRORS, result);
	}

	private Measure newMeasure(Metric metric, Metric.Level level, String label) {
		return new Measure(metric).setAlertStatus(level).setAlertText(label);
	}

}
