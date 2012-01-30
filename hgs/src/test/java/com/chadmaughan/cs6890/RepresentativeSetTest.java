package com.chadmaughan.cs6890;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chadmaughan.cs6890.testing.model.Branch;

public class RepresentativeSetTest {

	private static Logger logger = Logger.getLogger(RepresentativeSetTest.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] tests = { 0, 1, 2, 3, 4, 7, 8, 10, 12, 14, 20, 24, 26, 27, 53 };
		new RepresentativeSetTest("/Users/chadmaughan/workspaces/school/cs6890-testing/hw1/src/main/resources/data/Input1C.txt", tests);
	}

	public RepresentativeSetTest(String input, int[] tests) {

		try {
			Map<Integer,Branch> data = new HashMap<Integer,Branch>();
	
		    BufferedReader in = new BufferedReader(new FileReader(input));

		    String line;
		    while ((line = in.readLine()) != null) {

			    // not all the input files where tab delimited, changed to white space regular expression
		    	String[] parts = line.split("\\s+");
		    	int column = 0;

		    	int testCase = -1;
		    	
		    	for(String part : parts) {

		    		part = part.trim();
		    		
	    			int number = Integer.parseInt(part);

		    		// first column in input files is the requirement number
		    		if(column == 0) {
				    	testCase = number;
		    			if(logger.isLoggable(Level.INFO))
		    				logger.info("Parsed testCase number: " + testCase);
		    		}
		    		else {
		    			
		    			Branch branch = data.get(number);
		    			if(branch == null) {
		    				branch = new Branch(number, new TreeSet<Integer>());
		    				data.put(number, branch);
		    			}
		    			
		    			// add each test to the collection of 
		    			branch.getTests().add(testCase);
		    			
		    		}
		    		
		    		column++;
		    	}
		    }
		    		
		    in.close();
		    
			// check each test suite to make sure it is covered by 
			// one of the representative tests
		    Map<Integer,Boolean> coverage = new HashMap<Integer,Boolean>();
		    int coveredCount = 0;
		    int notCoveredCount = 0;
		    
		    for(Branch branch : data.values()) {
		    	boolean covered = false;

		    	for(int i : tests) {
		    		if(branch.getTests().contains(i)) {
		    			covered = true;
		    			break;
		    		}
		    	}

		    	coverage.put(branch.getNumber(), covered);
		    	
		    	if(covered) {
		    		coveredCount++;
		    	}
		    	else {
		    		notCoveredCount++;
		    	}
		    }
		    
		    if(logger.isLoggable(Level.INFO)) {
		    	for(Integer key : coverage.keySet()) {
		    		if(!coverage.get(key)) {
		    			logger.info("Key coverage missing for test: " + key);
		    		}
		    	}
		    	
		    	logger.info("-------------------------------------");
		    	
		    	logger.info("Covered count: " + coveredCount);
		    	logger.info("Not covered count: " + notCoveredCount);
		    }
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error testing representative set: " + input, e);
		}
	}
}
