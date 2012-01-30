package com.chadmaughan.cs6890.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Implementation of the AETG algorithm that generates combinatorial test suites.
 * @author Chad Maughan
 */
public class AETG {

	private static Logger logger = Logger.getLogger(AETG.class.getName());
	
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
				new AETG(f);
			}
			else {
				System.out.println(System.getProperty("line.separator") + "Missing input file.  See usage below." + System.getProperty("line.separator"));
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "AETG", options );
				System.out.println(System.getProperty("line.separator") + "example: java -jar chadmaughan-hw2.jar -f /tmp/input.txt" + System.getProperty("line.separator"));
			}
		}
		catch (ParseException e) {
			logger.log(Level.SEVERE, "Error parsing command line option", e);
		}		
	}

	public AETG(String input) {
		
		// covering array coverage - defaults to 2 way coverage
		int coverage = 2;
		int factors = 0;

		// read in the input file into the data structures
		BufferedReader in = null;
		try {
			
			if(logger.isLoggable(Level.INFO))
				logger.info("Reading file: " + input);
			
		    in = new BufferedReader(new FileReader(input));

		    String line;
		    int lineCount = 0;
		    while ((line = in.readLine()) != null) {
		    	switch(lineCount) {
			    	case 0:
			    		// line 0 specifies the coverage 
			    		coverage = Integer.parseInt(line.trim());
			    		if(logger.isLoggable(Level.INFO))
			    			logger.info("Set covering array coverage: " + coverage);
			    		break;
			    	case 1:
			    		// line 1 specifies the factors
			    		factors = Integer.parseInt(line.trim());
			    		if(logger.isLoggable(Level.INFO))
			    			logger.info("Set factors: " + factors);
			    		break;
		    		default:
		    			line = line.trim();

		    			// make sure it isn't an empty line or the 'end of file delimiter' = 0
		    			if(line.length() > 0 && !"0".equals(line)) {

		    				int levels = 0;
		    				int factor = 0;
		    				
		    				// split by white space
		    				String[] parts = line.split("\\s");
		    				if(parts.length > 0) {
		    					levels = Integer.parseInt(parts[0]);
					    		if(logger.isLoggable(Level.INFO))
					    			logger.info("Set levels: " + levels);
		    				}
		    				
		    				if(parts.length > 1) {
		    					factor = Integer.parseInt(parts[1]);
					    		if(logger.isLoggable(Level.INFO))
					    			logger.info("Set factor: " + factor + " for level: " + levels);
		    				}
		    			}
		    			break;
		    	}
		    	lineCount++;
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
	}
}
