package com.chadmaughan.cs6890.testing.model;

import java.util.List;

public class Factor implements Comparable<Factor> {

	private int number;
	private List<Integer> levels;

	public Factor() {
	}

	public Factor(int number) {
		this.number = number;
	}
		
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<Integer> getLevels() {
		return levels;
	}

	public void setLevels(List<Integer> levels) {
		this.levels = levels;
	}
	
	public String toString() {
		return this.getNumber() + ":" + this.getLevels();
	}
	
	@Override
	public int compareTo(Factor otherFactor) {
		if(this.getNumber() == otherFactor.getNumber()) {
			return 0;
		}
		else if(this.getNumber() > otherFactor.getNumber()) {
			return 1;
		}
		else {
			return -1;
		}
	}
}