package com.pawelmaslyk.sonar.gerritintegration;

import java.io.IOException;

import org.junit.Test;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;


public class ConnectToGerritTest {

	private static final String authKey = "/home/pawel/.ssh/id_rsa";
	private static final String authKeyPassword = null;
	private static final String userName = "pawel";
	private static final String sshPort = "29418";
	private static final String hostName = "gerrit.localhost";

	@Test
	public void testIfOneCanConnectToGerrit() throws IOException {
		
		GerritConnection connection = GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword, userName, sshPort, hostName);
		SshConnection ssh = SshConnectionFactory.getConnection(connection.getSshHostName(), connection.getSshPort(), connection.getAuthentication());
		
		String mark = "-2";
		GerritCommit commit = new GerritCommit("spafcio/fdd", "5", "1");
		
		ssh.executeCommand(String.format("gerrit approve --project %s --message \"Sonar analysis\" --code-review %s %s,%s", commit.getProjectName(),  mark, commit.getChange(), commit.getPatch()));
		
		//Paul Graham - haker, inkubator
	}

}
