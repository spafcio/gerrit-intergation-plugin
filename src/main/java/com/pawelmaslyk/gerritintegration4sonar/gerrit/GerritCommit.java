package com.pawelmaslyk.gerritintegration4sonar.gerrit;

/**
 * I gather the information about the gerrit commit to be reviewed by sonar .
 * @author pawel
 *
 */
public class GerritCommit {

	/**
	 * Key to obtain gerrit project name
	 */
	public static final String GERRIT_PROJECT_KEY = "gerrit.project";
	
	/**
	 * Key to obtain gerrit change
	 */
	public static final String GERRIT_CHANGE_KEY = "gerrit.change";
	
	/**
	 * Key to obtain gerrit patch
	 */
	public static final String GERRIT_PATCH_KEY = "gerrit.patch";

	private final String projectName;
	private final String change;
	private final String patch;

	GerritCommit(String projectName, String change, String patch) {
		this.projectName = projectName;
		this.change = change;
		this.patch = patch;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getChange() {
		return change;
	}

	public String getPatch() {
		return patch;
	}

	@Override
	public String toString() {
		return "GerritCommit [projectName=" + projectName + ", change=" + change + ", patch=" + patch + "]";
	}
}
