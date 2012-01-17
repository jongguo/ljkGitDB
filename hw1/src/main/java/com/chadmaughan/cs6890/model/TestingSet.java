package com.chadmaughan.cs6890.model;

import java.util.List;

public class TestingSet implements Comparable<TestingSet> {

	/**
	 * Each testing set is referenced by an integer in
	 * the first column of each line of the input file
	 * 
	 * Example line: (number = 1)
	 * 1	21	29	18	30	31	32	33	18	34	18	1	3	1	4	35	36	37	38
	 */
	private int number;

	/**
	 * Tests are also referenced by an integer in columns > 2
	 * in each line of the input file
	 * 
	 * Example line: (tests are 51,46,24,21,24,25)
	 * 5	51	46	24	21	24	25
	 */
	private List<Integer> tests;
	
	/**
	 * Whether the testing set 
	 */
	private boolean marked;
	
	/**
	 * Returns the count (int) of the number of tests
	 * in this testing set.  Equivalent of this.tests.size()
	 * @return count of tests
	 */
	public int getCardinality() {
		if(tests != null) {
			if(tests.size() > 0) {
				return tests.size();
			}
		}
		return 0;
	}
	
	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public List<Integer> getTests() {
		return tests;
	}
	
	public void setTests(List<Integer> tests) {
		this.tests = tests;
	}

	public int compareTo(TestingSet other) {
		
		if(this.tests.size() > other.tests.size()) {
			return 1;
		}
		else if(this.tests.size() < other.tests.size()) {
			return -1;
		}
		else {
			return 0;
		}
	}	
}
