package com.pawelmaslyk.sonar.gerritintegration;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

/**
 * This class is the entry point for the gerrit integration plugin
 */
@Properties({
		@Property(key = GerritConnection.GERRIT_AUTH_KEY_FILE_KEY, defaultValue="/home/pawel/.ssh/id_rsa", name = "SSH Keyfile", description = "The path to the private key file for Gerrit ssh authentication."),
		@Property(key = GerritConnection.GERRIT_AUTH_KEY_FILE_PASSWORD_KEY, name = "SSH Keyfile Password", description = "The password for the private key file. Set to an empty value if there is no password."),
		@Property(key = GerritConnection.GERRIT_HOSTNAME_KEY, defaultValue="gerrit.localhost", name = "Hostname", description = "The hostname where Gerrit is listening for ssh command connections."),
		@Property(key = GerritConnection.GERRIT_SSH_PORT_KEY, defaultValue = "29418", name = "SSH Port", description = "The port where Gerrit is listening for ssh command connections."),
		@Property(key = GerritConnection.GERRIT_USERNAME_KEY, defaultValue = "pawel", name = "Username", description = "The username to use when authenticating to Gerrit."),
		})
public final class GerritIntegrationPlugin extends SonarPlugin {

	public List<?> getExtensions() {
		return Arrays.asList(GerritNotifier.class);
		
	}
	
}