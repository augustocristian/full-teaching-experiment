package com.fullteaching.e2e.common;

import static com.fullteaching.e2e.common.Constants.LOCALHOST;
import static java.lang.System.getProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fullteaching.e2e.utils.Wait;

public class SpiderNavigation {
	
	/*NOTE: traditional spider navigation (retrieving all links and then confirm them doesn't 
	 * 		work properly in this kind of application. 
	 * This is a Specialized Spider navigation for FullTeaching.*/
	
	
	private static String host = LOCALHOST;
	
	/**
	 * Retrieve all the links in a webpage
	 * @param wd
	 * @return List<WebElements> 
	 */
	public static List<WebElement> getPageLinks(WebDriver wd){
		
		String appHost = getProperty("fullTeachingUrl");
        if (appHost != null) {
            host = appHost;
        }
		
		Set<String> links_href = new HashSet<String>();
		List<WebElement> links = new ArrayList<WebElement>();
		
		List<WebElement> a_lst = wd.findElements(By.tagName("a"));
		
		for (WebElement a : a_lst) {
			String href = a.getAttribute("href"); 
			if ((href !=null)&& (!href.trim().equals(""))&&(!href.contains("#"))){
				if(! isContainedIn(href.trim(),links_href) && href.contains(host))
					links.add(a);
			}
		}
		
		return links;	
	}
	
	/**
	 * Returns all the unexplored links of a web page
	 * @param wd
	 * @param explored
	 * @return
	 */
	public static List<WebElement> getUnexploredPlageLinks(WebDriver wd, Map<String,String> explored){
		List<WebElement> links = new ArrayList<WebElement>();
		
		List<WebElement> allLinks = getPageLinks(wd);
		
		for (WebElement a : allLinks) {
			String href = a.getAttribute("href"); 
			if ((href !=null)&& (!href.trim().equals(""))&&(!href.contains("#"))){
				if(! isContainedIn(href.trim(),explored.keySet()) && href.contains(host))
					links.add(a);
			}
		}
		
		return links;	
	}
	
	public static Map<String,String> exploreLinks(WebDriver wd, List<WebElement> pageLinks, Map<String,String> explored, int depth){
		
		if (depth <= 0) return explored; 
		
		while (!pageLinks.isEmpty()) {
			WebElement link = pageLinks.get(0);
			String href = link.getAttribute("href");
			String currentUrl = wd.getCurrentUrl();
			boolean explore = true;
			try {
				//explore page...
				//navigate to new page: 
				link.click();
				Wait.footer(wd);
				explored.put(href, "OK");
				
			}catch(Exception e) {
				//if fails put KO and continue
				explored.put(href, "KO");
				explore = false;
			}
			if (explore) {
				List<WebElement> newLinks = getUnexploredPlageLinks(wd, explored);
				explored =  exploreLinks(wd, newLinks, explored, depth-1);
			}
			
			NavigationUtilities.getUrlAndWaitFooter(wd,currentUrl);
			pageLinks = SpiderNavigation.getUnexploredPlageLinks(wd, explored);
			
		}
		return explored;
	}
	
	private static boolean isContainedIn(String href, Set<String> set) {
		if(set.contains(href)) return true;
		
		if(href.endsWith("/")) {
			String aux_href = href.substring(0, href.length()-1);
			return set.contains(aux_href);
		}
		else {
			String aux_href = href+"/";
			return set.contains(aux_href);
		}	
	}
	
	public static Set<String> addNonExistentLink(Set<String> original, String href){
		
		if ((href !=null)&& (!href.equals(""))&&(!href.contains("#"))){
			if(! isContainedIn(href, original) && href.contains(host))
				original.add(href);
		}
		
		return original;
	}
	
	public static List<String> discardExplored(List<String> new_links, Set<String> explored){
		List<String> withOutExplored = new ArrayList<String>();
		
		for (String href: new_links) {
			if ((href !=null)&& (!href.equals(""))&&(!href.contains("#"))){
				if(! isContainedIn(href,explored) && href.contains(host))
					withOutExplored.add(href);
			}
		}
		return withOutExplored;
	}
	
}
