package com.fullteaching.e2e.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Scroll {
	public static WebDriver toElement(WebDriver wd, WebElement ele) {
		Actions actions = new Actions(wd);
		actions.moveToElement(ele);
		actions.perform();
		return wd;
	}
}
