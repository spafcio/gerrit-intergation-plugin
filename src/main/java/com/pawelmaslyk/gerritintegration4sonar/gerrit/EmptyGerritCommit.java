package com.pawelmaslyk.gerritintegration4sonar.gerrit;

public class EmptyGerritCommit extends GerritCommit {

	EmptyGerritCommit(String projectName, String change, String patch) {
		super(projectName, change, patch);
	}
	
	@Override
	public String toString() {	
		return "No commit information";
	}

}
