package com.fullteaching.e2e.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;

import com.fullteaching.e2e.common.BrowserUser;
import com.fullteaching.e2e.common.ChromeUser;
import com.fullteaching.e2e.common.FirefoxUser;

public class UserLoader {

	public static int USERNAME = 0;
	public static int PASSWORD = 1; 
	public static int ROLES = 2; 
	
	private static String cvsMainFieldsSplitBy = ",";
	private static String cvsRolesSplitBy = "\\|";
	
	private static Map<String,User> users;
	
	private static String usersDefaultFile = "src/test/resources/inputs/default_user_file.csv";
	private static String oneTeacherMultipleStudentsFile = "src/test/resources/inputs/session_test_file.csv";
	
	public static void loadUsers (List<User> userlst, boolean override) {
		if (override == true || users == null ) 
			users = new HashMap<String, User>();
		
		for (User i : userlst) users.put(i.getName(),i);
				
	}
	
	public static void loadUsers(String usersFile, boolean override) throws IOException {
		//read file and create users
		String line = "";
     
		List<User> userlst = new ArrayList<User>();
		
        BufferedReader br = new BufferedReader(new FileReader(usersDefaultFile));

        while ((line = br.readLine()) != null) {
        	userlst.add(parseUser(line));
        }  
        loadUsers(userlst, override);
	}
	
	
	
	public static void loadUsers(String usersFile) throws IOException {
		loadUsers(usersFile, false);
	}
	
	public static void loadUsers() throws IOException {
		loadUsers(usersDefaultFile);
	}
	
	
	
	public static User parseUser(String cvsline) {
		String field[] = cvsline.split(cvsMainFieldsSplitBy);
		return new User(field[USERNAME], 
						field[PASSWORD], 
						field[ROLES]);
	}

	
	public static Collection<String[]> getSessionParameters() throws IOException{
		String line = "";
	     
		List<String[]> paramList = new ArrayList<String[]>();
		
        BufferedReader br = new BufferedReader(new FileReader(oneTeacherMultipleStudentsFile));
        while ((line = br.readLine()) != null) {
        	paramList.add(line.split(cvsMainFieldsSplitBy));
        }  
        return paramList;
	}
	
	public static User retrieveUser(String name) {
		return users.get(name);
	}
	
	public static Collection<User> getAllUsers() throws IOException{
		if (users==null) {
			loadUsers();
		}
		return users.values();
	}
	
	 public static BrowserUser setupBrowser(String browser, String testName,
	            String userIdentifier, int secondsOfWait,String APP_URL,Logger log) {

	        BrowserUser u;

	        log.info("Starting browser ({})", browser);

	        switch (browser) {
	        case "chrome":
	            u = new ChromeUser(userIdentifier, secondsOfWait, testName,
	                    userIdentifier);
	            break;
	        case "firefox":
	            u = new FirefoxUser(userIdentifier, secondsOfWait, testName,
	                    userIdentifier);
	            break;
	        default:
	            u = new ChromeUser(userIdentifier, secondsOfWait, testName,
	                    userIdentifier);
	        }

	        log.info("Navigating to {}", APP_URL);

	        u.getDriver().get(APP_URL);
	        //For solve some problems with certain window resolutions
	        u.getDriver().manage().window().maximize();

	        final String GLOBAL_JS_FUNCTION = "var s = window.document.createElement('script');"
	                + "s.innerText = 'window.MY_FUNC = function(containerQuerySelector) {"
	                + "var elem = document.createElement(\"div\");"
	                + "elem.id = \"video-playing-div\";"
	                + "elem.innerText = \"VIDEO PLAYING\";"
	                + "document.body.appendChild(elem);"
	                + "console.log(\"Video check function successfully added to DOM by Selenium\")}';"
	                + "window.document.head.appendChild(s);";

	        u.runJavascript(GLOBAL_JS_FUNCTION);

	        return u;
	    }
	

}
