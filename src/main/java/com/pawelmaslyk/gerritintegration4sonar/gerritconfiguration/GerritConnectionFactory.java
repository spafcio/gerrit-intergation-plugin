package com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import com.google.common.base.Strings;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;

/**
 * I'm {@link GerritConnection} Factory
 * 
 * @author pawel
 * 
 */
public class GerritConnectionFactory {

	static Logger logger = LoggerFactory.getLogger(GerritConnectionFactory.class);

	/**
	 * Create {@link GerritConnection} from gerrit {@link Settings}
	 * 
	 * @param settings
	 *            the settings
	 * @return the {@link GerritConnection}
	 */
	public static GerritConnection createGerritConnectionFromSonarSettings(Settings settings) {
		String authKey = settings.getString(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY);
		String userName = settings.getString(GerritConnection.GERRIT_USERNAME_KEY);
		String authKeyPassword = settings.getString(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY);
		int sshPort = settings.getInt(GerritConnection.GERRIT_SSH_PORT_KEY);
		String sshHostName = settings.getString(GerritConnection.GERRIT_HOSTNAME_KEY);

		return createGerritConnection(authKey, authKeyPassword, userName, sshPort, sshHostName);
	}

	/**
	 * Create {@link GerritConnection} from strings
	 * 
	 * @param authKey
	 *            the authorization key file location
	 * @param authKeyPassword
	 *            the authorization key file password
	 * @param userName
	 *            the username for the ssh connection
	 * @param sshPort
	 *            the ssh connection port
	 * @param sshHostName
	 *            the ssh hostname
	 * @return the {@link GerritConnection}
	 */

	public static GerritConnection createGerritConnection(String authKey, String authKeyPassword, String userName, int sshPort, String sshHostName) {

		if (requiredParametersAreSet(authKey, userName, sshHostName))
			return new GerritConnection(createGerritAuthentication(authKey, userName, authKeyPassword), sshPort, sshHostName);
		else
			throw new SonarException("Cannot create gerrit connection, not all required parameters are set.");
	}

	private static boolean requiredParametersAreSet(String authKey, String userName, String sshHostName) {
		boolean requiredParametersSet = true;
		
		if (Strings.isNullOrEmpty(authKey)) {
			logger.error(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY + " is empty");
			requiredParametersSet = false;
		}
		if (Strings.isNullOrEmpty(userName)) {
			logger.error(GerritConnection.GERRIT_USERNAME_KEY + " is empty");
			requiredParametersSet = false;
		}
		if (Strings.isNullOrEmpty(sshHostName)) {
			logger.error(GerritConnection.GERRIT_HOSTNAME_KEY + " is empty");
			requiredParametersSet = false;
		}
		
		return requiredParametersSet;
	}

	private static Authentication createGerritAuthentication(String authKey, String userName, String authKeyPassword) {
		return new Authentication(new File(authKey), userName, authKeyPassword);
	}

}
