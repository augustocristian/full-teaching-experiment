package com.fullteaching.backend.e2e;

import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import  static com.fullteaching.e2e.common.Constants.*;

import com.fullteaching.e2e.common.CourseNavigationUtilities;
import com.fullteaching.e2e.common.NavigationUtilities;
import com.fullteaching.e2e.common.exception.BadUserException;
import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;
import com.fullteaching.e2e.utils.Click;
import com.fullteaching.e2e.utils.Wait;

import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;




@ExtendWith(SeleniumExtension.class)
public class CourseStudentTest extends FullTeachingTestE2E {

	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;

	static Exception ex = null;

	final String teacherMail = "teacher@gmail.com";
	final String teacherPass = "pass";
	final String teacherName = "Teacher Cheater";
	final String studentMail = "student1@gmail.com";
	final String studentPass = "pass";
	final String studentName = "Student Imprudent";

	WebDriver driver;

	BrowserUser user;

	public CourseStudentTest() {
		super();
	}

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
	public void studentCourseMainTest()throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {


		this.user = setupBrowser("chrome", teacherName, teacherMail, 30);

		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);



		try {
			if(!NavigationUtilities.amIHere(driver,COURSES_URL.replace("__HOST__", APP_URL)))
				driver = NavigationUtilities.toCoursesHome(driver);

			//go to first course
			//get course list
			List<String>course_list = CourseNavigationUtilities.getCoursesList(driver, APP_URL);
			if (course_list.size()<0)  fail("No courses available for test user");

			WebElement course_button = CourseNavigationUtilities.getCourseElement(driver, course_list.get(0)).findElement(By.className("title"));

			driver = Click.clickelement(driver, course_button);

			Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(COURSE_TABS_TAG)));

		}catch(Exception e) {
			fail("Failed to load Courses Tabs"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		//Check tabs
		//Home tab 
		try {

			//WebDriverWait wait = new WebDriverWait(driver, 10); 
			//wait.until(ExpectedConditions.presenceOfElementLocated(By.id(HOME_ICON_ID)));

			driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);



		} catch(Exception e) {
			fail("Failed to load home tab" + e.getClass() + ": "+e.getLocalizedMessage());
		}

		try {
			driver = CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);
		} catch(Exception e) {
			fail("Failed to load session tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		try {
			driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
		} catch(Exception e) {
			fail("Failed to load forum tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		try {
			driver = CourseNavigationUtilities.go2Tab(driver, FILES_ICON);
		} catch(Exception e) {
			fail("Failed to load files tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		try {
			driver = CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);	
		} catch(Exception e) {
			fail("Failed to load attenders tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}




	}



}
