package com.fullteaching.e2e.common;

import static com.fullteaching.e2e.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.fullteaching.e2e.common.exception.BadUserException;
import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.common.exception.NotLoggedException;
import com.fullteaching.e2e.common.exception.TimeOutExeception;
import com.fullteaching.e2e.utils.Click;
import com.fullteaching.e2e.utils.Wait;


public class UserUtilities {
	
	public static String login_url = "__HOST__";
	
	final static Logger log = getLogger(lookup().lookupClass());
	
	public static WebDriver login(WebDriver wd, String user, String password, String host) throws ElementNotFoundException, TimeOutExeception {
		log.info("[INI]login");
		//navigate to login page
		NavigationUtilities.getUrlAndWaitFooter(wd, login_url.replace("__HOST__", host));
		
		try {
			Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOGINMENU_XPATH)));
						
			wd = Click.withNRetries(wd, By.xpath(LOGINMENU_XPATH), 3, LOGIN_MODAL );
			
			WebElement login_modal = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(LOGIN_MODAL));
			
			WebElement user_field = login_modal.findElement(LOGIN_USER_FIELD);
			WebElement pass_field = login_modal.findElement(LOGIN_PASSWORD_FIELD);
			WebElement submit_field = login_modal.findElement(LOGIN_BUTTON);
			
			user_field.sendKeys(user);
			pass_field.sendKeys(password);
			
			wd = Click.clickelement(wd, submit_field);
			
		}
		catch(TimeoutException tOe) {
			System.err.println("[User.login] Time Out");
			throw new TimeOutExeception("[User.login] Time Out");
		}
		catch(NoSuchElementException nEe) {
			System.err.println("[User.login] Element not found");
			throw new ElementNotFoundException("[User.login] Time Out");
		}
		log.info("[END]login");		
		return wd;
		
	}
	
	public static WebDriver checkLogin(WebDriver wd, String user) throws NotLoggedException, BadUserException, ElementNotFoundException{
		log.info("[INI]checkLogin");
		//Wait to settings button to be present
		try {
			WebElement settings_button  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_BUTTON));
		
			wd = Click.clickelement(wd, settings_button);
		}catch(TimeoutException toe) {
			throw new NotLoggedException(toe.getMessage());
		}
		
		WebElement settings_page  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_USEREMAIL));
		
		//Check if the user name is the expected
		if (!settings_page.getText().trim().equals(user.trim())) throw new BadUserException();
		log.info("[END]checkLogin");
		return wd;
	}
	
	public static String getUserName(WebDriver wd, boolean goBack, String host) throws NotLoggedException, BadUserException, ElementNotFoundException{
		log.info("[INI]getUserName");
		//Wait to settings button to be present
		try {
			
			WebElement settings_button  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_BUTTON));
			
			if(!NavigationUtilities.amIHere(wd, host+"/settings")) {
				wd = Click.clickelement(wd, settings_button);
			}
			else {
				goBack = false;
			}
			
		}catch(TimeoutException toe) {
			throw new NotLoggedException(toe.getMessage());
		}
		
		WebElement name_placeholder  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(USERNAME_XPATH)));
		
		String userName = name_placeholder.getText().trim();
		
		if (goBack) {
			wd.navigate().back();
		}
		//Check if the user name is the expected
		log.info("[END]getUserName");
		return userName;
		
	}
	
	public static WebDriver logOut(WebDriver wd, String host) throws NotLoggedException, ElementNotFoundException {
		log.info("[INI]logOut");
		//press logout link
		try {
			WebElement arrow_button  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(MAINMENU_ARROW));
		
			wd = Click.clickelement(wd, arrow_button);
			
			WebElement logout_button  = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_BUTTON));
			
			wd = Click.clickelement(wd, logout_button);
			
			//go to home as the log out has been done
			NavigationUtilities.getUrlAndWaitFooter(wd, login_url.replace("__HOST__", host));
			
		}catch(TimeoutException toe) {
			throw new NotLoggedException("Already logged Out");
		}
		log.info("[END]logOut");
		return wd;
		
	}
	
	public static WebDriver checkLogOut(WebDriver wd) throws ElementNotFoundException {
		log.info("[INI]checkLogOut");
		try {
			Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOGINMENU_XPATH)));
		}
		catch (TimeoutException toe) {
			throw new ElementNotFoundException("Not Logged Out. Not in the home");	
		}
		log.info("[END]checkLogOut");
		return wd;
		
	}
	
	public static String getLoggedUser(WebDriver wd) throws NotLoggedException, ElementNotFoundException {
		log.info("[INI]getLoggedUser");
		String current_user = null;
		String current_url = wd.getCurrentUrl();

		try {
			//go to settings
			Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_BUTTON));
			
			Click.withNRetries(wd, SETTINGS_BUTTON, 3, SETTINGS_USEREMAIL);
			
			WebElement settings_page  = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_USEREMAIL));
			current_user = settings_page.getText().trim();
			while (!wd.getCurrentUrl().equals(current_url)) {
				wd.navigate().back();
			}
			
			
		}catch(TimeoutException toe) {
			while (!wd.getCurrentUrl().equals(current_url)) {
				wd.navigate().back();
			}
            log.info("[END]getLoggedUser => NOT LOGGED");
			throw new NotLoggedException(toe.getMessage());
		}
		log.info("[END]getLoggedUser: {}", current_user);
		return current_user;
		
	}
	
	
}
