package com.chadmaughan.cs6890.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import com.chadmaughan.cs6890.testing.log.LogFormatter;
import com.chadmaughan.cs6890.testing.model.Candidate;
import com.chadmaughan.cs6890.testing.model.Factor;
import com.chadmaughan.cs6890.testing.model.Test;

/**
 * Implementation of the AETG algorithm that generates two-way and three-way combinatorial test suites.
 * @author Chad Maughan
 */
public class AETG {

	private static int RUN_COUNT = 100;
	
	private static Logger logger = Logger.getLogger(AETG.class.getName());

	// tests
	private List<String> testsList;
	private Map<String,Test> testsMap;
	private Map<Integer,List<Test>> testsByLevels;

	// built factors
	private List<Factor> factors;
	
	/**
	 * @param input Absolute path of the input file to be processed (ex '-f /tmp/input.txt')
	 */
	public static void main(String[] args) {

		// configure java.util.logging single line output
		Logger logger = Logger.getLogger("com.chadmaughan");
		logger.setUseParentHandlers(false);

		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new LogFormatter());

		logger.addHandler(ch);
		logger.setLevel(Level.SEVERE);

		try {
			// add a command line option for file input name
			Options options = new Options();
			options.addOption("f", true, "input file to process");
			options.addOption("v", false, "verbose logging");

			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);

			// verbose output
			if (cmd.hasOption('v')) {
				System.out.println("Verbose output");
				logger.setLevel(Level.FINE);
			}

			if (cmd.hasOption('f')) {
				String f = cmd.getOptionValue("f");
				try {
					new AETG(f);
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, "Error", e);
				}
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

