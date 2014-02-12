package com.pawelmaslyk.gerritintegration4sonar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;

import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommit;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;
import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisStatus;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ GerritNotifier.class, SshConnectionFactory.class })
public class GerritNotifierTest {

	private static final String BASE_URL = "someurl";
	private static final String URL = BASE_URL + "/dashboard/index/null";

	@Mock
	Server server;

	@Mock
	ProjectIssues projectIssues;

	@Mock
	SonarResultEvaluator sonarResultEvaluator;

	@Mock
	Project project;

	@Mock
	SensorContext context;

	Settings settings = new Settings();

	@Before
	public void setUp() {
		settings.appendProperty(CoreProperties.SERVER_BASE_URL, BASE_URL);
	}

	@Test
	public void forIncompleteSettingsCheckThatAnExceptionWasThrown() {
		// given
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_HOSTNAME_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_USERNAME_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_SSH_PORT_KEY, "0");

		GerritNotifier gerritNotifier = new GerritNotifier(settings, server, sonarResultEvaluator);

		// when
		gerritNotifier.executeOn(project, context);

		// then
		verifyZeroInteractions(sonarResultEvaluator);
	}

	@Test
	public void forCompleteSettingsCheckThatAnExceptionWasThrown() throws IOException {
		// given
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, "testfile");
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_HOSTNAME_KEY, "hostname");
		settings.appendProperty(GerritConnection.GERRIT_USERNAME_KEY, "user");
		settings.appendProperty(GerritConnection.GERRIT_SSH_PORT_KEY, "29418");

		settings.appendProperty(GerritCommit.GERRIT_PROJECT_KEY, "project");
		settings.appendProperty(GerritCommit.GERRIT_CHANGE_KEY, "12");
		settings.appendProperty(GerritCommit.GERRIT_PATCH_KEY, "2");

		GerritNotifier gerritNotifier = new GerritNotifier(settings, server, sonarResultEvaluator);

		mockStatic(SshConnectionFactory.class);
		SshConnection sshConnection = mock(SshConnection.class);

		// when
		when(sshConnection.isConnected()).thenReturn(true);
		when(sshConnection.isAuthenticated()).thenReturn(true);
		when(sshConnection.isSessionOpen()).thenReturn(true);
		when(SshConnectionFactory.getConnection((GerritConnection) any())).thenReturn(sshConnection);
		when(sshConnection.executeCommand(anyString())).thenReturn("OK");

		when(sonarResultEvaluator.getResult(context, URL)).thenReturn(
				new SonarAnalysisResult("message", SonarAnalysisStatus.NO_PROBLEMS));

		gerritNotifier.executeOn(project, context);

		// then
		verify(sshConnection).executeCommand(anyString());
		verify(sonarResultEvaluator).getResult(context, URL);

	}

}
