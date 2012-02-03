package com.chadmaughan.cs6890.testing.model;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Candidate {

	private int number;
	private SortedMap<Integer,Integer> tests;
	
	public Candidate(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}

	public SortedMap<Integer,Integer> getTests() {
		return tests;
	}

	public void setTests(SortedMap<Integer,Integer> tests) {
		this.tests = tests;
	}

	public void addTest(int test, List<Factor> factors) {
		if(this.getTests() == null)
			this.tests = new TreeMap<Integer,Integer>();
		
		for(Factor f : factors) {
			if(f.getLevels().contains(test)) {
				this.tests.put(f.getNumber(), test);
			}
		}
	}
	
	public String toString() {
		return number + " " + tests.values();
	}
}
