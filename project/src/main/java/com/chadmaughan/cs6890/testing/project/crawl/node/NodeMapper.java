package com.chadmaughan.cs6890.testing.project.crawl.node;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.openqa.selenium.WebElement;

public interface NodeMapper {
	Node mapNode(String url, WebElement anchor);
	void setIndex(Index<Node> indexes);
}