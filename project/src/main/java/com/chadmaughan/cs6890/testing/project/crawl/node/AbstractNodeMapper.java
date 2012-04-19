package com.chadmaughan.cs6890.testing.project.crawl.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.openqa.selenium.WebElement;

abstract class AbstractNodeMapper implements NodeMapper {

	protected GraphDatabaseService graphDatabaseService;
	
	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	public abstract Node mapNode(String url, WebElement anchor);
	public abstract void setIndex(Index<Node> index);
}
