package com.pawelmaslyk.gerritintegration4sonar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import com.pawelmaslyk.gerritintegration4sonar.gerrit.GerritCommit;
import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnection;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.ssh.SshConnectionFactory;

@RunWith(PowerMockRunner.class)
public class GerritNotifierTest {

	@Test
	@PrepareForTest({LoggerFactory.class, SshConnectionFactory.class})
	public void forIncompleteSettingsCheckThatAnExceptionWasThrown() {
		//given
		Settings settings = new Settings();
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_HOSTNAME_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_USERNAME_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_SSH_PORT_KEY, "0");
		
		Project project = mock(Project.class);
		SensorContext context = mock(SensorContext.class);
		
		mockStatic(LoggerFactory.class);
		Logger loggerMock = mock(Logger.class);
		
		GerritNotifier gerritNotifier = new GerritNotifier(settings);
		
		// when
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
        gerritNotifier.executeOn(project, context);
        
		// then
		verify(loggerMock, times(3)).error(anyString());
	}
	
	@Test
	@PrepareForTest({LoggerFactory.class, SshConnectionFactory.class})
	public void forCompleteSettingsCheckThatAnExceptionWasThrown() throws IOException {
		//given
		Settings settings = new Settings();
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, "testfile");
		settings.appendProperty(GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, null);
		settings.appendProperty(GerritConnection.GERRIT_HOSTNAME_KEY, "hostname");
		settings.appendProperty(GerritConnection.GERRIT_USERNAME_KEY, "user");
		settings.appendProperty(GerritConnection.GERRIT_SSH_PORT_KEY, "29418");
		
		settings.appendProperty(GerritCommit.GERRIT_PROJECT_KEY, "project");
		settings.appendProperty(GerritCommit.GERRIT_CHANGE_KEY, "12");
		settings.appendProperty(GerritCommit.GERRIT_PATCH_KEY, "2");
		
		GerritNotifier gerritNotifier = new GerritNotifier(settings);
		
		Project project = mock(Project.class);
		SensorContext context = mock(SensorContext.class);
		
		mockStatic(SshConnectionFactory.class);
		SshConnection sshConnection = mock(SshConnection.class);
		
		mockStatic(LoggerFactory.class);		
		Logger loggerMock = mock(Logger.class);
		
		//when
		when(sshConnection.isConnected()).thenReturn(true);
		when(sshConnection.isAuthenticated()).thenReturn(true);
		when(sshConnection.isSessionOpen()).thenReturn(true);
		
		when(SshConnectionFactory.getConnection((GerritConnection) any())).thenReturn(sshConnection);
		
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
		
		when(context.getMeasures((MeasuresFilter) anyObject())).thenReturn(
			Arrays.<Measure> asList(
					newMeasure(CoreMetrics.LINES, null, null), 
					newMeasure(CoreMetrics.COVERAGE, Metric.Level.OK, null),
					newMeasure(CoreMetrics.CLASS_COMPLEXITY, Metric.Level.OK, null)));
		
		
		when(sshConnection.executeCommand(anyString())).thenReturn("OK");
		gerritNotifier.executeOn(project, context);
		
		// then
		verify(sshConnection, times(1)).executeCommand(anyString());
		
	}

	private Measure newMeasure(Metric metric, Metric.Level level, String label) {
		return new Measure(metric).setAlertStatus(level).setAlertText(label);
	}
}
