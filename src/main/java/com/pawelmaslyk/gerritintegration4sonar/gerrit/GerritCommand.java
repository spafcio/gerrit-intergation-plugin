package com.pawelmaslyk.gerritintegration4sonar.gerrit;

import com.pawelmaslyk.gerritintegration4sonar.sonar.SonarAnalysisResult;

/**
 * I represent a gerrit SSH command
 * 
 * @author pawel
 * 
 */
public class GerritCommand {

	private static final String GERRIT_CODE_REVIEW_COMMAND = "gerrit approve --project %s --message \"Sonar analysis\" --code-review %s %s,%s";

	/**
	 * Create a gerrit approve command for a commit given the sonar analysis results
	 * 
	 * @param commit
	 *            gerrit commit information
	 * @param mark
	 *            sonar mark
	 * @return gerrit ssh command to approve the commit
	 */
	public static String createCodeReview(GerritCommit commit, SonarAnalysisResult mark) {
		return String.format(GerritCommand.GERRIT_CODE_REVIEW_COMMAND, commit.getProjectName(), mark.getApproval(), commit.getChange(), commit.getPatch());
	}
}