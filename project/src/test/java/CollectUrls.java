import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.chadmaughan.cs6890.testing.project.crawl.Crawl;

public class CollectUrls {

	private static Logger logger = Logger.getLogger(Crawl.class.getName());

	private Set<String> urls;
	private HashSet<String> processed;
	private Queue<String> queue;
	private WebDriver driver;

	private int count = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new CollectUrls("http://local.lds.org:8180/index.html");
	}

	public CollectUrls(String start) {

		try {

			processed = new HashSet<String>();
			queue = new LinkedList<String>();
			urls = new HashSet<String>();

			// selenium browser instance
			driver = new FirefoxDriver();
			driver.get("http://local.lds.org:8180");
			WebElement w = driver.findElement(By.linkText("bednarda"));
			logger.info(w.getAttribute("href"));
			w.click();

			// start crawling the site!
			crawl(start);

		    BufferedWriter out = new BufferedWriter(new FileWriter("/tmp/urls.txt"));
		    for(String s :urls) {
			    out.write(s + System.getProperty("line.separator"));
		    }
		    out.close();
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		} 
		finally {
			driver.close();
		}
	}

	public void crawl(String startingUrl) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Starting to crawl: " + startingUrl);

		urls.add(startingUrl);
		queue.add(startingUrl);
		
		String newAddress = "";
		while ((newAddress = queue.poll()) != null) {
			Thread.sleep(300);
			processPage(newAddress);
		}
	}

	protected void processPage(String sourceHref) throws Exception {

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Opening URL: " + sourceHref);
		}

		driver.get(sourceHref);

		Thread.sleep(300);

		List<WebElement> links = driver.findElements(By.tagName("a"));
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Found link count: " + links.size());
		}

		// for each source node link, create the target link (if necessary)
		// and populate link relationship
		for (int i = 0; i < links.size(); i++) {

			try {
				
				WebElement w = links.get(i);
				String targetHref = w.getAttribute("href");
	
				if(w.isDisplayed()) {
					if (validUrl(targetHref)) {
			
						urls.add(targetHref);
	
						// add the target link to the queue for processing
						if (!processed.contains(targetHref)) {
							queue.add(targetHref);
						}
					}
				}
			}
			catch(StaleElementReferenceException sere) {
				
			}
		}

		processed.add(sourceHref);
		count++;
		
		if(count > 500) {
			queue.clear();
		}

		if (logger.isLoggable(Level.INFO))
			logger.info("Done processing URL: " + sourceHref);

	}

	private boolean validUrl(String href) {
		if(href != null) {			
			if(href.startsWith("http://local.lds.org") 
					&& !href.endsWith("#") 
					&& !href.endsWith(".pdf") 
					&& !href.equals("http://local.lds.org:8180/signout.html?signmeout")) {
				return true;
			}		
		}
		return false;
	}
}
