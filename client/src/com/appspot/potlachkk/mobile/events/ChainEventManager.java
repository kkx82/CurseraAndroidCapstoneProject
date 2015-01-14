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


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.appspot.potlachkk.mobile.client.ErrorHandler;
import com.appspot.potlachkk.mobile.client.PotlachApiEndpointInterface;
import com.appspot.potlachkk.mobile.models.Chain;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ChainEventManager {
	// private final static String LOG_TAG = ChainEventManager.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	private Bus bus;
	private PotlachApiEndpointInterface potlachService;
	private ErrorHandler errorHandler;
	
	public ChainEventManager(Bus bus, PotlachApiEndpointInterface potlachService, ErrorHandler errorHandler) {
		super();
		this.bus = bus;
		this.potlachService = potlachService;
		this.errorHandler = errorHandler;
	}
	
	// Request to GET a chain coming from somewhere
	@Subscribe
	public void onChainLoad(ChainLoad event){
		potlachService.getChain(event.getChainId(), new ChainResponseHandler());
	}
	
	private class ChainResponseHandler implements Callback<Chain>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}

		public void success(Chain chain, Response response) {
			bus.post(new ChainLoaded(chain));
		}
	}
}
