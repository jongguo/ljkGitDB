package com.chadmaughan.cs6890.testing.project.crawl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.chadmaughan.cs6890.testing.log.LogFormatter;
import com.chadmaughan.cs6890.testing.project.crawl.node.DefaultNodeMapper;
import com.chadmaughan.cs6890.testing.project.crawl.node.NodeMapper;
import com.chadmaughan.cs6890.testing.project.crawl.url.DefaultUrlCrawler;
import com.chadmaughan.cs6890.testing.project.crawl.url.UrlCrawler;

public class Crawl {

	private static enum RelationshipTypes implements RelationshipType {
		LINKS_TO
	};

	private static Logger logger = Logger.getLogger(Crawl.class.getName());

	private static final String GRAPHDB_PATH = "/tmp/example.db";

	private HashSet<String> processed;
	private Queue<String> queue;
	private WebDriver driver;

	private DefaultUrlCrawler urlCrawler;
	private DefaultNodeMapper nodeMapper;
	
	private GraphDatabaseService gds;
	private Index<Node> hrefIndex;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// configure java.util.logging single line output
		Logger logger = Logger.getLogger("com.chadmaughan");
		logger.setUseParentHandlers(false);

		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new LogFormatter());

		logger.addHandler(ch);

		DefaultNodeMapper defaultNodeMppaer = new DefaultNodeMapper();
		
		DefaultUrlCrawler defaultUrlCrawler = new DefaultUrlCrawler("http://coenraets.org/backbone/directory/jquerymobile/#employees/1");
		new Crawl(defaultUrlCrawler, defaultNodeMppaer);
	}

	public Crawl(UrlCrawler crawler, NodeMapper mapper) {

		urlCrawler = (DefaultUrlCrawler) crawler;
		nodeMapper = (DefaultNodeMapper) mapper;

		try {

			// populate blacklist (if exists)
			urlCrawler.addUrlBlacklist("http://slashdot.org");

			// collections used for populating graph
			processed = new HashSet<String>();
			queue = new LinkedList<String>();
			
			// selenium browser instance
			driver = new FirefoxDriver();

			// create neo4j database
			gds = new EmbeddedGraphDatabase(GRAPHDB_PATH);
			
			// add a reference to the database to the node mapper
			nodeMapper.setGraphDatabaseService(gds);
	
			// create an index on the URLs so that we can retrieve them later
			IndexManager index = gds.index();
			hrefIndex = index.forNodes("href");
			nodeMapper.setIndex(hrefIndex);
			
			registerShutdownHook(gds);
	
			if (gds == null) {
				throw new RuntimeException("No embedded graph database");
			}

			// start crawling the site!
			crawl(urlCrawler.getStartUrl());
			
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		}
		finally {
			gds.shutdown();
			driver.close();
		}
	}

	public void crawl(String startingUrl) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Starting to crawl: " + startingUrl);

		queue.add(startingUrl);
		
		String newAddress = "";
		while ((newAddress = queue.poll()) != null) {
			Thread.sleep(urlCrawler.getPageProcessDelay());
			processPage(newAddress);
		}
	}

	protected void processPage(String sourceHref) throws Exception {

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Opening URL: " + sourceHref);
		}

		driver.get(sourceHref);

		Thread.sleep(urlCrawler.getPageProcessDelay());
		
		List<WebElement> links = driver.findElements(By.tagName("a"));
		if(logger.isLoggable(Level.INFO)) {
			logger.info("Found link count: " + links.size());
		}
		
		Transaction tx = gds.beginTx();
		try {
			
			// get the existing node if it has already been created
			IndexHits<Node> hits = hrefIndex.get("href", sourceHref);
			Node sourceNode = hits.getSingle();
			
			// if the source node isn't in the database, create it
			if(sourceNode == null) {
				sourceNode = nodeMapper.mapNode(sourceHref, null);
			}
			else {
				if(logger.isLoggable(Level.FINE)) {
					logger.fine("Not creating, source node existed: " + sourceNode.getId());
				}
			}
			
			// for each source node link, create the target link (if necessary) and populate link relationship
			String targetHref;
			for (int i = 0; i < links.size(); i++) {
	
				WebElement w = links.get(i);
				
				targetHref = w.getAttribute("href");

				if (urlCrawler.validUrl(targetHref)) {

					// only add links to traverse if they are visible
					if(w.isDisplayed()) {

						// add the target node to the database
						Transaction targetTx = gds.beginTx();

						IndexHits<Node> targetHits = hrefIndex.get("href", targetHref);
						Node targetNode = targetHits.getSingle();
						
						// the node isn't in the database, create it
						if(targetNode == null) {
							targetNode = nodeMapper.mapNode(targetHref, w);
						}
						else {
							if(logger.isLoggable(Level.FINE)) {
								logger.fine("Not creating, target node existed: " + sourceNode.getId());
							}
						}
	
						targetTx.success();
						targetTx.finish();
						
						if(logger.isLoggable(Level.INFO)) {
							logger.info("LINK: " + sourceHref.substring(0, sourceHref.indexOf("#")) + " --> " + targetHref.substring(0, sourceHref.indexOf("#")));
						}
						
						sourceNode.createRelationshipTo(targetNode, RelationshipTypes.LINKS_TO);
		
						// add the target link to the queue for processing
						if (!processed.contains(targetHref)) {
							queue.add(targetHref);
						}
					}
				}
				tx.success();
			}
		} 
		finally {
			tx.finish();
		}

		processed.add(sourceHref);

		if (logger.isLoggable(Level.INFO))
			logger.info("Done processing URL: " + sourceHref);

	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
