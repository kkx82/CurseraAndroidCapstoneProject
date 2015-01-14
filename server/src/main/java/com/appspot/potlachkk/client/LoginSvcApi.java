package com.appspot.potlachkk.client;



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

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

import com.appspot.potlachkk.auth.LoginStatusBuilder;

//Retrofit Interface used for testing and compliance 
//these API is for client only - there is no need to implement
//a controller since it is build in into spring-security
public interface LoginSvcApi {

	public static final String LOGIN_PATH = "/login";
	
	public static final String LOGOUT_PATH = "/logout";

	public static final String PASSWORD_PARAMETER = "password";

	public static final String USERNAME_PARAMETER = "username";
	
	@FormUrlEncoded
	@POST(LOGIN_PATH)
	public LoginStatusBuilder.LoginStatus login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String pass);
	
	@GET(LOGOUT_PATH)
	public LoginStatusBuilder.LoginStatus logout();
}

