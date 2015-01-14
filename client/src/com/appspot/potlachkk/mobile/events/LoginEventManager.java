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
import com.appspot.potlachkk.mobile.models.LoginStatus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LoginEventManager {
	
	// private final static String LOG_TAG = LoginEventManager.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	private Bus bus;
	private PotlachApiEndpointInterface potlachService;
 	private ErrorHandler errorHandler;

	public LoginEventManager(Bus bus, PotlachApiEndpointInterface potlachService, ErrorHandler errorHandler) {
		super();
		this.bus = bus;
		this.potlachService = potlachService;
		this.errorHandler = errorHandler;
	}
	
	@Subscribe
	public void onLogoutReques(LogoutRequest event){
		potlachService.logout(new LogoutRequestResponseHandler());
	}
	
	@Subscribe
	public void onLoginRequest(final LoginRequest event){
		String user = event.getUser();
		String pass = event.getPass();
		potlachService.login(user, pass, new LoginRequestResponseHandler());
	}
	
	private class LoginRequestResponseHandler implements Callback<LoginStatus>{

		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}

		public void success(LoginStatus status, Response response) {
			bus.post(new LoginOKResponse());
		}
	}
	
	private class LogoutRequestResponseHandler implements Callback<LoginStatus>{
		public void failure(RetrofitError error) {
			errorHandler.handleFailure(error);
		}
		public void success(LoginStatus status, Response response) {
			bus.post(new LogoutOKResponse(status));
		}
	}
}
