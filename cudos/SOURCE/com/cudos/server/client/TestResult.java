package com.cudos.server.client;

import java.util.*;
/**
 * One test result.
 */

public class TestResult {
	public int score;
	public int maximum;
	public String testName;
	public Date date;

	public TestResult(String testName, int score, int maximum, Date date) {
		this.testName = testName;
		this.score = score;
		this.maximum = maximum;
		this.date = date;
	}


	/**
	 * Return the test result in the format:
	 * Mon Nov 11 13:30:00 GMT 2002: Coronal Sections 11 - 27/30
	 */
	public String toString(){
		return date + ": " + testName + " - " + score + "/" + maximum;
	}
}