package com.fullteaching.e2e.common;

import static com.fullteaching.e2e.common.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fullteaching.e2e.common.exception.ElementNotFoundException;

public class SessionNavigationUtilities {
	
	public static List<String> getFullSessionList(WebDriver wd){
		ArrayList <String> session_titles = new ArrayList<String>();
		
		WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, SESSION_ICON);
		List<WebElement> sessions = tab_content.findElements(SESSIONLIST_SESSION_ROW);
		for(WebElement session: sessions) {
			session_titles.add(session.findElement(SESSIONLIST_SESSION_NAME).getText());
		}
		
		return session_titles;
	}
	
	public static WebElement getSession(WebDriver wd, String session_name) throws ElementNotFoundException {
		WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, SESSION_ICON);
		List<WebElement> sessions = tab_content.findElements(SESSIONLIST_SESSION_ROW);
		for(WebElement session: sessions) {
    		try {
    			WebElement title = session.findElement(SESSIONLIST_SESSION_NAME);
    			String title_text = title.getText();
    			if (title_text ==null || title_text.equals("")) {
    				title_text = title.getAttribute("innerHTML");
    			}
    			if(session_name.equals(title_text)) {
    				
    				return session;
    			}
    		}
    		catch(NoSuchElementException csee) {
    			//do nothing and look for the next item
    		}
    	}
		throw new ElementNotFoundException("getSession-the session doesn't exist");
	}
}
