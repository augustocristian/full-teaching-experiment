package com.fullteaching.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;


import com.fullteaching.e2e.common.UserUtilities;
import com.fullteaching.e2e.common.exception.BadUserException;
import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;


import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;




@ExtendWith(SeleniumExtension.class)
public class UserTest extends FullTeachingTestE2E {

	 private static String TEACHER_BROWSER;
	    private static String STUDENT_BROWSER;

	    static Exception ex = null;

	    final String teacherMail = "teacher@gmail.com";
	    final String teacherPass = "pass";
	    final String teacherName = "Teacher Cheater";
	    final String studentMail = "student1@gmail.com";
	    final String studentPass = "pass";
	    final String studentName = "Student Imprudent";

	    BrowserUser user;

	

	    @BeforeAll()
	    static void setupAll() {

	        if (System.getenv("ET_EUS_API") == null) {
	            // Outside ElasTest
	            ChromeDriverManager.getInstance().setup();
	            FirefoxDriverManager.getInstance().setup();
	        }

	        if (System.getenv("ET_SUT_HOST") != null) {
	            APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":5000/";
	        } else {
	            APP_URL = System.getProperty("app.url");
	            if (APP_URL == null) {
	                APP_URL = "https://localhost:5000/";
	            }
	        }

	        TEACHER_BROWSER = System.getenv("TEACHER_BROWSER");
	        STUDENT_BROWSER = System.getenv("STUDENT_BROWSER");

	        if ((TEACHER_BROWSER == null) || (!TEACHER_BROWSER.equals(FIREFOX))) {
	            TEACHER_BROWSER = CHROME;
	        }

	        if ((STUDENT_BROWSER == null) || (!STUDENT_BROWSER.equals(FIREFOX))) {
	            STUDENT_BROWSER = CHROME;
	        }

	        log.info("Using URL {} to connect to openvidu-testapp", APP_URL);
	    }

	    @AfterEach
	    void dispose(TestInfo info) {
	        try {
	            this.logout(user);
	            user.dispose();
	        } finally {
	            log.info("##### Finish test: "
	                    + info.getTestMethod().get().getName());
	        }
	    }

	    @Test
	public void loginTest() throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

		user= setupBrowser("chrome",teacherName,teacherMail,100);
		WebDriver driver=user.getDriver();
		try {
			this.slowLogin(user, teacherMail, teacherPass);
		
			driver = UserUtilities.checkLogin(driver, teacherMail);

			assertTrue(true, "not logged");

		} catch (NotLoggedException | BadUserException e) {
				
			e.printStackTrace();
			fail("Not logged");
			
		} catch (ElementNotFoundException e) {
			
			e.printStackTrace();
			fail(e.getLocalizedMessage());
			
		} 
		
		try {
			driver = UserUtilities.logOut(driver,APP_URL);
			
			driver = UserUtilities.checkLogOut(driver);
			
		} catch (ElementNotFoundException enfe) {
			fail("Still logged");
			
		} catch (NotLoggedException e) {
			assertTrue(true, "Not logged");
		}
			
		assertTrue(true);
	}
	
	
}
