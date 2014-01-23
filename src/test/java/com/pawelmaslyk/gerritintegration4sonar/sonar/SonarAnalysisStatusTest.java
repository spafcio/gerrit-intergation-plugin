package com.pawelmaslyk.gerritintegration4sonar.sonar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SonarAnalysisStatusTest {

	@Test
	public void NO_PROBLEMSIsPlusOne() {
		// given
		SonarAnalysisStatus result = SonarAnalysisStatus.NO_PROBLEMS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(1, approval);
	}

	@Test
	public void WARNIsPlusOne() {
		// given
		SonarAnalysisStatus result = SonarAnalysisStatus.WARNINGS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(-1, approval);
	}

	@Test
	public void ERRORIsPlusOne() {
		// given
		SonarAnalysisStatus result = SonarAnalysisStatus.ERRORS;

		// when
		int approval = result.getApproval();

		// then
		assertEquals(-2, approval);
	}

}
