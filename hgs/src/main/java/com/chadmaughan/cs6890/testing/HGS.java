package com.chadmaughan.cs6890.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.chadmaughan.cs6890.testing.model.Branch;

/**
 * Implementation of algorithm for Hitting Set using the HGS algorithm (Harrold, Gupta, Soffa)
 * @author Chad Maughan
 */
public class HGS {

	private static Logger logger = Logger.getLogger(HGS.class.getName());
	
	// keeps track of the maximum cardinality of the branches
	private int maximumCardinality = -1;

	private Map<Integer,Branch> data;

	/**
	 * @param input Absolute path of the input file to be processed (ex '-f /tmp/input.txt')
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
				System.out.println(System.getProperty("line.separator") + "Missing input file.  See usage below." + System.getProperty("line.separator"));
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "HSG", options );
				System.out.println(System.getProperty("line.separator") + "example: java -jar chadmaughan-hw1.jar -f input-file.txt" + System.getProperty("line.separator"));
			}
		}
		catch (ParseException e) {
			logger.log(Level.SEVERE, "Error parsing command line option", e);
		}		
	}

	public HGS(String input) {

		// use a hash map to store the data imported from the file
		data = new TreeMap<Integer,Branch>();

		// representative set of selected tests (from all testing sets)
		SortedSet<Integer> representativeSet = new TreeSet<Integer>();
		SortedSet<Integer> allSet = new TreeSet<Integer>();
		
		// keeps track of the current cardinality we're processing (starts with 1)
		int currentCardinality = 1;
		
		// read in the input file into the data structures
		BufferedReader in = null;
		try {
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Reading file: " + input);
			
		    in = new BufferedReader(new FileReader(input));

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
		    			
		    			// keep track of total unique sets for reduction calculations
		    			allSet.add(number);
		    		}
		    		
		    		column++;
		    	}
		    }
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading input file: " + input, e);
		}
		finally {
		    try {
				in.close();
			}
		    catch (IOException e) {
				logger.log(Level.SEVERE, "Error closing input file: " + input, e);
			}
		}
		
    	// get the maximum cardinality (so we know when to stop)
	    for(Branch branch : data.values()) {
	    	if(branch.getTests().size() > maximumCardinality)
	    		maximumCardinality = branch.getTests().size();
		}

		if(logger.isLoggable(Level.INFO))
			logger.info("Maximum cardinality of testing sets: " + maximumCardinality);
		
		// first process all branches that have exactly one test
		for(Branch branch : data.values()) {

			// only process those that have cardinality of 1
			//  break out of loop for efficiency
			if(branch.getCardinality() > 1) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Breaking from single cardinality on testing set (requirement) number: " + branch.getNumber() + ", " + branch.getTests());
				break;
			}
			else {
				branch.setMarked(true);
				representativeSet.addAll(branch.getTests());
				if(logger.isLoggable(Level.INFO))
					logger.info("Adding tests: " + branch.getTests());
			}
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Representative tests after single cardinality insert: " + representativeSet);
		
		for(Branch ts : data.values()) {
			logger.info(ts.getNumber() + ", " + ts.getTests());
		}
		
		// now work your way up increasing cardinality by 1 until you've processed all branches
		while(currentCardinality < maximumCardinality) {

			// only increment to next cardinality if all branches of the current cardinality are satisfied
			boolean allSatisfied = true;
			
			for(Branch branch : data.values()) {
				if(branch.getCardinality() == currentCardinality) {
					if(!branch.isMarked()) {
						allSatisfied = false;
					}
				}
			}

			if(allSatisfied) {
				// on first loop, should be two as all single cardinality tests are already processed
				currentCardinality++;
			}
			else {
				if(logger.isLoggable(Level.INFO))
					logger.info("Staying on current cardinality as all testing sets are not satisfied: " + currentCardinality);
			}
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Processing cardinality: " + currentCardinality);

			SortedSet<Integer> tests = new TreeSet<Integer>();
			for(Branch branch : data.values()) {
				if(branch.getCardinality() == currentCardinality && branch.isMarked() == false) {
					if(logger.isLoggable(Level.INFO))
						logger.info("Testing set (requirement) number " + branch.getNumber() + " has matching cardinality and is not marked, adding tests: " + branch.getTests());
					tests.addAll(branch.getTests());
				}
			}

			// there might not be any testing sets of the cardinality we're currently working on
			if(tests.size() > 0) {
				
				int nextTest = selectTest(currentCardinality, tests);
				representativeSet.add(nextTest);
				if(logger.isLoggable(Level.INFO))
					logger.info("Added selected test " + nextTest + ", representative test set now: " + representativeSet);
				
				boolean mayReduce = false;
				for(Branch branch : data.values()) {
					if(branch.getTests().contains(nextTest)) {
						branch.setMarked(true);
						if(branch.getCardinality() == maximumCardinality) {
							mayReduce = true;
						}
					}
				}
				
				if(mayReduce) {
					
					if(logger.isLoggable(Level.INFO))
						logger.info("Attempting to reduce");
					
					int max = 0;
					for(Branch branch : data.values()) {
						if(!branch.isMarked()) {
							if(branch.getCardinality() > max) {
								max = branch.getCardinality();
								if(logger.isLoggable(Level.INFO))
									logger.info("New max: " + max + " from testing set (requirement): " + branch.getNumber());
							}
						}
					}
					
					maximumCardinality = max;
					if(logger.isLoggable(Level.INFO))
						logger.info("New maximum cardinality set: " + maximumCardinality);
				}
			}
			else {
				if(logger.isLoggable(Level.INFO))
					logger.info("No testing sets of cardinality: " + currentCardinality);
			}
		}

		System.out.println("");
		System.out.println("Branches: " + allSet.size() + " (unique count) - " + allSet);
		System.out.println("Representative set: " + representativeSet.size() + " (reduced size) - " + representativeSet);
	}
	
	private Integer selectTest(int cardinality, SortedSet<Integer> tests) {
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Working with cardinality " + cardinality + " and tests: " + tests);
		
		Map<Integer,Integer> counts = new HashMap<Integer, Integer>();
		
		// keep track of the test that has the most 
		int maxTestingSetCoverageCount = -1;
		
		// get the counts of each individual test
		for(Integer i : tests) {

			int currentTestingSetCoverageCount = 0;
			if(counts.get(i) == null) {
				currentTestingSetCoverageCount = 1;
			}
			else {
				currentTestingSetCoverageCount = counts.get(i) + 1;
			}
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Branch coverage count for test: " + i + " now at: " + currentTestingSetCoverageCount);
			counts.put(i, currentTestingSetCoverageCount);

			if(currentTestingSetCoverageCount > maxTestingSetCoverageCount)
				maxTestingSetCoverageCount = currentTestingSetCoverageCount;
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Maximum branch coverage count for cardinality " + cardinality + ": " + maxTestingSetCoverageCount);
		
		SortedSet<Integer> testList = new TreeSet<Integer>();
		
		for(int key : counts.keySet()) {
			if(counts.get(key) == maxTestingSetCoverageCount) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Branch: " + key + " has individual test count: " + maxTestingSetCoverageCount);
				testList.add(key);
			}
		}
		
		if(testList.size() == 1) {
			return testList.first();
		}
		else if(cardinality == maximumCardinality) {
			// "random" for this algorithm implementation takes first in test list
			return testList.first();
		}
		else {
			return selectTest(cardinality + 1, testList);
		}
	}
}
