package com.fullteaching.e2e.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DOMMannager {

	public static WebElement getParent(WebDriver wd, WebElement childElement) {
		JavascriptExecutor executor = (JavascriptExecutor)wd;
		WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", childElement);
		return parentElement;
	}
}
