package com.chadmaughan.cs6890.testing.project.crawl.node;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.openqa.selenium.WebElement;

public class DefaultNodeMapper extends AbstractNodeMapper {

	private static Logger logger = Logger.getLogger(DefaultNodeMapper.class.getName());
	private Index<Node> index;
	
	@Override
	public Node mapNode(String url, WebElement anchor) {
		
		Node node = this.graphDatabaseService.createNode();

		// set the href as a property
		node.setProperty("href", url);

		// set the pixel location of a link as a property
		if(anchor != null) {
			int location = anchor.getLocation().getX() + anchor.getLocation().getY();
			node.setProperty("location", location);
			
			String anchorText = anchor.getText();
			node.setProperty("text", anchorText);
		}
		
		// add the index
		if(this.index != null) {
			index.add(node, "href", url);
		}

		if(logger.isLoggable(Level.FINE)) {
			logger.fine("Creating source node: " + node.getId());
		}

		return node;
	}

	@Override
	public void setIndex(Index<Node> index) {
		this.index = index;
	}
}