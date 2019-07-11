package com.fullteaching.e2e.common;

import com.fullteaching.e2e.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.utils.Click;
import com.fullteaching.e2e.utils.Wait;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

import static com.fullteaching.e2e.common.Constants.*;
import static org.junit.jupiter.api.Assertions.*;


public class ForumNavigationUtilities {


	public static boolean isForumEnabled(WebElement forumTabContent) {

		try {
			forumTabContent.findElement(FORUM_NEWENTRY_ICON);
			return true;
		}catch(Exception e) {
			return false;
		}
	}


	public static List<String> getFullEntryList(WebDriver wd){
		ArrayList <String> entries_titles = new ArrayList<String>();

		WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
		List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
		for(WebElement entry: entries) {
			entries_titles.add(entry.findElement(FORUMENTRYLIST_ENTRYTITLE).getText());
		}

		return entries_titles;
	}

	public static List<String> getUserEntries(WebDriver wd, String user_name){
		ArrayList <String> entries_titles = new ArrayList<String>();

		WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
		List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
		for(WebElement entry: entries) {
			//if user name is the publisher of the entry... 
			entries_titles.add(entry.findElement(FORUMENTRYLIST_ENTRYTITLE).getText());
		}

		return entries_titles;
	}

	public static WebElement getEntry(WebDriver wd, String entry_name) throws ElementNotFoundException {
		WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
		List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
		for(WebElement entry: entries) {
			try {
				WebElement title = entry.findElement(FORUMENTRYLIST_ENTRYTITLE);
				String title_text = title.getText();
				if (title_text ==null || title_text.equals("")) {
					title_text = title.getAttribute("innerHTML");
				}
				if(entry_name.equals(title_text)) {

					return entry;
				}
			}
			catch(NoSuchElementException csee) {
				//do nothing and look for the next item
			}
		}

		throw new ElementNotFoundException("getEntry-the entry doesn't exist");
	}

	public static List<WebElement> getComments(WebDriver wd){


		Wait.notTooMuch(wd);
		return wd.findElements(FORUMCOMMENTLIST_COMMENT);
	}

	public static List<WebElement> getUserComments(WebDriver wd, String user_name){
		List<WebElement> user_comments = new ArrayList<WebElement>();

		List<WebElement> all_comments = wd.findElements(FORUMCOMMENTLIST_COMMENT);

		for (WebElement comment: all_comments) {
			String comment_username = comment.findElement(FORUMCOMMENTLIST_COMMENT_USER).getText();
			if (user_name.equals(comment_username)) {
				user_comments.add(comment);
			}
		}
		return user_comments;
	}

	public static List<WebElement> getHighLightedComments(WebDriver wd, String user_name){
		List<WebElement> user_comments = new ArrayList<WebElement>();

		List<WebElement> all_comments = wd.findElements(FORUMCOMMENTLIST_COMMENT);

		for (WebElement comment: all_comments) {
			String comment_username = comment.findElement(FORUMCOMMENTLIST_COMMENT_USER).getText();
			if (user_name.equals(comment_username)) {
				user_comments.add(comment);
			}
		}
		return user_comments;
	}

	public static WebDriver newEntry(WebDriver wd, String newEntryTitle, String newEntryContent) throws ElementNotFoundException {
		wd = CourseNavigationUtilities.go2Tab(wd, FORUM_ICON);
		assertEquals(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(wd,FORUM_ICON)), true, "Forum not activated");

		wd = Click.element(wd, FORUM_NEWENTRY_ICON);

		//wait for modal
		Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEWENTRY_MODAL));

		//fill new Entry
		WebElement title = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEWENTRY_MODAL_TITLE));
		title.sendKeys(newEntryTitle);
		WebElement comment = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEWENTRY_MODAL_CONTENT));
		comment.sendKeys(newEntryContent);

		//Publish
		wd = Click.element(wd,FORUM_NEWENTRY_MODAL_POSTBUTTON);

		//Wait to publish
		Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUMENTRYLIST_ENTRIESUL));

		//Check entry... 
		WebElement newEntry = ForumNavigationUtilities.getEntry(wd, newEntryTitle);

		return wd;
	}


	public static List<WebElement> getReplies(WebDriver driver, WebElement comment) {
		List<WebElement> replies= new ArrayList<WebElement>();

		//get all comment-div 
		List<WebElement> subcomments = comment.findElements(FORUMCOMMENTLIST_COMMENT_DIV);

		//ignore first it is original comment
		for (int i = 1; i<subcomments.size(); i++) {
			replies.add(subcomments.get(i));
		}

		return replies;
	}

	public static WebDriver enableForum(WebDriver wd) throws ElementNotFoundException {

		//click edit
		WebElement edit_button =  Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_EDITENTRY_ICON));
		wd = Click.element(wd,edit_button);
		WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ENABLEFORUM_MODAL));

		//press enable
		WebElement enable_button = edit_modal.findElement(ENABLEFORUM_BUTTON);
		wd = Click.element(wd, enable_button);

		WebElement save_button = edit_modal.findElement(ENABLEFORUM_MODAL_SAVEBUTTON);
		wd = Click.element(wd, save_button);

		WebElement forum_tab_content = CourseNavigationUtilities.wait4TabContent(wd, FORUM_ICON);

		assertTrue(isForumEnabled(forum_tab_content),"The forum is not dissabled");

		return wd;
	}

	public static WebDriver disableForum(WebDriver wd) throws ElementNotFoundException {

		//click edit
		WebElement edit_button =  Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_EDITENTRY_ICON));
		wd = Click.element(wd,edit_button);
		WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ENABLEFORUM_MODAL));

		//press disable
		WebElement enable_button = edit_modal.findElement(DISABLEFORUM_BUTTON);
		wd = Click.element(wd, enable_button);

		WebElement save_button = edit_modal.findElement(ENABLEFORUM_MODAL_SAVEBUTTON);
		wd = Click.element(wd, save_button);

		WebElement forum_tab_content = CourseNavigationUtilities.wait4TabContent(wd, FORUM_ICON);

		assertFalse(ForumNavigationUtilities.isForumEnabled(forum_tab_content), "The forum is not dissabled");

		return wd;
	}

}