	public AETG(String input) throws Exception {

		// covering array coverage - defaults to 2 way coverage
		int inputCoverageCount = 2;
		int inputTotalFactorCount = 0;

		// counts of all factors should equal total factor count on line 2
		int totalCount = 0;

		// factors
		factors = new ArrayList<Factor>();

		// tests
		testsList = new ArrayList<String>();
		testsMap = new HashMap<String,Test>();
		testsByLevels = new HashMap<Integer,List<Test>>();

		// read the input file into the data structures
		BufferedReader in = null;
		try {

			if (logger.isLoggable(Level.INFO))
				logger.info("Reading file: " + input);

			in = new BufferedReader(new FileReader(input));

			// for factor indexing
			int factorsIndex = 0;
			int levelsIndex = 0;
			
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
	
						// make sure it isn't an empty line or the 'end of file delimiter' = 0 (in input files provided)
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
							
							for(int i = 0; i < inputFactorCount; i++) {
								
								List<Integer> levels = new ArrayList<Integer>();
								
								for(int j = 0; j < inputLevelCount; j++) {
									levels.add(levelsIndex);
									levelsIndex++;
								}

								Factor factor = new Factor(factorsIndex);
								factor.setLevels(levels);
								if(logger.isLoggable(Level.INFO))
									logger.info("Created factor: " + factor.getNumber() + " with levels: " + factor.getLevels());
								
								factors.add(factor);
								factorsIndex++;
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
		
		if(logger.isLoggable(Level.INFO)) {
			logger.info("Created factors:");
			for(Factor factor : factors) {
				logger.info("" + factor);
			}
		}
		// stop if the configuration file is wrong
		if(totalCount != inputTotalFactorCount) {
			logger.severe("Problem reading input configuration, counts don't match. Expected " + inputTotalFactorCount + " actual " + totalCount);
		}
		else {
			
			// build the pairs, or whatever the coverage is (set to on first file of input)
			if(inputCoverageCount == 2) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Two-way covering array specified in input file");
					
				for(Factor firstFactor : factors) {
					for(Factor secondFactor : factors) {

						if(firstFactor.getNumber() != secondFactor.getNumber()) {

							for(int firstLevel : firstFactor.getLevels()) {
								for(int secondLevel : secondFactor.getLevels()) {
							
									if(firstLevel != secondLevel) {
										SortedSet<Integer> l = new TreeSet<Integer>();
										l.add(firstLevel);
										l.add(secondLevel);
	
										Test test = new Test(l);
										
										// add to tests collection indexed by level they contain
										for(Integer level : l) {
											List<Test> tests = testsByLevels.get(level);
											if(tests == null)
												tests = new ArrayList<Test>();
											
											if(!tests.contains(test)) {
												tests.add(test);
											}
											
											testsByLevels.put(level, tests);
										}
										testsMap.put(test.getKey(), test);
										if(!testsList.contains(test.getKey())) {
											testsList.add(test.getKey());
											if(logger.isLoggable(Level.INFO))
												logger.info("Adding two-way test: " + test.getLevels());
										}
									}
								}
							}
						}
					}
				}
			}
			else if(inputCoverageCount == 3) {
				if(logger.isLoggable(Level.INFO))
					logger.info("Three-way covering array specified in input file");
				
				for(Factor firstFactor : factors) {
					for(Factor secondFactor : factors) {
						for(Factor thirdFactor : factors) {

							if((firstFactor.getNumber() != secondFactor.getNumber()) && (secondFactor.getNumber() != thirdFactor.getNumber()) && (firstFactor.getNumber() != thirdFactor.getNumber())) {
							
								for(int firstLevel : firstFactor.getLevels()) {
									for(int secondLevel : secondFactor.getLevels()) {
										for(int thirdLevel : thirdFactor.getLevels()) {
	
											if((firstLevel != secondLevel) && (secondLevel != thirdLevel) && (firstLevel != thirdLevel)) {
												SortedSet<Integer> l = new TreeSet<Integer>();
												l.add(firstLevel);
												l.add(secondLevel);
												l.add(thirdLevel);
			
												Test test = new Test(l);
												testsMap.put(test.getKey(), test);
												if(!testsList.contains(test.getKey())) {
													testsList.add(test.getKey());
													if(logger.isLoggable(Level.INFO))
														logger.info("Adding three-way test: " + test.getLevels());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			else {
				throw new Exception("This algorithm only generates two-way or three-way covering arrays, input file specified: " + inputCoverageCount);
			}

			// store the run times so we can calculate the mean and std dev
			DescriptiveStatistics runStatistics = new DescriptiveStatistics();
			
			// also track the number of candidates
			DescriptiveStatistics candidateStatistics = new DescriptiveStatistics();
			int bestCandidateCount = Integer.MAX_VALUE;
			int worstCandidateCount = Integer.MIN_VALUE;
			
			// due to randomization, run the algorithm 100 times and average
			for(int z = 0; z < RUN_COUNT; z++) {
				
				if(logger.isLoggable(Level.INFO))
					logger.info("STARTING RUN: " + z);
				
				long start= System.currentTimeMillis();

				List<Candidate> candidates = new ArrayList<Candidate>();
				int candidateCount = 0;
				
				boolean stop = false;
				while(!stop) {
	
					Candidate candidate = new Candidate(candidateCount, factors.size());
					if(logger.isLoggable(Level.INFO))
						logger.info("Building candidate number: " + candidate.getNumber());
					
					// choose an unmarked row at random
					int randomNumber = getRandomUnmarkedRow();
					
					// all rows are marked
					if(randomNumber < 0) {
						if(logger.isLoggable(Level.INFO))
							logger.info("All rows are marked");
						stop = true;
					}
					else {
	
						String key = testsList.get(randomNumber);
						Test test = testsMap.get(key);
						if(logger.isLoggable(Level.INFO))
							logger.info("Unmarked row chosen at random: " + test.getLevels());
	
						// add factor to candidate
						candidate.addTests(test.getLevels(), factors);
	
						if(logger.isLoggable(Level.INFO))
							logger.info("Added tests to candidate: " + candidate);
	
						// fill up the candidate with test for each factor
						for(int i = 0; i < inputTotalFactorCount - test.getLevels().size(); i++) {
								
							// get a random factor
							int randomFactorIndex = candidate.getRandomFactor();
							Factor randomFactor = factors.get(randomFactorIndex);
							if(logger.isLoggable(Level.INFO))
								logger.info("Random uncovered factor chosen: " + randomFactor.getNumber());
							
							// get level covering the most tests
							int level = getLevelCoveringMostTests(randomFactor);
							if(logger.isLoggable(Level.INFO))
								logger.info("Level covering most tests: " + level);
	
							candidate.addTest(level, factors);
							if(logger.isLoggable(Level.INFO))
								logger.info("Added single test to candidate: " + candidate);
						}
						
						// mark the chosen rows
						logger.info("Enumerated test: " + candidate.twoWayTests());
						for(Test t : candidate.twoWayTests()) {
							boolean marked = markCoveredTest(t);
							if(marked)
								candidate.incrementCoveredTests();
						}
	
						if(logger.isLoggable(Level.INFO))
							logger.info("Candidate covers test count: " + candidate.getCoveredTests());
						
						candidates.add(candidate);
						candidateCount++;
	
					}				
					
					logUnmarkedTests();	
				}

				// store the candidate count
				candidateStatistics.addValue((double) candidateCount);

				if(candidateCount < bestCandidateCount)
					bestCandidateCount = candidateCount;
				
				if(candidateCount > worstCandidateCount)
					worstCandidateCount = candidateCount;
				
				// store the run time
				long end = System.currentTimeMillis();
				long diff = end - start;
				if(logger.isLoggable(Level.INFO))
					logger.info("Time to run (in milliseconds): " + diff);

				runStatistics.addValue((double) diff);
				
				// reset the marked tests (should be all of them)
				for(Test test : testsMap.values()) {
					test.setMarked(false);
				}
			}
			
			System.out.println("Run count: " + RUN_COUNT);
			
			// output the run time stats
			System.out.println("Run time mean (in milliseconds): " + runStatistics.getMean());
			System.out.println("Run time standard deviation (in milliseconds): " + runStatistics.getStandardDeviation());

			System.out.println("");
			
			// output the candidate stats
			System.out.println("Candidates mean: " + candidateStatistics.getMean());
			System.out.println("Candidates standard deviation: " + candidateStatistics.getStandardDeviation());
			System.out.println("Candidates worst count: " + worstCandidateCount);
			System.out.println("Candidates best count: " + bestCandidateCount);

		}
	}
	
	private void logUnmarkedTests() {
		List<Test> unmarked = new ArrayList<Test>();
		
		for(Test test : testsMap.values()) {
			if(!test.isMarked()) {
				unmarked.add(test);
			}
		}
		
		if(unmarked.size() > 0) {
			if(logger.isLoggable(Level.INFO))
				logger.info("Unmarked tests: " + unmarked);
		}
	}
	
	private boolean markCoveredTest(Test test) {
		boolean marked = false;
		if(testsMap.get(test.getKey()).isMarked()) {
			marked = true;
		}

		testsMap.get(test.getKey()).setMarked(true);
		if(logger.isLoggable(Level.INFO))
			logger.info("Test marked: " + test);

		return marked;
	}
	
	private int getLevelCoveringMostTests(Factor factor) {

		int max = 0;
		int result = -1;

		Map<Integer,Integer> counts = new HashMap<Integer,Integer>();
		for(Integer i : factor.getLevels()) {
			for(Test t : testsByLevels.get(i)) {
				if(!t.isMarked()) {
					Integer count = counts.get(i);
					if(count == null) {
						count = 1;
					}
					else {
						count = count + 1;
					}
					counts.put(i, count);
					
					if(count > max) {
						max = count;
					}
				}
			}
		}

		// remove all but those with count == to max
		Map<Integer,Integer> highest = new HashMap<Integer,Integer>();
		for(int key : counts.keySet()) {
			if(counts.get(key) == max) {
				highest.put(key, counts.get(key));
			}
		}
		
		if(logger.isLoggable(Level.INFO))
			logger.info("Test covering count to choose from: " + highest.keySet() + highest.values());
		
		if(highest.values().size() > 1) {
			int random = (int) (Math.random() * highest.values().size());
			result = (Integer) highest.keySet().toArray()[random];

			if(logger.isLoggable(Level.INFO))
				logger.info("Chose random level: " + result);
			
		}
		else if(highest.values().size() == 1) {
			result = (Integer) highest.keySet().toArray()[0];
			if(logger.isLoggable(Level.INFO))
				logger.info("Single level with highest covering count: " + result);			
		}
		else {
			
			// just get a random level for the factor (it has no coverage)
			int random = (int) (Math.random() * factor.getLevels().size());
			result = factor.getLevels().get(random);
			if(logger.isLoggable(Level.INFO))
				logger.info("Zero coverage with all levels, randomly returing level from factor: " + result);			
		}

		return result;
	}
		
	/**
	 * Picks a random number between 0 and tests.size()
	 * and increments by 1 until an unmarked row is found.
	 * If the rowIndex gets above tests.size(), it is reset
	 * to zero and checks the beginning of the tests collection.
	 * 
	 * If no unmarked rows are found, then -1 is returned
	 * 
	 * @return int position in tests collection
	 */
	private int getRandomUnmarkedRow() {

		int rowIndex = -1;
		
		// choose an uncovered test at random, start at the random number and work your
		// way up until you can find one
		int random = (int) (Math.random() * testsList.size());

		String key = testsList.get(random);
		if(!testsMap.get(key).isMarked()) {
			rowIndex = random;
		}
		else {
			int count = random;
			for(int i = 0; i < testsMap.values().size(); i++) {
				String k = testsList.get(count);
				if(!testsMap.get(k).isMarked()) {
					rowIndex = i;
					break;
				}
				else {
					if(count == testsMap.values().size() - 1) {
						count = 0;
					}
					else {
						count++;
					}
				}
			}
		}		
		return rowIndex;
	}
}
