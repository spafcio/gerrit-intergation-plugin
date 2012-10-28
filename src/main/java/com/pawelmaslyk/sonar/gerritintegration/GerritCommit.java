package com.pawelmaslyk.sonar.gerritintegration;

public class GerritCommit {

	public static final String GERRIT_PROJECT_KEY = "gerrit.project";
	public static final String GERRIT_CHANGE_KEY = "gerrit.change";
	public static final String GERRIT_PATCH_KEY = "gerrit.patch";
	
	private final String projectName;
	private final String change;
	private final String patch;
	
	public GerritCommit(String projectName, String change, String patch) {
		super();
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
