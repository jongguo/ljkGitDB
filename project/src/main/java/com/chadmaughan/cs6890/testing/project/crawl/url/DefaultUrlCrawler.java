package com.chadmaughan.cs6890.testing.project.crawl.url;

import java.util.ArrayList;
import java.util.List;

public class DefaultUrlCrawler extends AbstractUrlCrawler {

	private String startUrl;
	
	private List<String> urls;
	private List<String> urlBlacklist;
	
	private int depth = 3;
	
	private long DEFAULT_PAGE_PROCESSING_DELAY = 1000;

	private String strategy = UrlCrawler.DEPTH_FIRST_STRATEGY;

	public DefaultUrlCrawler() {}

	public DefaultUrlCrawler(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public List<String> getUrlBlacklist() {
		return urlBlacklist;
	}

	public void setUrlBlacklist(List<String> urlBlacklist) {
		this.urlBlacklist = urlBlacklist;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public boolean addUrlBlacklist(String url) {
		if(this.getUrlBlacklist() == null) {
			this.setUrlBlacklist(new ArrayList<String>());
		}
		return this.getUrlBlacklist().add(url);
	}

	@Override
	public boolean addUrl(String url) {
		if(this.getUrls() == null) {
			this.setUrls(new ArrayList<String>());
		}
		return this.getUrls().add(url);
	}
	
	@Override
	public boolean validUrl(String href) {
		if(href != null) {
			
			if(this.getUrlBlacklist() == null) {
				this.setUrlBlacklist(new ArrayList<String>());
			}

			if(!this.getUrlBlacklist().contains(href)) {
				if(href.startsWith("http://") && !href.endsWith("#")) {
					return true;
				}		
			}
		}
		return false;
	}

	@Override
	public long getPageProcessDelay() {
		return DEFAULT_PAGE_PROCESSING_DELAY;
	}
}