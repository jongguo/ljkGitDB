package com.chadmaughan.cs6890.testing.model;

import static org.junit.Assert.*;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class TestTest {

	@Test
	public void test() {
		SortedSet<Integer> levels = new TreeSet<Integer>();
		levels.add(1);
		levels.add(2);
		
		com.chadmaughan.cs6890.testing.model.Test test = new com.chadmaughan.cs6890.testing.model.Test(levels);
		System.out.println(test);
		
		com.chadmaughan.cs6890.testing.model.Test other = new com.chadmaughan.cs6890.testing.model.Test(levels);
		System.out.println(other);

		System.out.println(test.getLevels().containsAll(other.getLevels()));
		System.out.println(test.equals(other));
		
		System.out.println(test.compareTo(other));
		System.out.println(other.compareTo(test));

		assertEquals(test, other);
	}

}
