package com.appspot.potlachkk.model;


/*
 * Potlach - Coursea POSA Capstone Project
 * Copyright (C) 2014  KK
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

@PersistenceCapable
public class User implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	//default user
	public User(String email, String username, String pass, Date registrationDate) {
		super();
		
		this.email = email;
		this.username = username; 
		this.password = pass;
		
		this.firstName = "FirstName";
		this.lastName = "LastName";
		this.role = "USER";  //default role
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.likeCount = 0;
		this.likedGifts = new ArrayList<Long>();
		this.flaggedGifts = new ArrayList<Long>();
		this.registrationDate = registrationDate;
		this.lastLoginDate = new Date(); //TODO
	}

	@Persistent
    private String email;
	
	@Persistent
    private String firstName;
 
	@Persistent
    private String lastName;
 
	@Persistent
	private String password;
 
	//TODO: create an ENUM with roles, see how to save it in DB
	@Persistent
    private String role;
	
	@PrimaryKey
	@Persistent
	private String username; //compliance with UserDetails
	
	@Persistent
	private boolean accountNonExpired; //compliance with UserDetails
	
	@Persistent
	private boolean accountNonLocked; //compliance with UserDetails
	
	@Persistent
	private boolean credentialsNonExpired; //compliance with UserDetails
	
	@Persistent
	private boolean enabled; //compliance with UserDetails
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Persistent
	private Date registrationDate;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Persistent
	private Date lastLoginDate;
	
	//keeps like count of all gifts created by user
	@Persistent
	private Integer likeCount;
	
	//gifts liked by user
	@Persistent
	private List<Long> likedGifts;

	//gifts flagged by user
	@Persistent
	private List<Long> flaggedGifts;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		
		if(this.role != null && !this.role.isEmpty()){
			String authority = "ROLE_" + this.role;
			authorities.add( new SimpleGrantedAuthority(authority));	
		}
		
		return authorities;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
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


	public void setRole(String role) {
		this.role = role;
	}


	public String getUsername() {
		return username;
	}

	//TODO - make sure it is unique
	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}


	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public List<Long> getLikedGifts() {
		return likedGifts;
	}

	public void setLikedGifts(List<Long> likedGifts) {
		this.likedGifts = likedGifts;
	}

	//helper
	public void addLikedGift(Long id){
		this.likedGifts.add(id);
	}

	public void removeLikedGift(Long id){
		this.likedGifts.remove(id);
	}
	
	public List<Long> getFlaggedGifts() {
		return flaggedGifts;
	}

	public void setFlaggedGifts(List<Long> flaggedGifts) {
		this.flaggedGifts = flaggedGifts;
	}

	//helper
	public void addFlaggedGift(Long id){
		this.flaggedGifts.add(id);
	}

	public void removeFlaggedGift(Long id){
		this.flaggedGifts.remove(id);
	}

	
}
