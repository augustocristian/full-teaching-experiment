package com.fullteaching.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import  static com.fullteaching.e2e.common.Constants.*;

import com.fullteaching.e2e.common.CourseNavigationUtilities;
import com.fullteaching.e2e.common.ForumNavigationUtilities;
import com.fullteaching.e2e.common.NavigationUtilities;
import com.fullteaching.e2e.common.exception.BadUserException;
import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.ExceptionsHelper;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;
import com.fullteaching.e2e.utils.ParameterLoader;
import com.fullteaching.e2e.utils.Click;
import com.fullteaching.e2e.utils.Wait;

import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


@ExtendWith(SeleniumExtension.class)
public class CourseTeacherTest extends FullTeachingTestE2E {
//PASS


	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;

	static Exception ex = null;


	static Class<? extends WebDriver> chrome = ChromeDriver.class;
	static Class<? extends WebDriver> firefox = FirefoxDriver.class;

	final String teacherMail = "teacher@gmail.com";
	final String teacherPass = "pass";
	final String teacherName = "Teacher Cheater";
	final String studentMail = "student1@gmail.com";
	final String studentPass = "pass";
	final String studentName = "Student Imprudent";

	String course_title;

	String courseName="Pseudoscientific course for treating the evil eye";
	WebDriver driver;

	BrowserUser user;

	public CourseTeacherTest() {
		super();
	}

