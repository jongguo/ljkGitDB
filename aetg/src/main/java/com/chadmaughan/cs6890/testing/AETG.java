package com.chadmaughan.cs6890.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.chadmaughan.cs6890.testing.model.Factor;
import com.chadmaughan.cs6890.testing.model.Test;

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
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption('f')) {
				String f = cmd.getOptionValue("f");
				new AETG(f);
			} 
			else {
				System.out.println(System.getProperty("line.separator") + "Missing input file.  See usage below." + System.getProperty("line.separator"));
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("AETG", options);
				System.out.println(System.getProperty("line.separator") + "example: java -jar chadmaughan-hw2.jar -f /tmp/input.txt" + System.getProperty("line.separator"));
			}
		} 
		catch (ParseException e) {
			logger.log(Level.SEVERE, "Error parsing command line option", e);
		}
	}

	public AETG(String input) {

		// covering array coverage - defaults to 2 way coverage
		int inputCoverageCount = 2;
		int inputTotalFactorCount = 0;

		// counts of all factors should equal total factor count on line 2
		int totalCount = 0;

		// built factors
		List<Factor> factors = new ArrayList<Factor>();

		// tests
		List<Test> tests = new ArrayList<Test>();
		
		// read in the input file into the data structures
		BufferedReader in = null;
		try {

			if (logger.isLoggable(Level.INFO))
				logger.info("Reading file: " + input);

			in = new BufferedReader(new FileReader(input));

			
			String line;
			int lineCount = 0;
			while ((line = in.readLine()) != null) {
				switch (lineCount) {
					case 0:
						// line 0 specifies the coverage
						inputCoverageCount = Integer.parseInt(line.trim());
						if (logger.isLoggable(Level.INFO))
							logger.info("Set covering array coverage: " + inputCoverageCount);
						break;
					case 1:
						// line 1 specifies the factors
						inputTotalFactorCount = Integer.parseInt(line.trim());
						if (logger.isLoggable(Level.INFO))
							logger.info("Set factors: " + inputTotalFactorCount);
						break;
					default:
						line = line.trim();
	
						// make sure it isn't an empty line or the 'end of file
						// delimiter' = 0
						if (line.length() > 0 && !"0".equals(line)) {
		
							int inputLevelCount = 0;
							int inputFactorCount = 0;

							// split by white space
							String[] parts = line.split("\\s");
							if (parts.length > 0) {
								inputLevelCount = Integer.parseInt(parts[0]);
								if (logger.isLoggable(Level.INFO))
									logger.info("Set levels: " + inputLevelCount);
							}
	
							if (parts.length > 1) {
								inputFactorCount = Integer.parseInt(parts[1]);
								if (logger.isLoggable(Level.INFO))
									logger.info("Set factor: " + inputFactorCount + " for level: " + inputLevelCount);
								
								totalCount = totalCount + inputFactorCount;
							}
							
							int numbering = 0;
							for(int i = 0; i < inputFactorCount; i++) {
								
								List<Integer> levels = new ArrayList<Integer>();
								
								for(int j = 0; j < inputLevelCount; j++) {
									levels.add(numbering);
									numbering++;
								}

								Factor factor = new Factor(i);
								factor.setLevels(levels);
								if(logger.isLoggable(Level.INFO))
									logger.info("Created factor: " + factor.getNumber() + " with levels: " + factor.getLevels());
								
								factors.add(factor);
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
		
		// stop if the configuration file is wrong
		if(totalCount != inputTotalFactorCount) {
			logger.severe("Problem reading input configuration, counts don't match. Expected " + inputTotalFactorCount + " actual " + totalCount);
		}
		else {
			
			// build the pairs, or whatever coverage is (set to on first file of input)
			for(Factor factor : factors) {
				for(int level : factor.getLevels()) {
					for(Factor otherFactor : factors) {
						if(factor.getNumber() != otherFactor.getNumber()) {
							for(int otherLevel : otherFactor.getLevels()) {
								if(level != otherLevel) {
									List<Integer> l = new ArrayList<Integer>();
									l.add(level);
									l.add(otherLevel);
									
									Test test = new Test(l);
									tests.add(test);
									logger.info("adding test: " + test.getLevels());
								}
								else {
									logger.info("don't add: " + level + ", " + otherLevel);
								}
							}
						}
						else {
							logger.info("don't make test with same factor: " + factor.getNumber());
						}
					}
				}
			}
		}
	}
}
