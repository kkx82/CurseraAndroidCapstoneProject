package com.appspot.potlachkk.mobile.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Gift {

	private Long id;

	private String picture;
	
	private String title;
	
	private String text;
	
	private String username;
	
	private Date creationDate;
	
	private List<String> likedBy;
	
	private Integer likeCount;
	
	private List<String> flaggedBy;
	
	private Integer flagCount;
	
	private Long chainId;
	
	public Gift() {
		this.likedBy = new ArrayList<String>();
		this.flaggedBy = new ArrayList<String>();
	}
	
	public Gift(String picture, String title, String text) {
		super();
		this.picture = picture;
		this.title = title;
		this.text = text;
		this.flagCount = 0;
		this.likeCount = 0;
		this.likedBy = new ArrayList<String>();
		this.flaggedBy = new ArrayList<String>();
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	
	public List<String> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}

	//helper for adding reference to users who liked given gift
	public void addLiker(String username){
		this.likedBy.add(username);
	}
	
	//helper for deleting reference to user who doesn't like anymore
	public void deleteLiker(String username){
		this.likedBy.remove(username);
	}
	
	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public List<String> getFlaggedBy() {
		return flaggedBy;
	}

	public void setFlaggedBy(List<String> flaggedBy) {
		this.flaggedBy = flaggedBy;
	}

	//helper for adding reference to users who flag given gift
	public void addFlagger(String username){
		this.flaggedBy.add(username);
	}
	
	//helper for deleting reference to user who doesn't like anymore
	public void deleteFlagger(String username){
		this.flaggedBy.remove(username);
	}

	public Integer getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(Integer flagCount) {
		this.flagCount = flagCount;
	}

	public Long getChainId() {
		return chainId;
	}

	public void setChainId(Long chainId) {
		this.chainId = chainId;
	}

	/*
	@Override
	public int hashCode() {
		return Objects.hashCode(picture, text, text);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Gift) {
			Gift other = (Gift) obj;
				
			// Google Guava provides great utilities for equals too!
			return Objects.equal(picture, other.picture)
					&& Objects.equal(title, other.title)
					&& Objects.equal(text, other.text);
		} else {
			return false;
		}
	}
	*/
	
	
	@Override
	public String toString() {
		return "Gift [id=" + id + ", picture=" + picture + ", title=" + title
				+ ", text=" + text + ", username=" + username
				+ ", creationDate=" + creationDate + ", likeCount=" + likeCount
				+ ", flagCount=" + flagCount + "]";
	}
	
}

