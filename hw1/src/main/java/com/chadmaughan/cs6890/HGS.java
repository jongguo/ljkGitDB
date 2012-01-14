package com.chadmaughan.cs6890;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class HGS {

	private static Logger logger = Logger.getLogger(HGS.class.getName());
	
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

		// representative set of selected tests
		Set<Integer> representativeSet = new TreeSet<Integer>();
		
		// keeps track of the maximum cardinality of the testing sets
		int maximumCardinality = -1;

		// keeps track of the current cardinality we're processing (starts with 1)
		int currentCardinality = 1;
		
		// read in the input file into the data structures
		try {
		    BufferedReader in = new BufferedReader(new FileReader(input));

		    String line;
		    while ((line = in.readLine()) != null) {

		    	StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		    	int column = 0;
		    	
		    	// each line is a testing set (T_n) for requirement (r_n) contains multiple tests (t_1 ... t_n)
		    	TestingSet testingSet = new TestingSet();
		    	testingSet.setTests(new ArrayList<Integer>());

		    	while (tokenizer.hasMoreTokens()) {

		    		// first column in input files is the requirement number
		    		if(column == 0) {
		    			int number = Integer.parseInt(tokenizer.nextToken());
				    	testingSet.setNumber(number);
		    			if(logger.isLoggable(Level.INFO))
		    				logger.info("Parsed testing set (requirement) number: " + testingSet.getNumber());
		    		}
		    		else {
		    			// add each test to the collection of 
		    			testingSet.getTests().add(Integer.parseInt(tokenizer.nextToken()));
		    		}
		    		
		    		column++;
		    	}
    			
		    	// track the maximum cardinality (so we know when to stop)
		    	if(testingSet.getTests().size() > maximumCardinality)
		    		maximumCardinality = testingSet.getTests().size();
		    	
		    	if(logger.isLoggable(Level.INFO))
    				logger.info("Test count: " + testingSet.getTests().size());

    			data.add(testingSet);
			}
		
		    in.close();
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading input file: " + input, e);
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Maximum cardinality: " + maximumCardinality);
		
		// sort the testing sets by cardinality
		Collections.sort(data);
		
		// first process all testing sets that have exactly one test
		for(TestingSet ts : data) {

			// only process those that have cardinality of 1
			//  break out of loop for efficiency
			if(ts.getTests().size() > 1) {
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
			logger.info("Representative tests: " + representativeSet);
		
		// now work your way up increasing cardinality by 1 until you've processed all testing sets
		while(currentCardinality < maximumCardinality) {
			if(logger.isLoggable(Level.INFO))
				logger.info("Processing cardinality: " + currentCardinality);

			currentCardinality++;
		}
	}
	
	private Integer selectTest(int cardinality, List<Integer> tests) {
		return 0;
	}
}
