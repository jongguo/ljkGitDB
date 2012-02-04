package com.chadmaughan.cs6890.testing.model;

import java.util.SortedSet;

public class Test implements Comparable<Test> {

	private int coverage;
	private SortedSet<Integer> levels;
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
	
	public boolean isMarked() {
		return marked;
	}
	
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public String toString() {
		return this.levels.toString();
	}
	
	@Override
	public boolean equals(Object object) {
	    if(!(object instanceof Test)) 
	    	 return false;

		if(this.compareTo(((Test) object)) == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Test otherTest) {
		
		if(this.getLevels().containsAll(otherTest.getLevels())) {
			return 0;
		}
		else {
			return -1;
		}
	}
}