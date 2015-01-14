package com.appspot.potlachkk.mobile.models;

public class LoginStatus {
	
	public enum LoginStatusCode {
		LOGIN_OK, 
		LOGIN_FAILURE,
		NOT_AUTHORIZED
	}
	
	private LoginStatusCode code; //OK|FAIL
	private String message;
	
	private LoginStatus(LoginStatusCode code, String message) {
		this.code = code;
		this.message = message;
	}

	public LoginStatusCode getCode() {
		return this.code;
	}

	public void setCode(LoginStatusCode code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
