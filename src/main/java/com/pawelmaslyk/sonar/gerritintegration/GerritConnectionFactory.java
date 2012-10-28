package com.pawelmaslyk.sonar.gerritintegration;

import java.io.File;

import org.sonar.api.config.Settings;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;

public class GerritConnectionFactory {

	public static GerritConnection createGerritConnectionFromSonarSettings(Settings settings) {
		String authKey = settings.getString(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY);
		String userName = settings.getString(GerritConnection.GERRIT_USERNAME_KEY);
		String authKeyPassword = settings.getString(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY);
		String sshPort = settings.getString(GerritConnection.GERRIT_SSH_PORT_KEY);
		String sshHostName = settings.getString(GerritConnection.GERRIT_HOSTNAME_KEY);

		return createGerritConnection(authKey, authKeyPassword, userName, sshPort, sshHostName);
	}

	public static GerritConnection createGerritConnection(String authKey, String authKeyPassword, String userName,
			String sshPort, String sshHostName) {
		return new GerritConnection(createGerritAuthentication(authKey, userName, authKeyPassword), sshPort,
				sshHostName);
	}

	private static Authentication createGerritAuthentication(String authKey, String userName, String authKeyPassword) {
		return new Authentication(new File(authKey), userName, authKeyPassword);
	}

}
