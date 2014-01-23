package com.pawelmaslyk.gerritintegration4sonar.sonar;

/**
 * Contains the result of a SonarQube analysis
 * 
 * @author deiwin
 */
public class SonarAnalysisResult {

	/**
	 * Human-readable result of the analysis
	 */
	private final String message;

	/**
	 * The end status of the analysis
	 */
	private final SonarAnalysisStatus status;

	public SonarAnalysisResult(String message, SonarAnalysisStatus status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public SonarAnalysisStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "SonarAnalysisResult [message=" + message + ", status=" + status + "]";
	}
}
