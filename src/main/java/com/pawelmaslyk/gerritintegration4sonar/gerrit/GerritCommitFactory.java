package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;

import com.google.common.base.Strings;

/**
 * I'm {@link GerritCommit} Factory
 * 
 * @author pawel
 * 
 */
public class GerritCommitFactory {

	static Logger logger = LoggerFactory.getLogger(GerritCommitFactory.class);

	/**
	 * Create GerritCommit from Sonar settings
	 * 
	 * @param settings
	 *            sonar settings
	 * 
	 * @return gerrit commit information
	 */
	public static GerritCommit createGerritCommitFromSonarSettings(Settings settings) {

		String projectName = settings.getString(GerritCommit.GERRIT_PROJECT_KEY);
		String change = settings.getString(GerritCommit.GERRIT_CHANGE_KEY);
		String patch = settings.getString(GerritCommit.GERRIT_PATCH_KEY);

		return createGerritCommitFromSonarSettings(projectName, change, patch);
	}

	/**
	 * Create GerritCommit from Sonar settings
	 * 
	 * @param projectName
	 *            gerrit project name
	 * @param change
	 *            gerrit change
	 * @param patch
	 *            gerrit patch
	 * @return gerrit commit information
	 */
	public static GerritCommit createGerritCommitFromSonarSettings(String projectName, String change, String patch) {
		if (requiredParametersAreSet(projectName, change, patch)) {
			return new GerritCommit(projectName, change, patch);
		} else {
			return new EmptyGerritCommit(projectName, change, patch);
		}
	}

	private static boolean requiredParametersAreSet(String projectName, String change, String patch) {
		boolean requiredParametersSet = true;

		if (Strings.isNullOrEmpty(projectName)) {
			logger.error(GerritCommit.GERRIT_PROJECT_KEY + " is empty");
			requiredParametersSet = false;
		}
		if (Strings.isNullOrEmpty(change)) {
			logger.error(GerritCommit.GERRIT_CHANGE_KEY + " is empty");
			requiredParametersSet = false;
		}
		if (Strings.isNullOrEmpty(patch)) {
			logger.error(GerritCommit.GERRIT_PATCH_KEY + " is empty");
			requiredParametersSet = false;
		}

		return requiredParametersSet;
	}
}
