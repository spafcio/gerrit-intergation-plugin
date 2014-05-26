package com.pawelmaslyk.gerritintegration4sonar;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;

/**
 * This class is the entry point for the gerrit integration plugin
 */
@Properties({
		@Property(key = GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, name = "SSH Keyfile", description = "The path to the private key file for Gerrit ssh authentication."),
		@Property(key = GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, name = "SSH Keyfile Password", description = "The password for the private key file. Set to an empty value if there is no password."),
		@Property(key = GerritConnection.GERRIT_HOSTNAME_KEY, name = "Hostname", description = "The hostname where Gerrit is listening for ssh command connections."),
		@Property(key = GerritConnection.GERRIT_SSH_PORT_KEY, type = PropertyType.INTEGER, defaultValue = "29418", name = "SSH Port", description = "The port where Gerrit is listening for ssh command connections."),
		@Property(key = GerritConnection.GERRIT_USERNAME_KEY, defaultValue = "sonar", name = "Username", description = "The username to use when authenticating to Gerrit."),
		@Property(key = GerritConnection.VOTE_POSITIVE, defaultValue = "1",  type = PropertyType.SINGLE_SELECT_LIST, name = "Pass Code-Review Vote", description = "The code-review vote cast by the Sonar in case of no alerts.", options = {"+1","0"}),
		@Property(key = GerritConnection.VOTE_NEUTRAL,  defaultValue = "-1", type = PropertyType.SINGLE_SELECT_LIST, name = "Warn Code-Review Vote", description = "The code-review vote cast by the Sonar in case of warning level alerts.", options = {"0","-1"}),
		@Property(key = GerritConnection.VOTE_NEGATIVE, defaultValue = "-2", type = PropertyType.SINGLE_SELECT_LIST, name = "Fail Code-Review Vote", description = "The code-review vote cast by the Sonar in case of error level alerts.", options = {"-1","-2"}),
		@Property(key = GerritConnection.SONAR_METRICS, type = PropertyType.METRIC, multiValues = true, name = "Metrics to consider", description = "Only consider the selected metrics when evaluating the vote to cast."),
})
public final class GerritIntegrationPlugin extends SonarPlugin {

	public List<?> getExtensions() {
		return Arrays.asList(GerritNotifier.class);
	}
}
