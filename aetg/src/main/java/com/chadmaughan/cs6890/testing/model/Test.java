package com.chadmaughan.cs6890.testing.model;

import java.util.SortedSet;

public class Test implements Comparable<Test> {

	private String key;
	private int coverage;
	private SortedSet<Integer> levels;
	private boolean marked;
	
	public Test(SortedSet<Integer> levels) {
		this.setLevels(levels);
	}
	
	public int getCoverage() {
		return coverage;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}
	
	public SortedSet<Integer> getLevels() {
		return levels;
	}
	
	public void setLevels(SortedSet<Integer> levels) {
		StringBuffer key = new StringBuffer();
		for(Integer level : levels) {
			key.append(level);
			key.append('-');
		}
		this.key = key.substring(0, key.length() - 1);
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
		
		if(this.key.equals(otherTest.getKey())) {
			return 0;
		}
		else {
			return -1;
		}
	}
}