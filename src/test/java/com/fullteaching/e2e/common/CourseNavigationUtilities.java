package com.fullteaching.e2e.common;

import static com.fullteaching.e2e.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.utils.Click;
import com.fullteaching.e2e.utils.DOMMannager;
import com.fullteaching.e2e.utils.Wait;

public class CourseNavigationUtilities {

	public static final  Logger log = getLogger(lookup().lookupClass());

	public static String newCourse(WebDriver wd, String host) throws ElementNotFoundException {
		boolean found = false;
    	
		
    	// navigate to courses if not there
		if (!NavigationUtilities.amIHere(wd, COURSES_URL.replace("__HOST__", host))) {
			
			wd = NavigationUtilities.toCoursesHome(wd);
		}

    	// press new course button and wait for modal course-modal 
	    WebElement new_course_button = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(NEWCOURSE_BUTTON));
	    Click.byJS(wd,new_course_button);
   	
	    Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(NEWCOURSE_MODAL));
    	
    	
    	//fill information
    	WebElement name_field = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(NEWCOURSE_MODAL_NAMEFIELD));
	    String course_title = "Test Course_"+System.currentTimeMillis();
	    name_field.sendKeys(course_title); //no duplicated courses
    	
    	WebElement save_button = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(NEWCOURSE_MODAL_SAVE));
	    Click.element(wd, By.id(NEWCOURSE_MODAL_SAVE_ID));
    	    	
    	//check if the course appears now in the list
    	WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSELIST));
	    	
	    //find the newly create course
	    List<WebElement> courses = courses_list.findElements(By.tagName("li"));
	    	
	    for (WebElement c : courses) {
	    	try {
	    			WebElement title = c.findElement(By.className("title"));
	    			String title_text = title.getText();
	    			if(course_title.equals(title_text)) {
	    				found = true;
	    				break;
	    			}
	    		}
	    		catch(NoSuchElementException csee) {
	    			//do nothing and look for the next item
	    		}
	    }
	    if (! found) {
	    	log.error("newCourse - Course hasn't been created");
	    	throw new ElementNotFoundException("newCourse - Course hasn't been created");
	    }
		return course_title;
	}
	
	public static boolean checkIfCourseExists(WebDriver wd, String course_title) {
		WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSELIST));
    	
    	//find the newly create course
    	List<WebElement> courses = courses_list.findElements(By.tagName("li"));
    	
    	for (WebElement c : courses) {
    		try {
    			WebElement title = c.findElement(By.className("title"));
    			String title_text = title.getText();
    			if(course_title.equals(title_text)) {
    				return true;
    			}
    		}
    		catch(NoSuchElementException csee) {
    			//do nothing and look for the next item
    		}
    	}
    	return false;
	}

	public static boolean checkIfCourseExists(WebDriver wd, String course_title, int retries){
		for (int i = 0; i< retries; i++){
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {

			}
			if (checkIfCourseExists(wd, course_title)) {
				return true;
			}
		}
		return false;
	}

	public static WebDriver changeCourseName(WebDriver wd, String oldName, String newName) throws ElementNotFoundException {
		
		log.info("[INI] changeCourseName({}=>{})",oldName,  newName);
		
		boolean found = false;
		WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSELIST));
    	
		try {
			//find the course 
			WebElement c = getCourseElement(wd, oldName);
			
			WebElement edit_name_button = c.findElement(EDITCOURSE_BUTTON);
    	    		
    	    wd = Click.element(wd,edit_name_button);
    	    		
    	    //wait for edit modal
    	    WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(EDITDELETE_MODAL));
    				//change name
    		WebElement name_field = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDITCOURSE_MODAL_NAMEFIELD));
    		name_field.clear();
    		name_field.sendKeys(newName);
    				
    		//save
    		wd = Click.element(wd, EDITCOURSE_MODAL_SAVE);
    		
		}catch(NoSuchElementException csee) {
    		log.info("[END] changeCourseName KO: changeCourseName - Course \"{}\" probably doesn't exists",oldName);
			throw new ElementNotFoundException("changeCourseName - Course "+oldName +"probably doesn't exists");
    	}
    		
    	
    	log.info("[END] changeCourseName OK");
    	return wd;
		
	}

	public static WebDriver deleteCourse(WebDriver wd, String course_name) throws ElementNotFoundException{
		log.info("[INI] deleteCourse({}", course_name);

		boolean found = false;
		WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSELIST));

		try {
			//find the course
			WebElement c = getCourseElement(wd, course_name);

			WebElement edit_name_button = c.findElement(EDITCOURSE_BUTTON);

			wd = Click.element(wd, edit_name_button);

			//wait for edit modal
			WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(EDITDELETE_MODAL));

			//Allow delete
			WebElement delete_check = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDITCOURSE_DELETE_CHECK));
			wd = Click.element(wd, delete_check);
			
			//press delete
			WebElement delete_button = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDITCOURSE_DELETE_BUTTON));
			wd = Click.element(wd, delete_button);

			//save
			wd = Click.element(wd, EDITCOURSE_MODAL_SAVE);

		}catch(NoSuchElementException csee) {
			log.info("[END] deleteCourse KO: changeCourseName - Course \"{}\" probably doesn't exists",course_name);
			throw new ElementNotFoundException("changeCourseName - Course "+course_name +"probably doesn't exists");
		}
		log.info("[END] deleteCourse OK: changeCourseName - Course \"{}\" ",course_name);

		return wd;

	}


	public static List<String> getCoursesList(WebDriver wd, String host) throws ElementNotFoundException{
		
		ArrayList <String> courses_names = new ArrayList<String>();
		
		if (!NavigationUtilities.amIHere(wd, COURSES_URL.replace("__HOST__", host))) {
			
			wd = NavigationUtilities.toCoursesHome(wd);
		}
		WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSELIST));
    	List<WebElement> courses = courses_list.findElements(By.tagName("li"));

		for (WebElement c : courses) {
    		try {
    			WebElement title = c.findElement(By.className("title"));
    			String title_text = title.getText();
    			courses_names.add(title_text);   			
    		}
    		catch(NoSuchElementException csee) {
    			//do nothing and look for the next item
    		}
    	}
		
		return courses_names;
		
	}
	
	
	public static WebElement getCourseElement(WebDriver wd, String name) throws ElementNotFoundException {
		WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSELIST));
    	
    	//find the newly create course
    	List<WebElement> courses = courses_list.findElements(By.tagName("li"));
    	
    	for (WebElement c : courses) {
    		try {
    			WebElement title = c.findElement(By.className("title"));
    			String title_text = title.getText();
    			if(name.equals(title_text)) {
    				
    				return c;
    			}
    		}
    		catch(NoSuchElementException csee) {
    			//do nothing and look for the next item
    		}
    	}
    	
    	throw new ElementNotFoundException("getCourseElement-the course doesn't exist");
	}
	
	public static WebDriver go2Tab(WebDriver wd, By icon) throws ElementNotFoundException {
		
		
		WebElement tab = getTabElementFromIcon(wd, icon);
		//prueba
		//tab = DOMMannager.getParent(wd,tab );
		
		String id = tab.getAttribute("id");
		System.out.println("El valor de tab es : " + tab);
		System.out.println("El valor del id es : " + id);
		wd = Click.element(wd,tab);
		id=id.replace("label", "content");
		Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));	
		
		return wd;
	
	}
	
	public static String getTabId(WebDriver wd, By icon) {

		WebElement tab = getTabElementFromIcon(wd, icon);
		return tab.getAttribute("id");
	}
	
	public static WebElement getTabContent(WebDriver wd, By icon) {
		
		String tab_id = getTabId(wd, icon);
		return wd.findElement(By.id(tab_id.replace("label", "content"))); 
	}

	public static WebElement wait4TabContent(WebDriver wd, By icon){

		String tab_id = getTabId(wd, icon);
		return Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.id(tab_id.replace("label", "content"))));
	}

	public static WebElement getTabElementFromIcon(WebDriver wd, By icon){
		WebElement icon_element = wd.findElement(COURSE_TABS).findElement(icon);
		
		WebElement parent1=DOMMannager.getParent(wd, icon_element);
		
		WebElement parent2 =DOMMannager.getParent(wd, parent1);
		WebElement parent3= DOMMannager.getParent(wd,parent2 );
		return parent2 ;
	}

	public static boolean isUserInAttendersList(WebDriver wd, String user_name) throws ElementNotFoundException {

		log.info("[INI] isUserInAttendersList");

		WebElement attenders_content = getTabContent(wd, ATTENDERS_ICON);

		List <WebElement> attenders_lst = wd.findElements(ATTENDERS_LIST_ROWS);

		if(attenders_lst.size()<1){
			log.info("[END] isUserInAttendersList KO: attenders list is empty");
			throw new ElementNotFoundException("isUserInAttendersList - attenders list is empty");
		}

		for (int i = 0; i< attenders_lst.size(); i++){
			String user_from_row = attenders_lst.get(i).getText();
			if (user_name.trim().equalsIgnoreCase(user_from_row.trim())){
				log.info("[END] isUserInAttendersList OK");
				return true;
			}
		}
		log.info("[END] isUserInAttendersList KO: user not found");
		return false;
	}

	public static String getHighlightedAttender(WebDriver wd) throws ElementNotFoundException {

		log.info("[INI] getHighlightedAttender");

		WebElement attenders_content = getTabContent(wd, ATTENDERS_ICON);

		List<WebElement> attender_highlighted = attenders_content.findElements(ATTENDERS_LIST_HIGHLIGHTEDROW);

		if (attender_highlighted == null || attender_highlighted.size()<1){
			log.info("[END] getHighlightedAttender KO: no highlighted user");
			throw new ElementNotFoundException("getHighlightedAttender - no highlighted user");
		}

		return attender_highlighted.get(0).getText();
	}
}