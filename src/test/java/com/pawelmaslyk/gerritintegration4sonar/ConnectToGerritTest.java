package com.pawelmaslyk.gerritintegration4sonar;

import java.io.IOException;

import org.junit.Test;
import org.junit.Ignore;

import org.sonar.api.config.Settings;

import com.pawelmaslyk.gerritintegration4sonar.GerritNotifier;
import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommit;
import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommitFactory;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnectionFactory;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;

@Ignore("this is an integration test")
public class ConnectToGerritTest {

	private static final String authKey = System.getProperty("user.home") + "/.ssh/id_rsa_sonar";
	private static final String authKeyPassword = null;
	private static final String userName = "sonar";
	private static final int sshPort = 29418;
	private static final String hostName = "127.0.0.1";

	@Test
	public void testIfOneCanConnectToGerrit() throws IOException {

		Settings settings = new Settings();

		//given
		GerritConnection connection = GerritConnectionFactory.createGerritConnection(authKey, authKeyPassword, userName, sshPort, hostName);
		GerritCommit commit = GerritCommitFactory.createGerritCommitFromSonarSettings("spafcio/fdd", "5", "1");		
		SshConnection ssh = SshConnectionFactory.getConnection(connection);
		
		//when
		//the analysis is run, the local sonar instance must be up
		String command = GerritNotifier.createCodeReviewCommand(settings, commit, SonarAnalysisResult.ERRORS, "");
		ssh.executeCommand(command);
		
		//then
		
	}

}
