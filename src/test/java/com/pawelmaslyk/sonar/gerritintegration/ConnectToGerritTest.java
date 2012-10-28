package com.pawelmaslyk.sonar.gerritintegration;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.Authentication;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;


public class ConnectToGerritTest {

	private static final File GERRIT_AUTH_KEY_FILE = new File("/home/pawel/.ssh/id_rsa");
	private static final String GERRIT_USERNAME = "pawel";
	private static final String GERRIT_AUTH_KEY_FILE_PASSWORD = null;
	private static final int GERRIT_SSH_PORT = 29418;
	private static final String GERRIT_HOST = "gerrit.localhost";

	@Test
	public void testIfOneCanConnectToGerrit() throws IOException {
		Authentication authentication = new Authentication(GERRIT_AUTH_KEY_FILE, GERRIT_USERNAME, GERRIT_AUTH_KEY_FILE_PASSWORD);
		SshConnection ssh = SshConnectionFactory.getConnection(GERRIT_HOST, GERRIT_SSH_PORT, authentication);
		
		String mark = "-1";
		String change = "5";
		String patchset = "1";
		
		ssh.executeCommand(String.format("gerrit approve --project spafcio/fdd --message test --code-review %s %s,%s", mark, change, patchset));
		
		//Paul Graham - haker, inkubator
	}

}
