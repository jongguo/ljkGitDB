package com.chadmaughan.cs6890;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.chadmaughan.cs6890.model.TestingSet;

public class HGS {

	private static Logger logger = Logger.getLogger(HGS.class.getName());
	
	// keeps track of the maximum cardinality of the testing sets
	private int maximumCardinality = -1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			// add a command line option for file input name
			Options options = new Options();
			options.addOption("f", true, "input file to process");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			if(cmd.hasOption('f')) {
				String f = cmd.getOptionValue("f");
				new HGS(f);
			}
			else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "HSG", options );
			}
		}
		catch (ParseException e) {
			logger.log(Level.SEVERE, "Error parsing command line option", e);
		}		
	}

	public HGS(String input) {

		// use a hash map to store the data imported from the file
		List<TestingSet> data = new ArrayList<TestingSet>();

		// representative set of selected tests (from all testing sets)
		Set<Integer> representativeSet = new TreeSet<Integer>();
		
		// keeps track of the current cardinality we're processing (starts with 1)
		int currentCardinality = 1;
		
		// read in the input file into the data structures
		try {
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Reading file: " + input);
			
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
    			
		    	// track the maximum cardinality (so we know when to stop)
		    	if(testingSet.getTests().size() > maximumCardinality)
		    		maximumCardinality = testingSet.getTests().size();
		    	
		    	if(logger.isLoggable(Level.INFO))
    				logger.info("Tests: " + testingSet.getTests().size() + ", " + testingSet.getTests());

    			data.add(testingSet);
			}
		
		    in.close();
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading input file: " + input, e);
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Maximum cardinality of testing sets: " + maximumCardinality);
		
		// sort the testing sets by cardinality
		Collections.sort(data);
		
		// first process all testing sets that have exactly one test
		for(TestingSet ts : data) {

			// only process those that have cardinality of 1
			//  break out of loop for efficiency
			if(ts.getCardinality() > 1) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Breaking from single cardinality on testing set (requirement) number: " + ts.getNumber() + ", " + ts.getTests());
				break;
			}
			else {
				ts.setMarked(true);
				representativeSet.addAll(ts.getTests());
				if(logger.isLoggable(Level.INFO))
					logger.info("Adding tests: " + ts.getTests());
			}
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Representative tests after single cardinality insert: " + representativeSet);
		
		// now work your way up increasing cardinality by 1 until you've processed all testing sets
		while(currentCardinality < maximumCardinality) {

			// on first loop, should be two as all single cardinality tests are already processed
			currentCardinality++;

			if(logger.isLoggable(Level.INFO))
				logger.info("Processing cardinality: " + currentCardinality);

			// this needs to be a list (not a set) because we have to calculate the most used individual test
			List<Integer> tests = new ArrayList<Integer>();
			for(TestingSet ts : data) {
				if(ts.getCardinality() == currentCardinality && ts.isMarked() == false) {
					if(logger.isLoggable(Level.INFO))
						logger.info("Testing set (requirement) number " + ts.getNumber() + " has matching cardinality and is not marked, adding tests: " + ts.getTests());
					tests.addAll(ts.getTests());
				}
			}

			int nextTest = selectTest(currentCardinality, tests);
			representativeSet.add(nextTest);
			if(logger.isLoggable(Level.INFO))
				logger.info("Added selected test, representative test set now: " + representativeSet);
			
			boolean mayReduce = false;
			for(TestingSet ts : data) {
				if(ts.getTests().contains(nextTest)) {
					ts.setMarked(true);
					if(ts.getCardinality() == maximumCardinality) {
						mayReduce = true;
					}
				}
			}
			
			if(mayReduce) {
				
				if(logger.isLoggable(Level.INFO))
					logger.info("Attempting to reduce");
				
				int max = -1;
				for(TestingSet ts : data) {
					if(!ts.isMarked()) {
						if(ts.getCardinality() > max) {
							max = ts.getCardinality();
							if(logger.isLoggable(Level.INFO))
								logger.info("New max: " + max + " from testing set (requirement): " + ts.getNumber());
						}
					}
				}
				
				maximumCardinality = max;
				if(logger.isLoggable(Level.INFO))
					logger.info("New maximum cardinality set: " + maximumCardinality);
			}
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Representative set: " + representativeSet);
	}
	
	private Integer selectTest(int cardinality, List<Integer> tests) {
		
		Collections.sort(tests);
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Working with cardinality " + cardinality + " and tests: " + tests);
		
		Map<Integer,Integer> counts = new HashMap<Integer, Integer>();
		
		// keep track of the test that has the most 
		int maxCount = -1;
		
		// get the counts of each individual test
		for(Integer i : tests) {

			int currentCount = 0;
			if(counts.get(i) == null) {
				currentCount = 1;
			}
			else {
				currentCount = counts.get(i) + 1;
			}
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Test count for test: " + i + " now at: " + currentCount);
			counts.put(i, currentCount);

			if(currentCount > maxCount)
				maxCount = currentCount;
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Maximum individual test count for cardinality " + cardinality + ": " + maxCount);

		List<Integer> testList = new ArrayList<Integer>();
		
		for(int key : counts.keySet()) {
			if(counts.get(key) == maxCount) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Test set: " + key + " has individual test count: " + maxCount);
				testList.add(key);
			}
		}
		
		if(testList.size() == 1) {
			return testList.get(0);
		}
		else if(cardinality == maximumCardinality) {
			// TODO - change to random instead of first
			logger.warning("BREAKING TIE");
			return testList.get(0);
		}
		else {
			return selectTest(cardinality + 1, testList);
		}
	}
}
