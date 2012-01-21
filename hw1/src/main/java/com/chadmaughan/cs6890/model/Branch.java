package com.chadmaughan.cs6890.model;

import java.util.Set;
import java.util.SortedSet;

public class Branch implements Comparable<Branch> {

	/**
	 * Each branch of code is referenced by an integer in
	 * columns of each line of the input file
	 * 
	 * Example line: (numbers = 21, 29, 18 ... 38)
	 * 1	21	29	18	30	31	32	33	18	34	18	1	3	1	4	35	36	37	38
	 */
	private int number;

	/**
	 * Tests are also referenced by an integer in column = 1
	 * in each line of the input file
	 * 
	 * Example line: (tests is 5)
	 * 5	51	46	24	21	24	25
	 */
	private SortedSet<Integer> tests;
	
	/**
	 * Whether the testing set has been satisfied 
	 */
	private boolean marked;
	
	public Branch() {	
	}
	
	public Branch(int number, SortedSet<Integer> tests) {
		this.number = number;
		this.tests = tests;
	}
	
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
	
	public Set<Integer> getTests() {
		return tests;
	}
	
	public void setTests(SortedSet<Integer> tests) {
		this.tests = tests;
	}

	public int compareTo(Branch other) {
		
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
