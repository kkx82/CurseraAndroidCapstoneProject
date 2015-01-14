package com.appspot.potlachkk.mobile;

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


import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.appspot.potlachkk.mobile.client.ErrorHandler;
import com.appspot.potlachkk.mobile.client.PotlachApiEndpointInterface;
import com.appspot.potlachkk.mobile.client.RetrofitClient;
import com.appspot.potlachkk.mobile.events.ChainEventManager;
import com.appspot.potlachkk.mobile.events.ChainListEventManager;
import com.appspot.potlachkk.mobile.events.ErrorBadOrMissingCredentials;
import com.appspot.potlachkk.mobile.events.ErrorNotAuthorized;
import com.appspot.potlachkk.mobile.events.GiftCreated;
import com.appspot.potlachkk.mobile.events.GiftEventManager;
import com.appspot.potlachkk.mobile.events.LoginEventManager;
import com.appspot.potlachkk.mobile.events.LoginRequest;
import com.appspot.potlachkk.mobile.events.NotFoundError;
import com.appspot.potlachkk.mobile.events.OtherServiceError;
import com.appspot.potlachkk.mobile.events.ServiceError;
import com.appspot.potlachkk.mobile.events.TopGiversEventManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class PotlachApplication extends Application {
	
	// TODO use common string values for error messages
	
	private final static String OTTO_TAG = "otto";
	private final static String IMAGE_PATH_PART = "http://192.168.1.66:8181/image/";
	
	private static Bus bus;
	public static Bus getBus(){ return bus; }
	
	RestAdapter restAdapter;
	PotlachApiEndpointInterface potlachService;
	
	public void setup(){
	
		OkHttpClient client = new OkHttpClient();
		
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		client.setCookieHandler(cookieManager);
		
		restAdapter = new RestAdapter.Builder()
			.setClient(new OkClient(client))
			.setEndpoint(RetrofitClient.BASE_URL)
			.setConverter(new GsonConverter(RetrofitClient.gson))
			.build();
		
		potlachService = restAdapter.create(PotlachApiEndpointInterface.class);
	}
	
	// all event manages / dispatchers
	private ChainListEventManager chainListEventManager;
	private LoginEventManager loginEventManagent;
	private ChainEventManager chainEventManager;
	private GiftEventManager giftEventManager;
	private TopGiversEventManager topGiversEventManager;
	
	// global error handler
	private ErrorHandler errorHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setup();
		
		bus = new Bus(ThreadEnforcer.ANY);
		
		errorHandler = new ErrorHandler(bus);
		
	    chainListEventManager = new ChainListEventManager(bus, potlachService, errorHandler );
	    loginEventManagent = new LoginEventManager(bus, potlachService, errorHandler );
	    chainEventManager = new ChainEventManager(bus,potlachService, errorHandler);
	    giftEventManager = new GiftEventManager(bus, potlachService, errorHandler);
	    topGiversEventManager = new TopGiversEventManager(bus, potlachService, errorHandler);
	    
	    bus.register(chainListEventManager);
	    bus.register(loginEventManagent);
	    bus.register(chainEventManager);
	    bus.register(giftEventManager);
	    bus.register(topGiversEventManager);
	    bus.register(errorHandler);
	    
	    bus.register(this); //listen for "global" events
	}
	
	
	// Helper methods that we need not only in activities
	
	// Create URL for given imageID
	public static String createImgUrl(String imageId){
		return IMAGE_PATH_PART + imageId;
	}
	
	
	// Global events
	// All global events must be subscribed here in Application class since Otto
	// don't get to overridden classes like BaseActivity
	@Subscribe
	public void onGiftCreated(GiftCreated event){
		//TODO
		Toast.makeText(this, "Gift Created", Toast.LENGTH_SHORT).show();
	}

	// If session is brokent (401) try to resolve it without bothering
	// the user using stored credentials
	@Subscribe
	public void onErrorNotAuthorized(ErrorNotAuthorized event) {
		
		Log.d(OTTO_TAG, "401");
		
		//try to read user and pass from Shared Preferences
		Context context = getApplicationContext();
		
		SharedPreferences sharedPref = context.getSharedPreferences( getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		String user = sharedPref.getString( getString(R.string.preference_file_key_user), null );
		String pass = sharedPref.getString( getString(R.string.preference_file_key_password), null );
		
		// if we get any user and pass try to log in
		// otherwise show LoginActivity
		if (user!=null || pass!=null){
			Log.d(OTTO_TAG, "User and pass found in SP: " + user + ":" + pass);
			bus.post(new LoginRequest(user, pass));
		}
		else{
			// each important activity is listening for this event and
			// cope with it in its own way e.g. redirect to Login
			bus.post(new ErrorBadOrMissingCredentials());
		}
	}
	
	// Other global errors
	@Subscribe //404
	public void onNotFound(NotFoundError event){
		Toast.makeText(this, "Resource not found on server", Toast.LENGTH_SHORT).show();
	}

	@Subscribe //500
	public void onServerError(ServiceError event){
		Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe //other
	public void onOtherServerError(OtherServiceError event){
		Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
	}
	
	// Other helpers
	public void toast(String messeage){		
		if (messeage==null)
			messeage = "";
	  	Toast.makeText(getApplicationContext(), messeage ,Toast.LENGTH_SHORT).show();
	}
}
