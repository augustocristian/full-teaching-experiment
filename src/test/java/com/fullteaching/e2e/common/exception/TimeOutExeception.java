package com.fullteaching.e2e.common.exception;

public class TimeOutExeception extends Exception {
	

	private static final long serialVersionUID = 9200938683074869086L;

	public TimeOutExeception() {
		super();
	}

	public TimeOutExeception(String message) {
		super(message);
	}
}
