package com.pawelmaslyk.sonar.gerritintegration;

import org.sonar.api.config.Settings;

public class GerritCommitFactory {

	public static GerritCommit createGerritCommitFromSonarSettings(Settings settings) {
		
		String projectName = settings.getString(GerritCommit.GERRIT_PROJECT_KEY);
		String change = settings.getString(GerritCommit.GERRIT_CHANGE_KEY);
		String patch = settings.getString(GerritCommit.GERRIT_PATCH_KEY);
		
		return new GerritCommit(projectName, change, patch);
	}
}
