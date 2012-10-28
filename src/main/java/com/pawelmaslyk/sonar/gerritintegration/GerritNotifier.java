package com.pawelmaslyk.sonar.gerritintegration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

public class GerritNotifier extends BuildBreaker {
	
	private final Settings settings;

	public GerritNotifier(Settings settings){
		this.settings = settings;
	}
	
	public void executeOn(Project project, SensorContext context) {
		analyseMeasures(context, LoggerFactory.getLogger(getClass()));
	}

	protected void analyseMeasures(SensorContext context, Logger logger) {
		Map<String, String> properties = settings.getProperties();
		logger.info("Just a message");
		
		logger.info(properties.toString());
	}
}
