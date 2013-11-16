package com.pawelmaslyk.gerritintegration4sonar.sonar;

import com.pawelmaslyk.gerritintegration4sonar.gerritconfiguration.GerritConnection;

/**
 * I represent sonar result analysis
 * 
 * @author pawel
 * 
 */
public enum SonarAnalysisResult {

	/**
	 * There were no alerts during analysis
	 */
	NO_PROBLEMS(GerritConnection.VOTE_POSITIVE, "Static analysis verification passed.\nResults detail: "),
	/**
	 * There were alerts at warning level during analysis
	 */
	WARNINGS(GerritConnection.VOTE_NEUTRAL, "Static analysis generated warnings.\nResults detail: "),
	/**
	 * There were alerts at error level during analysis
	 */
	ERRORS(GerritConnection.VOTE_NEGATIVE, "Static analisys generated error alerts.\nResults detail: ");

	private final String voteKey;
	private final String message;

	private SonarAnalysisResult(String voteKey, String message) {
		this.voteKey = voteKey;
		this.message = message;
	}

	public String getVoteKey() {
		return voteKey;
	}

	public String getMessage() {
		return message;
	}

}
