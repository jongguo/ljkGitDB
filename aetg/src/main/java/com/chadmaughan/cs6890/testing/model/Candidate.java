package com.chadmaughan.cs6890.testing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Candidate {

	private int number;
	
	// <Factor Number, Factor Level>
	private SortedMap<Integer,Integer> tests;	

	private int coveredTests = 0;

	public Candidate(int number, int factorSize) {
		this.number = number;

		// set up the factors
		this.tests = new TreeMap<Integer,Integer>();
		for(int i = 0; i < factorSize; i++) {
			this.tests.put(i, null);
		}
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}

	public int getCoveredTests() {
		return coveredTests;
	}

	public void setCoveredTests(int coveredTests) {
		this.coveredTests = coveredTests;
	}

	public SortedMap<Integer,Integer> getTests() {
		return tests;
	}

	public void setTests(SortedMap<Integer,Integer> tests) {
		this.tests = tests;
	}
	
	public void addTest(int test, List<Factor> factors) {
		SortedSet<Integer> tests = new TreeSet<Integer>();
		tests.add(test);
		this.addTests(tests, factors);
	}
	
	public void addTests(SortedSet<Integer> tests, List<Factor> factors) {
		if(this.getTests() == null)
			this.tests = new TreeMap<Integer,Integer>();

		for(int test : tests) {
			for(Factor f : factors) {
				if(f.getLevels().contains(test)) {
					this.tests.put(f.getNumber(), test);
				}
			}
		}
	}
	
	public String toString() {
		return number + " " + tests.values();
	}

	public List<Integer> getUncoveredFactors() {

		List<Integer> uncoveredFactors = new ArrayList<Integer>();
		for(int i : this.tests.keySet()) {
			if(this.tests.get(i) == null)
				uncoveredFactors.add(i);
		}
		return uncoveredFactors;
	}

	public int getRandomFactor() {

		List<Integer> uncoveredFactors = new ArrayList<Integer>();
		for(int i : this.tests.keySet()) {
			if(this.tests.get(i) == null)
				uncoveredFactors.add(i);
		}
		int r = (int) (Math.random() * uncoveredFactors.size());
		return uncoveredFactors.get(r);
	}
	
	public void incrementCoveredTests() {
		this.coveredTests = this.coveredTests + 1;
	}
	
	public List<Test> twoWayTests() {

		List<Test> tests = new ArrayList<Test>();
			
		for(int i : this.tests.values()) {
			for(int j : this.tests.values()) {
				if(j != i) {
					SortedSet<Integer> l = new TreeSet<Integer>();
					l.add(i);
					l.add(j);

					Test test = new Test(l);
					if(!tests.contains(test))
						tests.add(test);
				}
			}
		}

		return tests;
	}
	
	public List<Test> threeWayTests() {

		List<Test> tests = new ArrayList<Test>();
		
		for(int i : this.tests.values()) {
			for(int j : this.tests.values()) {
				for(int k : this.tests.values()) {
					if(i != j && i != k && j != k) {
						SortedSet<Integer> l = new TreeSet<Integer>();
						l.add(i);
						l.add(j);
						l.add(k);
	
						Test test = new Test(l);
						tests.add(test);
					}
				}
			}
		}

		return tests;
			
	}

}