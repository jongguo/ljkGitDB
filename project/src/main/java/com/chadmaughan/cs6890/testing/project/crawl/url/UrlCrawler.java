package com.chadmaughan.cs6890.testing.project.crawl.url;

public interface UrlCrawler {

	public static final String DEPTH_FIRST_STRATEGY = "depth";
	public static final String BREADTH_FIRST_STRATEGY = "breadth";

	long getPageProcessDelay();
	String getStartUrl();
	boolean addUrl(String url);
	boolean validUrl(String url);
}