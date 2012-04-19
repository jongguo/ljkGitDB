package com.chadmaughan.cs6890.testing.project.crawl.url;

public abstract class AbstractUrlCrawler implements UrlCrawler {
	public abstract String getStartUrl();
	public abstract long getPageProcessDelay();
	public abstract boolean addUrl(String url);
    public abstract boolean validUrl(String url);
}