	public static Stream<Arguments> data() throws IOException {
		return ParameterLoader.getTestTeachers();
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
			//this.logout(user);
			user.dispose();
		} finally {
			log.info("##### Finish test: "
					+ info.getTestMethod().get().getName());
		}
	}

	@Test
	public void teacherCourseMainTest() throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {


		this.user = setupBrowser("chrome", teacherName, teacherMail, 30);

		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);



		try {
			if(!NavigationUtilities.amIHere(driver,COURSES_URL.replace("__HOST__", APP_URL)))
				driver = NavigationUtilities.toCoursesHome(driver);


			WebElement course_button = Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath(FIRSTCOURSE_XPATH+GOTOCOURSE_XPATH)));
			Click.element(driver, By.xpath(FIRSTCOURSE_XPATH+GOTOCOURSE_XPATH));
			Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
		}catch(Exception e) {
			fail("Failed to load Courses Tabs"+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		//Check tabs
		//Home tab 
		try {
			driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);	
		} catch(Exception e) {
			fail("Failed to load home tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
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

	/**
	 * This tests get the login the user, go the the courses  and press the button of a 
	 * new course, creating a new course(each course have a time stap that avoids
	 * overlapping between different test).After that, we proceed to delete thoose courses, click
	 * into the edit icon, check the box that allows it and clicking into the delete button
	 * After that, we proceed to check if the course dont appears in the list. 
	 * 
	 */ 
	@Test
	public void teacherCreateAndDeleteCourseTest() throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {


		this.user = setupBrowser("chrome", teacherName, teacherMail, 30);

		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);



		boolean found = false;
		try {
			// navigate to courses if not there
			if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", APP_URL)))
				driver = NavigationUtilities.toCoursesHome(driver);
		}catch (Exception e) {
			fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		try {
			// press new course button and wait for modal course-modal 
			WebElement new_course_button = Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(NEWCOURSE_BUTTON));

			Click.byJS(driver,new_course_button);

		}catch (TimeoutException toe) {
			fail("Button for new course not found");
		}

		try {
			Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(NEWCOURSE_MODAL));
		}
		catch (TimeoutException toe) {
			fail("New course modal doesn't appear");
		}


		//fill information
		try {
			WebElement name_field = Wait.aLittle(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(NEWCOURSE_MODAL_NAMEFIELD_ID)));
			course_title = "Test Course_"+System.currentTimeMillis();
			name_field.sendKeys(course_title); //no duplicated courses
		}catch (TimeoutException toe) {
			fail("New course modal doesn't appear");
		}

		//Save
		try {
			WebElement save_button = Wait.aLittle(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(NEWCOURSE_MODAL_SAVE_ID)));
			driver = Click.element(driver, By.id(NEWCOURSE_MODAL_SAVE_ID));
		}catch (TimeoutException toe) {
			fail("New course modal doesn't appear");
		} catch (ElementNotFoundException e) {
			fail("Button failed");
		}

		//check if the course appears now in the list
		try {

			assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, course_title), "The course title hasn't been found in the list ¿Have been created?");

		}catch (TimeoutException toe) {
			fail("The courses list is not visible");
		}

		//DELETE
		try {
			CourseNavigationUtilities.deleteCourse(driver, course_title);

			Wait.notTooMuch(driver);
			assertFalse(CourseNavigationUtilities.checkIfCourseExists(driver, course_title), "the course still exists");

		}catch(Exception e){
			fail("there was an error while deleting the course");
		}

		//Well done!!!


	}

	/**
	 * This tests get the login the user, go the the courses  and in first place, edits the 
	 * course title, change its name for EDIT_+ one timestamp to avoid test overlapping.After that, we proceed
	 * to edit course details, deletes the details title, subtitle and content adding new values
	 * for thoose fields.Second checks if this content was correctly added.In second place, checks if the
	 * course forum is enabled/disbled, to proceed to check if allows disable/enable it.Finally
	 * this test check if the current user, that is accessing to the course, is included
	 * in the Attenders list( check if the user is in it)
	 */ 
	@Test
	public void teacherEditCourseValues() throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {



		this.user = setupBrowser("chrome", teacherName, teacherMail, 30);

		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);


		try {
			// navigate to courses if not there
			if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", APP_URL)))
				driver = NavigationUtilities.toCoursesHome(driver);
		}catch(Exception e) {
			fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		// select first course (never mind which course -while application is in a test environment-)
		// for more general testing use NavigationUtilities.newCourse but it will need some code rewriting.


		//Modify name
		try {

			WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);

			String old_name = course.findElement(By.className("title")).getText();
			String edition_name = "EDITION TEST_"+System.currentTimeMillis();

			driver = CourseNavigationUtilities.changeCourseName(driver, old_name, edition_name);
			//check if course exists

			assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, edition_name, 3), "The course title hasn't been found in the list ¿Have been created?");

			//return to old name	    	
			driver = CourseNavigationUtilities.changeCourseName(driver, edition_name, old_name);
			assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, old_name, 3), "The course title hasn't been reset");

		}catch (Exception e) {
			fail("Failed to edit course name "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		//Go to details and edit them
		try {//*[@id="sticky-footer-div"]/main/app-dashboard/div/div[3]/div/div[1]/ul/li[1]/div/div[2]

			WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
			course.findElement(COURSELIST_COURSETITLE).click();
			Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));

		}catch(Exception e) {
			fail("Failed to load Courses Tabs "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		// Modify description TAB HOME
		try {
			driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);

			//Modify the description
			WebElement editdescription_button = driver.findElement(EDITDESCRIPTION_BUTTON);
			driver = Click.clickelement(driver, editdescription_button);

			//wait for editor md editor???'
			WebElement editdescription_desc = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className(EDITDESCRIPTION_CONTENTBOX_CLASS)));

			//text here?? /html/body/app/div/main/app-course-details/div/div[4]/md-tab-group/div[2]/div[1]/div/div[2]/p-editor/div/div[2]/div[1]
			String old_desc = editdescription_desc.getAttribute("ng-reflect-model");

			//deletee old_desc
			WebElement editor = driver.findElement(By.className("ql-editor"));
			editor.sendKeys(SELECTALL);
			editor.sendKeys(DELETE);

			//New Title
			WebElement headerSelector = driver.findElement(By.className("ql-header"));
			Click.element(driver, By.className("ql-header"));
			WebElement picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
			WebElement option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Heading", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");

			assertNotNull(option,"Something went wrong while setting the Heading");

			driver  = Click.clickelement(driver, option);

			//Write the new Title.
			editor = driver.findElement(By.className("ql-editor"));
			editor.sendKeys("New Title");
			editor.sendKeys(NEWLINE);

			//New SubTitle
			headerSelector = driver.findElement(By.className("ql-header"));
			Click.element(driver, By.className("ql-header"));
			picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
			option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Subheading", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");

			assertNotNull(option, "Something went wrong while setting the Subheading");

			driver  = Click.clickelement(driver, option);

			//Write the new SubTitle.
			editor = driver.findElement(By.className("ql-editor"));
			editor.sendKeys("New SubHeading");
			editor.sendKeys(NEWLINE);

			//Content
			headerSelector = driver.findElement(By.className("ql-header"));
			Click.element(driver, By.className("ql-header"));
			picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
			option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Normal", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");

			assertNotNull(option,"Something went wrong while setting the Content");

			driver  = Click.clickelement(driver, option);

			//Write the new Content.
			editor = driver.findElement(By.className("ql-editor"));
			editor.sendKeys("This is the normal content");
			editor.sendKeys(NEWLINE);
			////*[@id="textEditorRowButtons"]/a[2]


			//preview? /html/body/app/div/main/app-course-details/div/div[4]/md-tab-group/div[2]/div[1]/div/div[2]/div/a[2]
			//driver.findElement(By.xpath(EDITDESCRIPTION_PREVIEWBUTTON_XPATH)).click();
			driver.findElement(By.xpath("//*[@id=\"textEditorRowButtons\"]/a[2]")).click();


			WebElement preview = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
			//chech heading
			assertEquals("New Title", preview.findElement(By.tagName("h1")).getText(),"Heading not properly rendered");
			//check subtitle
			assertEquals("New SubHeading", preview.findElement(By.tagName("h2")).getText(),"Subheading not properly rendered");
			//check content
			assertEquals("This is the normal content", preview.findElement(By.tagName("p")).getText(),"Normal content not properly rendered");

			//save send-info-btn
			driver.findElement(EDITDESCRIPTION_SAVEBUTTON).click();

			WebElement saved = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
			//chech heading
			assertEquals( "New Title", saved.findElement(By.tagName("h1")).getText(),"Heading not properly rendered");
			//check subtitle
			assertEquals("New SubHeading", saved.findElement(By.tagName("h2")).getText(),"Subheading not properly rendered");
			//check content
			assertEquals( "This is the normal content", saved.findElement(By.tagName("p")).getText(),"Normal content not properly rendered");


		} catch(Exception e) {	
			fail("Failed to modify description:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
					+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		// in sessions program 
		try {
			driver = CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);	
			// new session ¡in session Tests!
			// delete session ¡in session Tests!
		} catch(Exception e) {	
			fail("Failed to test session:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
					+ e.getClass()+ ": "+e.getLocalizedMessage());
		}

		// in forum enable/disable
		try {
			driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);	

			WebElement forum_tab_content = CourseNavigationUtilities.getTabContent(driver, FORUM_ICON);		

			//check if Forum is enabled 
			if(ForumNavigationUtilities.isForumEnabled(forum_tab_content)) {
				//if (enabled)
				//check entries ¡Only check if there is entries and all the buttons are present!
				assertNotNull(forum_tab_content.findElement(FORUM_NEWENTRY_ICON),"Add Entry not found");
				assertNotNull(forum_tab_content.findElement(FORUM_EDITENTRY_ICON),"Add Entry not found");

				//disable
				driver = ForumNavigationUtilities.disableForum(driver);

				//enable
				//clic edit
				driver = ForumNavigationUtilities.enableForum(driver);
			}
			else {
				//else
				//enable
				driver = ForumNavigationUtilities.enableForum(driver);

				//check entries  ¡Only check if there is entries and all the buttons are present!
				assertNotNull(forum_tab_content.findElement(FORUM_NEWENTRY_ICON),"Add Entry not found");
				assertNotNull(forum_tab_content.findElement(FORUM_EDITENTRY_ICON),"Add Entry not found");

				//disable 
				driver = ForumNavigationUtilities.disableForum(driver);
			}

		} catch(Exception e) {	
			fail("Failed to tests forum:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
					+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		// in attenders
		try {

			driver = CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);
			WebElement attenders_tab_content = CourseNavigationUtilities.getTabContent(driver, ATTENDERS_ICON);

			//is user in attenders?
			assertTrue(CourseNavigationUtilities.isUserInAttendersList(driver, teacherName), "User isn't in the attenders list");

			//is user highligthed?
			String main_user = CourseNavigationUtilities.getHighlightedAttender(driver);

			assertEquals(teacherName, main_user, "Main user and active user doesn't match");

		} catch(Exception e) {
			fail("Failed to tests attenders:: (File: CourseTeacherTest.java -line: "+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
					+ e.getClass()+ ": "+e.getLocalizedMessage());
		}


		//Well done!
	}

	@Disabled
	public void teacherDeleteCourseTest() throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {


		this.user = setupBrowser("chrome", teacherName, teacherMail, 30);

		WebDriver driver=user.getDriver();



		this.slowLogin(user, teacherMail, teacherPass);

		// navigate to courses if not there
		try {
			if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", 	APP_URL)))
				driver = NavigationUtilities.toCoursesHome(driver);
		}catch (Exception e) {
			fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		// create a course 
		try {
			courseName= CourseNavigationUtilities.newCourse(driver, APP_URL);

		} catch (ElementNotFoundException e) {
			fail("Failed to create course:: "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		// populate course
		// in sessions program 
		// TODO: new session
		// in attenders
		// TODO: add attenders

		// delete course  	
		try {
			WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);


			WebElement edit_name_button = course.findElement(EDITCOURSE_BUTTON);

			driver = Click.clickelement(driver,edit_name_button);

			//wait for edit modal
			WebElement edit_modal = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(EDITDELETE_MODAL));;

			//allow deletion
			driver = Click.element(driver, ENABLECOURSE_DELETION_BUTTON);

			//delete	
			driver = Click.withNRetries(driver, DELETECOURSE_BUTTON, 3, COURSELIST);

			assertFalse(CourseNavigationUtilities.checkIfCourseExists(driver, courseName), "The course have not been deleted");

		} catch(Exception e) {
			fail("Failed to deletecourse:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
					+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
		//Well done!
	}



}
