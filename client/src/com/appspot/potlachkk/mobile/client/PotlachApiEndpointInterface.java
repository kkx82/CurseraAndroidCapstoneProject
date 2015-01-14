package com.appspot.potlachkk.mobile.client;

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



import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import com.appspot.potlachkk.mobile.models.Chain;
import com.appspot.potlachkk.mobile.models.Gift;
import com.appspot.potlachkk.mobile.models.LoginStatus;
import com.appspot.potlachkk.mobile.models.User;

// Retrofit Interface definition
public interface PotlachApiEndpointInterface {
	
	//CHAINS
	public static final String CHAIN_SVC_PATH = "/chain";
	public static final String CHAIN_SVC_PATH_CHAIN = "/chain/{id}";
	
	// get list of chains - returns a list of chains 
	// (number and chain order is defined on server)
	@GET(CHAIN_SVC_PATH)
	public void getChainList(Callback<List<Chain>> callback);
	
	// returns a chain with all its gifts
	@GET(CHAIN_SVC_PATH_CHAIN)
	public void getChain(@Path("id") Long id, Callback<Chain> callback);
			
	//login
	public static final String LOGIN_PATH = "/login";
	public static final String LOGOUT_PATH = "/logout";
	public static final String PASSWORD_PARAMETER = "password";
	public static final String USERNAME_PARAMETER = "username";
		
	@FormUrlEncoded
	@POST(LOGIN_PATH)
	public void login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String pass, Callback<LoginStatus> callback);
	
	@GET(LOGOUT_PATH)
	public void logout(Callback<LoginStatus> callback);
	
	
	//gift
	public static final String GIFT_SVC_PATH_LIKE = "/gift/{id}/like";
	public static final String GIFT_SVC_PATH_FLAG = "/gift/{id}/flag";
	public static final String GIFT_SVC_PATH = "/gift";
	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/find";
	public static final String TITLE_PARAMETER = "title";

	// do always like - the server will decide if it is like on unlike
	@GET(GIFT_SVC_PATH_LIKE)
	public void like(@Path("id") Long id, Callback<Void> callback);
	
	@GET(GIFT_SVC_PATH_FLAG)
	public void flag(@Path("id") Long id, Callback<Void> callback);
	
	@POST(GIFT_SVC_PATH)
	public void addGift(@Body Gift g, Callback<Long> callback);
	
	// search for gifts by title
	@GET(GIFT_TITLE_SEARCH_PATH)
	public void findByTitle(@Query(TITLE_PARAMETER) String title, Callback<List<Gift>> callback);

	
	//image
	public static final String IMG_SVC_PATH = "/image";
	
	@Multipart
	@POST(IMG_SVC_PATH)
	public void addImage(@Part("imageData") TypedFile imageFile, Callback<Long> callback);
	
	
	//users
	public static final String USER_SVC_PATH_TOPGIVER = "/user/topgiver";

	@GET(USER_SVC_PATH_TOPGIVER)
	public void getTopGivers(Callback<List<User>> callback);
}



