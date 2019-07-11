package com.fullteaching.e2e.common.exception;

public class BadUserException extends Exception {

	private static final long serialVersionUID = 3882597333836058671L;

	public BadUserException() {
		super();
	}


	public BadUserException(String message) {
		super(message);
	}
}
