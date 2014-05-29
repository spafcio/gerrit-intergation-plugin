package com.pawelmaslyk.gerritintegration4sonar;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class SonarResultEvaluatorTest {

	private final static String ADDRESS = "http://10.10.10.10:9090/web/host";

	@Mock
	SensorContext context;

	@Mock
	RuleFinder ruleFinder;

	SonarResultEvaluator sonarResultEvaluator;

	@Before
	public void setUp() {
		sonarResultEvaluator = new SonarResultEvaluator();
	}

	@Test
	public void resultShouldBePositiveForNoErrorsNorWarnings() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
		                Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, null, "Message1"),
		                                newMeasure(CoreMetrics.COVERAGE, Metric.Level.OK, "Message2"),
		                                newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, "Message3")));

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

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

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

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

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

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

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  Warnings:\n    Coverage<80\n  Errors:\n    Complexity>20",
		                result.getMessage());
	}

	@Test
	public void resultShouldBeErrorsForErrorAndNoWarningsPlusOldIssues() {
		// when
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
		                Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
		                                newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
		                                newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.ERROR, "Complexity>20")));
		List<Issue> issues = new ArrayList<Issue>();
		Issue issue = mock(Issue.class);
		issues.add(issue);

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis (" + ADDRESS + "):\n  Warnings:\n    Coverage<80\n  Errors:\n    Complexity>20",
		                result.getMessage());
	}

	@Test
	@PrepareForTest({ Rule.class })
	public void resultShouldBeErrorsForErrorAndNoWarningsPlusNewIssues() {
		// when

		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
		                Arrays.<Measure> asList(newMeasure(CoreMetrics.LINES, Metric.Level.OK, "Message1"),
		                                newMeasure(CoreMetrics.COVERAGE, Metric.Level.WARN, "Coverage<80"),
		                                newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.ERROR, "Complexity>20")));

		Rule rule = mock(Rule.class);
		RuleKey ruleKey = RuleKey.parse("1:2");
		when(ruleFinder.findByKey(ruleKey)).thenReturn(rule);
		when(rule.getSeverity()).thenReturn(RulePriority.BLOCKER);

		Issue issue = mock(Issue.class);
		when(issue.ruleKey()).thenReturn(ruleKey);
		when(issue.message()).thenReturn("issue message");
		when(issue.componentKey()).thenReturn("componentKey");
		when(issue.line()).thenReturn(12);

		List<Issue> issues = new ArrayList<Issue>();
		issues.add(issue);

		SonarAnalysisResult result = sonarResultEvaluator.getResult(context, ADDRESS);

		assertEquals(SonarAnalysisStatus.ERRORS, result.getStatus());
		assertEquals("Sonar analysis ("
		                + ADDRESS
		                + "):\n  Warnings:\n    Coverage<80\n  Errors:\n    Complexity>20",
		                result.getMessage());
	}

	private Measure newMeasure(Metric metric, Metric.Level level, String label) {
		return new Measure(metric).setAlertStatus(level).setAlertText(label);
	}

}
