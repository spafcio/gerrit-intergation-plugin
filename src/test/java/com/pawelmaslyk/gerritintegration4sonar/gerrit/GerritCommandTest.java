package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;


import org.junit.Test;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;

public class GerritCommandTest {

	@Test
	public void testApprovalWithPositiveValue() {
		//given
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, SonarAnalysisResult.NO_PROBLEMS);
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review 1 1,2", command);
	}
	
	@Test
	public void testApprovalWithWarning() {
		//given
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, SonarAnalysisResult.WARNINGS);
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review -1 1,2", command);
	}
	
	@Test
	public void testApprovalWithError() {
		//given
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritCommand.createCodeReview(commit, SonarAnalysisResult.ERRORS);
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review -2 1,2", command);
	}

}
