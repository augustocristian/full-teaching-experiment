package com.fullteaching.e2e.utils;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 694253668952718366L;
	private String username;
	private String email;
	private String password;
	private String role;
	
	
	public User(String email, String password,String username, String role) {
		this.email = email; 
		this.password = password;
		this.username=username;
		this.role = role;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String mail) {
		this.email = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}	

	public void setRoles(String role) {this.role = role;}
	public String getUserCsv() {
		return ""+email+","+password+","+username+","+getRolesCsv();
	}
	private String getRolesCsv() {return role; }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	
	
}
