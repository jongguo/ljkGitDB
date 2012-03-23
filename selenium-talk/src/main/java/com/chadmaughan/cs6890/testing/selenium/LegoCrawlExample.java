package com.chadmaughan.cs6890.testing.selenium;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


public class LegoCrawlExample {

	private static Logger logger = Logger.getLogger(LegoCrawlExample.class.getName());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LegoCrawlExample();
	}

	public LegoCrawlExample() {

		// start the driver
		FirefoxDriver browser = new FirefoxDriver();
		browser.get("http://us.service.lego.com/en-US/BuildingInstructions/default.aspx");

		if(logger.isLoggable(Level.INFO)) {
			logger.info("Opened page: " + browser.getTitle());
		}

		try {

			// find the 'Select a brand' element
			// just to get the count initially, due to the dynamic nature of the site, it requires us to search on each page load
			WebElement countBrandElement = browser.findElementById("ctl00_ContentPlaceHolder1_selectBrand");
			List<WebElement> countBrandOptionList = countBrandElement.findElements(By.tagName("option"));

			for(int i = 0; i < countBrandOptionList.size(); i++) {

			    // search for the 'Select a brand' element again
			    WebElement brandElement = browser.findElementById("ctl00_ContentPlaceHolder1_selectBrand");
			    List<WebElement> brandOptionList = brandElement.findElements(By.tagName("option"));

			    // get the next option
			    WebElement option = brandOptionList.get(i);

			    // ignore the default 'selected' option
			    if(!"Select a brand".equals(option.getText())) {
			    	if(logger.isLoggable(Level.INFO)) {
			    		logger.info("On brand: " + option.getText());
			    	}

				    // select the brand so the building instructions load
			        option.click();
	
			        WebElement countInstructionsElement = browser.findElementById("ctl00_ContentPlaceHolder1_SelectBIListBox");
			        List<WebElement> countInstructionsOptionList = countInstructionsElement.findElements(By.tagName("option"));
	
			        for(int j = 0; j < countInstructionsOptionList.size(); j++) {
	
			        	// you can always (at least with what I've seen) get a name, this is valuable for logging errors
			        	String name = "";
			        	try {
	
			        		// now search for the building instructions multi-select element
				            WebElement instructionsElement = browser.findElementById("ctl00_ContentPlaceHolder1_SelectBIListBox");
				            List<WebElement> instructionsOptionList = instructionsElement.findElements(By.tagName("option"));
		
				            WebElement oo = instructionsOptionList.get(j);
				            
				            name = oo.getText();
				            if(logger.isLoggable(Level.INFO)) {
				            	logger.info("Instruction name: " + name);
				            }
		
				            // click the building instructions link so the description is loaded
				            oo.click();
		
				            // get the size (in Mb) from the description
			                WebElement sizeElement = browser.findElementById("ctl00_ContentPlaceHolder1_ctl00_NoOfMBLabel");
			                float size = Float.parseFloat(sizeElement.getText());
			                if(logger.isLoggable(Level.INFO)) {
			                	logger.info("Instruction size: " + size);
			                }
		
			                // get the cache download link
			                WebElement linkElement = browser.findElementById("ctl00_ContentPlaceHolder1_ctl00_DownloadLink");
			                String link = linkElement.getAttribute("href");
			                
			                if(logger.isLoggable(Level.INFO)) {
			                	logger.info("Instruction link: " + link);
			                }
	
			        	}
			    		catch(Exception e) {
			    			logger.log(Level.SEVERE, "Error getting set", e);
			    		}
			        }
		        }
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Error getting set", e);
		}

		browser.quit();
	}
}
