package com.fullteaching.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.fullteaching.e2e.common.NavigationUtilities;
import com.fullteaching.e2e.common.SpiderNavigation;
import com.fullteaching.e2e.common.exception.BadUserException;
import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;
import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;


@ExtendWith(SeleniumExtension.class)
public class LoggedLinksTests extends FullTeachingTestE2E {
	
	private static String TEACHER_BROWSER;
    private static String STUDENT_BROWSER;

	protected static int DEPTH = 3;
	
    static Exception ex = null;

    final String teacherMail = "teacher@gmail.com";
    final String teacherPass = "pass";
    final String teacherName = "Teacher Cheater";
    final String studentMail = "student1@gmail.com";
    final String studentPass = "pass";
    final String studentName = "Student Imprudent";

    WebDriver driver;
    BrowserUser user;

    public LoggedLinksTests() {
        super();
    }

    @BeforeAll()
    static void setupAll() {

        if (System.getenv("ET_EUS_API") == null) {
            // Outside ElasTest
            ChromeDriverManager.getInstance(chrome).setup();
            FirefoxDriverManager.getInstance(firefox).setup();
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
	public void spiderLoggedTest()  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	
this.user = setupBrowser("chrome", teacherName, teacherMail, 30);
		
		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);


		/*navigate from home*/
		NavigationUtilities.getUrlAndWaitFooter(driver, APP_URL);
				
		List <WebElement> pageLinks = SpiderNavigation.getPageLinks(driver);
		
		Map <String,String> explored = new HashMap<String,String>();
		
		//Navigate the links... 
		//Problem: once one is pressed the rest will be unusable as the page reloads... 

		explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH);
		
		List<String> failed_links = new ArrayList<String>();
		System.out.println(teacherMail+" tested "+explored.size()+" urls");
		explored.forEach((link,result) -> {
				log.debug("\t"+link+" => "+result);
				if (result.equals("KO")) {
					failed_links.add(link);				
				}			
		});

		String msg = "";
		for (String failed: failed_links) {
			msg = failed +"\n";	
		}
		assertTrue(failed_links.isEmpty(), msg);
	}
	
	
	    
	
}
