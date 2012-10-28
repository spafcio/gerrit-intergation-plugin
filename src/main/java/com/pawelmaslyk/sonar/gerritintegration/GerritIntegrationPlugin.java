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
	@Property(key = GerritIntegrationPlugin.MY_PROPERTY, name = "Plugin Property", description = "A property for the plugin") })
public final class GerritIntegrationPlugin extends SonarPlugin {

	public static final String MY_PROPERTY = "";
	
	public List<?> getExtensions() {
		return Arrays.asList(GerritNotifier.class);
	}
}