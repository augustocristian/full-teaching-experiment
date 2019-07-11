package com.fullteaching.e2e.utils;

import static java.lang.System.getProperty;
import static java.lang.invoke.MethodHandles.lookup;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.OutputType.BASE64;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.slf4j.LoggerFactory.getLogger;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.slf4j.Logger;

import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;
import com.fullteaching.e2e.common.CourseNavigationUtilities;
import com.fullteaching.e2e.common.UserUtilities;

import static com.fullteaching.e2e.common.Constants.*;

public class SetUp {
	
	final static  Logger log = getLogger(lookup().lookupClass());

	protected static String host=LOCALHOST;

	public static String getHost() {
		String appHost = getProperty("fullTeachingUrl");
        if (appHost != null) {
            host = appHost;
        }
        return host;
	}
	
	public static WebDriver loginUser(WebDriver driver, String host, String user, String password) throws ElementNotFoundException, TimeOutExeception {
		String logged_user = null; 
    	boolean is_logged = true;
    	
    	try {
    		
			logged_user = UserUtilities.getLoggedUser(driver);
			if (!logged_user.equals(user)) {
				UserUtilities.logOut(driver,host);
				UserUtilities.checkLogOut(driver);
				is_logged = false;
			}
			
		} catch (NotLoggedException e) {
			//perfect we will log it after
			is_logged=false;
		}
    	if (!is_logged) {
    		driver = UserUtilities.login(driver, user, password, host);
    	}
    	
    	return driver;
	}
	
	public static void tearDown(WebDriver driver) throws IOException {
		if (driver != null) {
            log.info("url:"+driver.getCurrentUrl()+"\nScreenshot (in Base64) at the end of the test:\n{}",
                    getBase64Screenshot(driver));

            log.info("Browser console at the end of the test");
            LogEntries logEntries = driver.manage().logs().get(BROWSER);
            logEntries.forEach((entry) -> log.info("[{}] {} {}",
                    new Date(entry.getTimestamp()), entry.getLevel(),
                    entry.getMessage()));
            System.out.print("Toy en paralelo!");
            try {
				TimeUnit.SECONDS.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
	}
	
	public static String getBase64Screenshot(WebDriver driver) throws IOException {
	    log.debug("getBase64Screenshot INI");
	 	String screenshotBase64 = ((TakesScreenshot) driver)
                .getScreenshotAs(BASE64);

        log.debug("getBase64Screenshot END");
        return "data:image/png;base64," + screenshotBase64;

   }
	
	public static String cleanEmptyCourse(WebDriver driver) throws ElementNotFoundException {
		
		return CourseNavigationUtilities.newCourse(driver, host);

	}
}
