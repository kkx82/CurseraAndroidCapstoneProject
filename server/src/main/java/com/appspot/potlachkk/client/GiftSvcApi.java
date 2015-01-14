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


import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.appspot.potlachkk.model.Gift;

//Retrofit Interface used for testing and compliance 
public interface GiftSvcApi {
	
	public static final String TITLE_PARAMETER = "title";
	
	public static final String GIFT_SVC_PATH = "/gift";

	public static final String GIFT_SVC_PATH_GIFT = "/gift/{id}";
	
	public static final String GIFT_SVC_PATH_LIKE = "/gift/{id}/like";
	
	public static final String GIFT_SVC_PATH_FLAG = "/gift/{id}/flag";
	
	// The path to search gifts by title
	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/find";

	@POST(GIFT_SVC_PATH)
	public Long addGift(@Body Gift g);

	@GET(GIFT_SVC_PATH_GIFT)
	public Gift getGiftById(@Path("id") Long id);

	@DELETE(GIFT_SVC_PATH_GIFT)
	public Void deleteGiftById(@Path("id") Long id);
	
	@GET(GIFT_TITLE_SEARCH_PATH)
	public Collection<Gift> findByTitle(@Query(TITLE_PARAMETER) String title);
	
	//POST with empty body doesn't work well on GAE
	@GET(GIFT_SVC_PATH_LIKE)
	public Void like(@Path("id") Long id);
	
	//POST with empty body doesn't work well on GAE
	@GET(GIFT_SVC_PATH_FLAG)
	public Void flag(@Path("id") Long id);
		
	
}
