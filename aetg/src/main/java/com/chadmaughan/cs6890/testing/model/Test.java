package com.chadmaughan.cs6890.testing.model;

import java.util.List;

public class Test implements Comparable<Test> {

	private int coverage;
	private List<Integer> levels;
	private boolean marked;
	
	public Test(List<Integer> levels) {
		this.levels = levels;
	}
	
	public int getCoverage() {
		return coverage;
	}
	
	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}
	
	public List<Integer> getLevels() {
		return levels;
	}
	
	public void setLevels(List<Integer> levels) {
		this.levels = levels;
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