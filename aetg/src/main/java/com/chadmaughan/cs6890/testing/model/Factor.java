package com.chadmaughan.cs6890.testing.model;

import java.util.List;

public class Factor {

	private int number;
	private List<Integer> levels;

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
}