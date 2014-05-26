package com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;

/**
 * I represent details of gerrit ssh connection
 * 
 * @author pawel
 * 
 */
public class GerritConnection {

	/**
	 * Key for obtaining authorization key location from the sonar settings
	 */
	public static final String GERRIT_AUTH_KEY_FILE_KEY = "gerrit.authkeyfile";

	/**
	 * Key for obtaining authorization key location password from the sonar settings
	 */
	public static final String GERRIT_AUTH_KEY_FILE_PASSWORD_KEY = "gerrit.authkeyfilepassword";
	/**
	 * Key for obtaining gerrit hostname from the sonar settings
	 */
	public static final String GERRIT_HOSTNAME_KEY = "gerrit.hostname";

	/**
	 * Key for obtaining gerrit's ssh port from the sonar settings
	 */
	public static final String GERRIT_SSH_PORT_KEY = "gerrit.sshport";

	/**
	 * Key for obtaining username that sends a ssh command from the sonar settings
	 */
	public static final String GERRIT_USERNAME_KEY = "gerrit.username";

	public static final String VOTE_POSITIVE = "gerrit.votepass";
	public static final String VOTE_NEUTRAL  = "gerrit.votewarn";
	public static final String VOTE_NEGATIVE = "gerrit.votefail";
	public static final String SONAR_METRICS = "gerrit.metrics";

	private final Authentication authentication;
	private final int sshPort;
	private final String sshHostName;

	GerritConnection(Authentication authentication, int sshPort, String sshHostName) {
		this.authentication = authentication;
		this.sshPort = sshPort;
		this.sshHostName = sshHostName;
	}

	@Override
	public String toString() {
		return "GerritConnection [authentication=" + authentication + ", sshPort=" + sshPort + ", sshHostName=" + sshHostName + "]";
	}

	/**
	 * @return the authentication
	 */
	public Authentication getAuthentication() {
		return authentication;
	}

	/**
	 * @return the sshPort
	 */
	public int getSshPort() {
		return sshPort;
	}

	/**
	 * @return the sshHostName
	 */
	public String getSshHostName() {
		return sshHostName;
	}

}
