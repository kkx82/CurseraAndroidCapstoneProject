package com.appspot.potlachkk.mobile.events;

import java.util.List;

import com.appspot.potlachkk.mobile.models.User;

public class TopGiversLoaded {

	private List<User> userList;

	public TopGiversLoaded(List<User> userList) {
		super();
		this.userList = userList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	
}
