package com.appspot.potlachkk.mobile.events;

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


import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.appspot.potlachkk.mobile.client.ErrorHandler;
import com.appspot.potlachkk.mobile.client.PotlachApiEndpointInterface;
import com.appspot.potlachkk.mobile.models.Gift;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class GiftEventManager {

	// private final static String LOG_TAG = ChainListEventManager.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	private Bus bus;
	private PotlachApiEndpointInterface potlachService;
	private ErrorHandler errorHandler;
	
	public GiftEventManager(Bus bus, PotlachApiEndpointInterface potlachService, ErrorHandler errorHandler) {
		super();
		this.bus = bus;
		this.potlachService = potlachService;
		this.errorHandler = errorHandler;
	}

	@Subscribe
	public void onGiftLike(GiftLike event){
		potlachService.like(event.getId(), new GiftLikeHandler());
	}

	@Subscribe
	public void onGiftFlag(GiftFlag event){
		potlachService.flag(event.getId(), new GiftFlagHandler());
	}
	
	// Two steps:
	// 1. upload picture and get its ID
	// 2. create and post gift with picture ID
	
	@Subscribe
	public void onGiftCreate(GiftCreate event){

		final Gift gift = event.getGift();
		
		// if we have photo post it at first and wait for its id
		// otherwise head directly to upload gift data
		if(event.getPhoto()!=null){
			TypedFile testFile = new TypedFile(event.getMimeType(), new File(event.getPhoto().getPath()));
			potlachService.addImage(testFile, new Callback<Long>(){
				public void failure(RetrofitError error) {
					errorHandler.handleFailure(error);
				}
				public void success(Long pictureId, Response response) {
					gift.setPicture(String.valueOf(pictureId));
					potlachService.addGift(gift, new GiftPostHandler());
				}
			});
		}
		else{
			potlachService.addGift(gift, new GiftPostHandler());
		}
	}
	
	
	private class GiftPostHandler implements Callback<Long>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}

		public void success(Long chainId, Response response) {
			bus.post(new GiftCreated(chainId));
		}
	}
	
	private class GiftLikeHandler implements Callback<Void>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}

		public void success(Void arg0, Response arg1) {
			// do nothing
		}		
	}

	private class GiftFlagHandler implements Callback<Void>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}

		public void success(Void arg0, Response arg1) {
			// do nothing
		}		
	}
	
	// Search
	@Subscribe
	public void onSearch(Search event){
		potlachService.findByTitle(event.getQuery(), new SearchHandler());
	}
	
	private class SearchHandler implements Callback<List<Gift>>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}	
		public void success(List<Gift> gifts, Response response) {
			bus.post(new SearchResultsLoaded(gifts));
		}
	}
}

