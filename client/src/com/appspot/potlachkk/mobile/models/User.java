package com.appspot.potlachkk.mobile.models;


public class User {
	
	public User(String username) {
		super();
		
		this.username = username; 
		this.likeCount = 0;
	}

	private String username; 
	private Integer likeCount;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}
	
	//temporary workaround
	public String toTopGivers(){
		return username + ": gifts liked by: " + String.valueOf(likeCount);
	}
}
