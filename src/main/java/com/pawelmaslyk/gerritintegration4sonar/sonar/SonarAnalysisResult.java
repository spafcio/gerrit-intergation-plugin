package com.pawelmaslyk.gerritintegration4sonar.sonar;

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
	NO_PROBLEMS(1),
	/**
	 * There were alerts at warning level during analysis
	 */
	WARNINGS(-1),
	/**
	 * There were alerts at error level during analysis
	 */
	ERRORS(-2);

	private final int approval;

	private SonarAnalysisResult(int approval) {
		this.approval = approval;
	}

	/**
	 * 
	 * @return the approval rate
	 */
	public int getApproval() {
		return approval;
	}
}
