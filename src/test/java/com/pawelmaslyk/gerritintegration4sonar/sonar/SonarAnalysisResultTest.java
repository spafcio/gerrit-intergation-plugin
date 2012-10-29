package com.pawelmaslyk.gerritintegration4sonar.sonar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SonarAnalysisResultTest {

	@Test
	public void NO_PROBLEMSIsPlusOne() {
		// given
		SonarAnalysisResult result = SonarAnalysisResult.NO_PROBLEMS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(1, approval);
	}

	@Test
	public void WARNIsPlusOne() {
		// given
		SonarAnalysisResult result = SonarAnalysisResult.WARNINGS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(-1, approval);
	}

	@Test
	public void ERRORIsPlusOne() {
		// given
		SonarAnalysisResult result = SonarAnalysisResult.ERRORS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(-2, approval);
	}

}
