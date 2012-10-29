package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;

@RunWith(PowerMockRunner.class)
public class GerritCommitFactoryTest {

	@Test
	public void creatingCommitFromSettingsWorksFine() throws Exception {
		// given
		String projectName = "project";
		String change = "3";
		String patch = "1";

		// when
		GerritCommit commit = GerritCommitFactory.createGerritCommitFromSonarSettings(projectName, change, patch)		;
		
		// then		
		assertEquals("project", commit.getProjectName());
		assertEquals("3", commit.getChange());
		assertEquals("1", commit.getPatch());
	}
	
	@Test
	@PrepareForTest({LoggerFactory.class})
	public void creatingFromEmptySettingsLogsError() throws Exception {
		// given
		String projectName = "";
		String change = "";
		String patch = "";
		
        mockStatic(LoggerFactory.class);
		Logger loggerMock = mock(Logger.class);
		
		// when
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
        try{
        	GerritCommitFactory.createGerritCommitFromSonarSettings(projectName, change, patch);
        	fail();
        }
        catch(SonarException e){
        	// then		
        	verify(loggerMock, times(3)).error(anyString());
        }
	}

}
