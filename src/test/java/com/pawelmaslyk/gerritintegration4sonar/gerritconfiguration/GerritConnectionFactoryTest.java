package com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration;

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
public class GerritConnectionFactoryTest {

	@Test
	public void creatingFromSettingsWorksFine() throws Exception {
		// given
		String authKey = "authKey";
		String authKeyPassword = "authKeyPassword";
		String userName = "userName";
		int sshPort = 12;
		String sshHostName = "sshHostName";

		// when
		GerritConnection connection = GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword, userName, sshPort, sshHostName);		
		
		// then		
		assertEquals("authKey", connection.getAuthentication().getPrivateKeyFile().getPath());
		assertEquals("authKeyPassword", connection.getAuthentication().getPrivateKeyFilePassword());
		assertEquals("userName", connection.getAuthentication().getUsername());
		assertEquals("sshHostName", connection.getSshHostName());
		assertEquals(12, connection.getSshPort());
	}
	
	@Test
	@PrepareForTest({LoggerFactory.class})
	public void creatingFromEmptySettingsLogsError() throws Exception {
		// given
		String authKey = "";
		String authKeyPassword = "";
		String userName = "";
		int sshPort = 12;
		String sshHostName = "";
		
        mockStatic(LoggerFactory.class);
		Logger loggerMock = mock(Logger.class);
		
		// when
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
        try{
        	GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword, userName, sshPort, sshHostName);
        	fail();
        }
        catch(SonarException e){
        	// then		
        	verify(loggerMock, times(3)).error(anyString());
        }
	}
}
