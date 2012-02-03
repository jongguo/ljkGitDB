package com.chadmaughan.cs6890.testing.model;

import java.util.SortedSet;

public class Test implements Comparable<Test> {

	private int coverage;
	private SortedSet<Integer> levels;
	private SortedSet<Integer> factors;
	private boolean marked;
	
	public Test(SortedSet<Integer> levels) {
		this.levels = levels;
	}
	
	public int getCoverage() {
		return coverage;
	}
	
	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}
	
	public SortedSet<Integer> getLevels() {
		return levels;
	}
	
	public void setLevels(SortedSet<Integer> levels) {
		this.levels = levels;
	}
	
	public SortedSet<Integer> getFactors() {
		return factors;
	}

	public void setFactors(SortedSet<Integer> factors) {
		this.factors = factors;
	}

	public boolean isMarked() {
		return marked;
	}
	
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	@Override
	public int compareTo(Test otherTest) {
		if(this.getLevels().equals(otherTest.getLevels())) {
			return 0;
		}
		else {
			return -1;
		}
	}
}