package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GerritCommandTest {

	@Test
	public void testApprovalWithPositiveValue() {
		// given
		GerritCommit commit = mock(GerritCommit.class);

		// when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, new SonarAnalysisResult("Sonar analysis",
				SonarAnalysisStatus.NO_PROBLEMS));

		// then
		assertEquals("gerrit review --project projectname --message \"Sonar analysis\" --code-review 1 1,2", command);
	}

	@Test
	public void testApprovalWithWarning() {
		// given
		GerritCommit commit = mock(GerritCommit.class);

		// when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, new SonarAnalysisResult("Some\n message",
				SonarAnalysisStatus.WARNINGS));

		// then
		assertEquals("gerrit review --project projectname --message \"Some\n message\" --code-review -1 1,2", command);
	}

	@Test
	public void testApprovalWithError() {
		// given
		GerritCommit commit = mock(GerritCommit.class);

		// when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, new SonarAnalysisResult("Error message",
				SonarAnalysisStatus.ERRORS));

		// then
		assertEquals("gerrit review --project projectname --message \"Error message\" --code-review -2 1,2", command);
	}

}
