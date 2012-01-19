package com.chadmaughan.cs6890;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chadmaughan.cs6890.model.TestingSet;

public class RepresentativeSetTest {

	private static Logger logger = Logger.getLogger(RepresentativeSetTest.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public RepresentativeSetTest(String input, int[] tests) {

		try {
			List<TestingSet> data = new ArrayList<TestingSet>();
	
			// read in the file
		    BufferedReader in = new BufferedReader(new FileReader(input));
	
		    String line;
		    while ((line = in.readLine()) != null) {
	
			    // not all the input files where tab delimited, changed to white space regular expression
		    	String[] parts = line.split("\\s+");
		    	int column = 0;
		    	
		    	// each line is a testing set (T_n) for requirement (r_n) contains multiple tests (t_1 ... t_n)
		    	TestingSet testingSet = new TestingSet();
		    	testingSet.setTests(new ArrayList<Integer>());
	
		    	for(String part : parts) {
	
		    		part = part.trim();
		    		
	    			int number = Integer.parseInt(part);
	
		    		// first column in input files is the requirement number
		    		if(column == 0) {
				    	testingSet.setNumber(number);
		    			if(logger.isLoggable(Level.INFO))
		    				logger.info("Parsed testing set (requirement) number: " + testingSet.getNumber());
		    		}
		    		else {
		    			// add each test to the collection of 
		    			testingSet.getTests().add(number);	    			
		    		}
		    		
		    		column++;
		    	}
				
				data.add(testingSet);
			}
		
		    in.close();
		    
			// check each test suite to make sure it is covered by 
			// one of the representative tests
		    Map<Integer,Boolean> coverage = new HashMap<Integer,Boolean>();
		    int coveredCount = 0;
		    int notCoveredCount = 0;
		    
		    for(TestingSet ts : data) {
		    	boolean covered = false;

		    	for(int i : tests) {
		    		if(ts.getTests().contains(i)) {
		    			covered = true;
		    			break;
		    		}
		    	}

		    	coverage.put(ts.getNumber(), covered);
		    	
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
