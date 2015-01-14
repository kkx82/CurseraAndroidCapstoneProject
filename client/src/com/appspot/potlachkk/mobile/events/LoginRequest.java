package com.appspot.potlachkk.mobile.events;


public class LoginRequest {

	//store credentials
	private String user;
	private String pass;
	
	//store activity that requested resourse
	//private Type activity;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	/*
	public Type getActivity() {
		return activity;
	}
	public void setActivity(Type activity) {
		this.activity = activity;
	}
	*/
	public LoginRequest(String user, String pass) {
		super();
		this.user = user;
		this.pass = pass;
		//this.activity = null;
	}

	/*
	public LoginRequest(String user, String pass, Type activity) {
		super();
		this.user = user;
		this.pass = pass;
		this.activity = activity;
	}
	*/
}
