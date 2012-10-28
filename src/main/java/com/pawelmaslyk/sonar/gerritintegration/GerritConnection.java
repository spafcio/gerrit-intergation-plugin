package com.pawelmaslyk.sonar.gerritintegration;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;

public class GerritConnection {

	public static final String GERRIT_AUTH_KEY_FILE_KEY = "gerrit.authkeyfile";
	public static final String GERRIT_AUTH_KEY_FILE_PASSWORD_KEY = "gerrit.authkeyfilepassword";
	public static final String GERRIT_HOSTNAME_KEY = "gerrit.hostname";
	public static final String GERRIT_SSH_PORT_KEY = "gerrit.sshport";
	public static final String GERRIT_USERNAME_KEY = "gerrit.username";

	private final Authentication authentication;
	private final String sshPort;
	private final String sshHostName;

	public GerritConnection(Authentication authentication, String sshPort,
			String sshHostName) {
		super();
		this.authentication = authentication;
		this.sshPort = sshPort;
		this.sshHostName = sshHostName;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public int getSshPort() {
		return Integer.parseInt(sshPort);
	}

	public String getSshHostName() {
		return sshHostName;
	}

	@Override
	public String toString() {
		return "GerritConnection [authentication=" + authentication + ", sshPort=" + sshPort + ", sshHostName="
				+ sshHostName + "]";
	}
	
	

}
