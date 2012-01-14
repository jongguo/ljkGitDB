package com.chadmaughan.cs6890;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
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
			options.addOption("f", false, "input file");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			String f = cmd.getOptionValue("f");

			if(f == null) {
				System.out.println("You must enter an input file with '-f input.txt'");
			}
			else {
				new HGS(f);
			}
		}
		catch (ParseException e) {
			logger.log(Level.SEVERE, "Error parsing command line option", e);
		}		
	}

	public HGS(String input) {

		// use a hash map to store the data imported from the file
		Map<Integer, List<Integer>> data = new HashMap<Integer, List<Integer>>();
		
		// read in the input file into the data structure
		try {
		    BufferedReader in = new BufferedReader(new FileReader(input));

		    String line;
		    while ((line = in.readLine()) != null) {

		    	List<Integer> tests = new ArrayList<Integer>();
		    	
		    	StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		    	int column = 0;
		    	int requirement = 0;
		    	while (tokenizer.hasMoreTokens()) {

		    		if(column == 0) {
		    			requirement = Integer.parseInt(tokenizer.nextToken());
		    			if(logger.isLoggable(Level.INFO))
		    				logger.info("Parsed requirement: " + requirement);
		    		}
		    		else {
		    			// add each test to the collection of 
		    			tests.add(Integer.parseInt(tokenizer.nextToken()));
		    		}
		    	}
    			
		    	if(logger.isLoggable(Level.INFO))
    				logger.info("Test count: " + tests.size());

    			data.put(requirement, tests);
			}
		
		    in.close();
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading input file: " + input, e);
		}
	}
}
