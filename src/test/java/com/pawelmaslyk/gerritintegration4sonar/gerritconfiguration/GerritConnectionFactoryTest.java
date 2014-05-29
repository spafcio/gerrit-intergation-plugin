package com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

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
		GerritConnection connection = GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword,
		                userName, sshPort, sshHostName);

		// then
		assertEquals("authKey", connection.getAuthentication().getPrivateKeyFile().getPath());
		assertEquals("authKeyPassword", connection.getAuthentication().getPrivateKeyFilePassword());
		assertEquals("userName", connection.getAuthentication().getUsername());
		assertEquals("sshHostName", connection.getSshHostName());
		assertEquals(12, connection.getSshPort());
	}

	@Test
	@PrepareForTest({ LoggerFactory.class })
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
		GerritConnection connection = GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword,
		                userName, sshPort, sshHostName);

		assertEquals(connection instanceof EmptyGerritConnection, true);
	}
}
