package com.chadmaughan.cs6890.testing.project.test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class ReduceNeo4j {

	private static enum RelationshipTypes implements RelationshipType {
		CHILD_OF
	};

	private static Logger logger = Logger.getLogger(ReduceNeo4j.class.getName());
	
	private GraphDatabaseService gds;
	private static final String GRAPHDB_PATH = "/tmp/links.db";
	
	private static final String URLS_FILE = "/Users/chadmaughan/urls.txt";

	private static final String URL_PROPERTY = "url";
	private static final String BRANCH_COUNT_PROPERTY = "branch";

	private Index<Node> urlIndex;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ReduceNeo4j();
	}
	
	public ReduceNeo4j() {

		try {

			gds = new EmbeddedGraphDatabase(GRAPHDB_PATH);

			IndexManager index = gds.index();
			urlIndex = index.forNodes(URL_PROPERTY);

			registerShutdownHook(gds);
			
			process();
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		}
		finally {
			gds.shutdown();
		}
	}
	
	private void process() {
		
		// get the urls from file
		List<String> urls = new ArrayList<String>();
		
		try {
		    BufferedReader in = new BufferedReader(new FileReader(URLS_FILE));
		    String str;
		    while ((str = in.readLine()) != null) {
		        urls.add(str);
		    }
		    in.close();
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading URLs", e);
		}

		// process each url
		for(String s : urls) {

			Transaction tx = gds.beginTx();

			try {
								
				// clear out the protocol
				s = s.replaceAll("https://", "");
				s = s.replaceAll("http://", "");
				
				if(s.indexOf("#") == -1)
					continue;
	
				System.out.println("Working full url: " + s);
	
				// separate the url from the fragments
				String[] parts = s.split("#");
	
		        // add the root part of url (everything up to '#')
				String root = parts[0] + "#";
	
				Node rootNode = createRootNodeIfNeeded(root);
				rootNode.setProperty(BRANCH_COUNT_PROPERTY, new Integer(1));

				Node concatenatedFragmentsNode = rootNode;
		        String concatenatedFragments = root;
	
		        Node previousNode = rootNode;
		        String previous = root;
		        
				String[] fragments = parts[1].split("[/:]");
				for(int i = 0; i < fragments.length; i++) {
	
					concatenatedFragments += "/" + fragments[i];
	
					System.out.println("       fragments: " + concatenatedFragments);
	
					IndexHits<Node> concatenatedFragmentsHits = urlIndex.get(URL_PROPERTY, concatenatedFragments);
					concatenatedFragmentsNode = concatenatedFragmentsHits.getSingle();
	
					if(concatenatedFragmentsNode == null) {
	
						concatenatedFragmentsNode = gds.createNode();
						concatenatedFragmentsNode.setProperty(BRANCH_COUNT_PROPERTY, new Integer(1));
						concatenatedFragmentsNode.setProperty(URL_PROPERTY, concatenatedFragments);
						urlIndex.add(concatenatedFragmentsNode, URL_PROPERTY, concatenatedFragments);
	
						System.out.println("Creating node: " + concatenatedFragmentsNode.getId() + ", " + concatenatedFragmentsNode.getProperty(URL_PROPERTY));
	
						Relationship re = concatenatedFragmentsNode.createRelationshipTo(previousNode, RelationshipTypes.CHILD_OF);
						re.setProperty("count", new Integer(1));
						re.setProperty("source", previousNode.getProperty(URL_PROPERTY));
						re.setProperty("target", concatenatedFragmentsNode.getProperty(URL_PROPERTY));

						previousNode.setProperty(BRANCH_COUNT_PROPERTY, ((Integer) previousNode.getProperty(BRANCH_COUNT_PROPERTY) + 1));

						previous = concatenatedFragments;
			        	previousNode = concatenatedFragmentsNode;
	
					}
					else {
						System.out.println("node exists: " + concatenatedFragmentsNode.getId());
	
						if(!concatenatedFragmentsNode.hasRelationship()) {
							Relationship re = concatenatedFragmentsNode.createRelationshipTo(previousNode, RelationshipTypes.CHILD_OF);
							re.setProperty("count", new Integer(1));
							re.setProperty("source", previousNode.getProperty(URL_PROPERTY));
							re.setProperty("target", concatenatedFragmentsNode.getProperty(URL_PROPERTY));
				        	System.out.println("     +edge: " + previous + " -> " + concatenatedFragments);
						}
						else {
							Relationship re = concatenatedFragmentsNode.getSingleRelationship(RelationshipTypes.CHILD_OF, Direction.OUTGOING);
							re.setProperty("count", ((Integer) re.getProperty("count") + 1));
						}
	
						previousNode = concatenatedFragmentsNode;
					}
				}
				tx.success();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				tx.finish();
			}
		}
	}
	
	private Node createRootNodeIfNeeded(String root) {
		
		// get the existing node if it has already been created
		IndexHits<Node> hits = urlIndex.get(URL_PROPERTY, root);
		Node rootNode = hits.getSingle();
		
		// if the source node isn't in the database, create it
		if(rootNode == null) {
			rootNode = gds.createNode();
			rootNode.setProperty(URL_PROPERTY, root);
			urlIndex.add(rootNode, URL_PROPERTY, root);

			System.out.println("Creating root node: " + rootNode.getId());
		}
		else {
			System.out.println("Not creating, root node existed: " + rootNode.getId());
		}
		return rootNode;
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
