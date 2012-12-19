package com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;

public class EmptyGerritConnection extends GerritConnection {

	EmptyGerritConnection(Authentication authentication, int sshPort, String sshHostName) {
		super(authentication, sshPort, sshHostName);
	}
	
	@Override
	public String toString() {
		return "No gerrit connection";
	}

}
