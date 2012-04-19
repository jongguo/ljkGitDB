package com.chadmaughan.cs6890.testing.project.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.browsermob.core.har.Har;
import org.browsermob.proxy.ProxyServer;
import org.eclipse.jetty.http.HttpStatus;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Test {
	
	private static Logger logger = Logger.getLogger(Test.class.getName());

	private static final String GRAPHDB_PATH = "/tmp/example.db";

	private List<String> httpErrors;
	
	private WebDriver driver;
	private GraphDatabaseService gds;

	public static void main(String[] args) {
		List<String> urls = new ArrayList<String>();
		urls.add("http://coenraets.org/backbone/directory/jquerymobile/#employees/1");
		urls.add("http://coenraets.org/backbone/directory/jquerymobile/#employees/1/reports");
		urls.add("http://coenraets.org/backbone/directory/jquerymobile/#employees/3");
		new Test(urls);
	}
	
	public Test(List<String> urls) {
		
		ProxyServer server = null;
		httpErrors = new ArrayList<String>();

		try {

			// create neo4j database
			gds = new EmbeddedGraphDatabase(GRAPHDB_PATH);

			// read in the javascript error file
			final String js = readJavascript();

			server = new ProxyServer(4444);
			server.start();
	
			server.addRequestInterceptor(new HttpRequestInterceptor() {
				@Override
				public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
					logger.info("Request intercepted: " + httpRequest.getRequestLine());
				}
			});
			
			server.addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {

					// log anything that isn't 200
					if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.OK_200) {
						logger.info("Error: " + httpResponse.getStatusLine().getStatusCode());
						httpErrors.add("Error");
					}
					
//					String html = EntityUtils.toString(httpResponse.getEntity());
//					html = html.replace("<head>", "<head>" + js);
//
//					System.out.println(httpResponse.getEntity().isStreaming());
				}
			});
			
			// get the Selenium proxy object
			Proxy proxy = server.seleniumProxy();
	
			// configure it as a desired capability
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(CapabilityType.PROXY, proxy);
			
			driver = new FirefoxDriver(capabilities);

			// loop through the urls
			for(String url : urls) {
				driver.get(url);
			}
			
			// create a new HAR with the label "yahoo.com"
			server.newHar("com.chadmaughan");
	
			// get the HAR data
			Har har = server.getHar();
			har.writeTo(new FileWriter("/tmp/crawl-har.json"));

		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		}
		finally {
			gds.shutdown();
			try {
				server.stop();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			driver.close();
		}
	}
	
	private String readJavascript() throws IOException {

		InputStream in = this.getClass().getClassLoader().getResourceAsStream("error.js");
		StringWriter writer = new StringWriter();

		IOUtils.copy(in, writer, "UTF-8");
		
		in.close();
		
		return writer.toString();

	}
}
