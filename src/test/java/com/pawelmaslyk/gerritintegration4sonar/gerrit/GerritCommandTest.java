package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;

import org.sonar.api.config.Settings;

import com.pawelmaslyk.gerritintegration4sonar.GerritNotifier;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;

@Ignore("just put in compile condition, needs to be refactored")
public class GerritCommandTest {

	@Test
	public void testApprovalWithPositiveValue() {
		//given
		Settings settings = new Settings();
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritNotifier.createCodeReviewCommand(settings, commit, SonarAnalysisResult.NO_PROBLEMS, "");
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review 1 1,2", command);
	}
	
	@Test
	public void testApprovalWithWarning() {
		//given
		Settings settings = new Settings();
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritNotifier.createCodeReviewCommand(settings, commit, SonarAnalysisResult.WARNINGS, "");
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review -1 1,2", command);
	}
	
	@Test
	public void testApprovalWithError() {
		//given
		Settings settings = new Settings();
		GerritCommit commit = mock(GerritCommit.class);
		
		//when
		when(commit.getProjectName()).thenReturn("projectname");
		when(commit.getChange()).thenReturn("1");
		when(commit.getPatch()).thenReturn("2");
		String command = GerritNotifier.createCodeReviewCommand(settings, commit, SonarAnalysisResult.ERRORS, "");
		
		//then
		assertEquals("gerrit approve --project projectname --message \"Sonar analysis\" --code-review -2 1,2", command);
	}

}
