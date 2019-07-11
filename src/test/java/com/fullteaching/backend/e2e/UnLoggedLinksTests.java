package com.fullteaching.backend.e2e;

import static java.lang.System.getProperty;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import  static com.fullteaching.e2e.common.Constants.*; 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.fullteaching.e2e.common.NavigationUtilities;
import com.fullteaching.e2e.common.SpiderNavigation;

abstract public class UnLoggedLinksTests {

	protected static WebDriver driver; 
	protected static int DEPTH = 3;
	protected static String host = LOCALHOST;
	
	 @BeforeAll
	    public void setUp() {
			
	    	String appHost = getProperty("fullTeachingUrl");
	        if (appHost != null) {
	            host = appHost;
	        }
	 }

	@Test
	public void spiderUnloggedTest() {
		/*navigate from home*/
		
		NavigationUtilities.getUrlAndWaitFooter(driver, host);
				
		List <WebElement> pageLinks = SpiderNavigation.getPageLinks(driver);
		
		Map <String,String> explored = new HashMap<String,String>();
		
		//Navigate the links... 
		//Problem: once one is pressed the rest will be unusable as the page reloads... 

		explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH);
		
		List<String> failed_links = new ArrayList<String>();
		
		explored.forEach((link,result) -> {if (result.equals("KO")) failed_links.add(link);});

		String msg = "";
		for (String failed: failed_links) {
			msg = failed +"\n";	
		}
		assertTrue(failed_links.isEmpty(), msg);
	}
	
}
