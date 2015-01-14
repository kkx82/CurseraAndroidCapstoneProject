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


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.appspot.potlachkk.mobile.events.ErrorBadOrMissingCredentials;
import com.appspot.potlachkk.mobile.events.LoginOKResponse;
import com.appspot.potlachkk.mobile.events.LoginRequest;
import com.squareup.otto.Subscribe;

public class LoginActivity extends BaseActivity {

	// private final static String LOG_TAG = LoginActivity.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// hide action bar
		ActionBar actionBar = this.getActionBar();
		actionBar.hide();
	}

	// Listeners
	
	public void login(View view) {
		
		EditText userET = (EditText) findViewById(R.id.username);
		EditText passET = (EditText) findViewById(R.id.password);
		
		Editable userE = userET.getText();
		Editable passE = passET.getText();
		
		String user = String.valueOf(userE.toString());
		String pass = String.valueOf(passE.toString());
		
		//Store login data in SharedPreferences
		Context context = getApplicationContext();
		
		SharedPreferences sharedPref = context.getSharedPreferences(
		        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.preference_file_key_user), user);
		editor.putString(getString(R.string.preference_file_key_password),pass);
		editor.commit();

		PotlachApplication.getBus().post(new LoginRequest(user, pass));
	}

	
	//login ok, we can go along
	@SuppressWarnings("unchecked")
	@Subscribe
	public void onLoginResponse(LoginOKResponse event){
		String caller = getIntent().getStringExtra("caller");
		if(caller!=null){		
			Class<Activity> callerClass;
			try {
				callerClass = (Class<Activity>) Class.forName(caller);
				Intent intent = new Intent(this, callerClass);
				startActivity(intent);
				return;
			} 
			catch (ClassNotFoundException e) {
				Intent intent = new Intent(this, ChainListActivity.class);
				startActivity(intent);
			}
		}
		
		Intent intent = new Intent(this, ChainListActivity.class);
		startActivity(intent);
	}
	
	@Subscribe
	public void onErrorBadCredentials(ErrorBadOrMissingCredentials event) {
		
		toast(getString(R.string.bad_credentials));
		
		//delete credentials since they are not valid
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(
		        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.remove(getString(R.string.preference_file_key_user));
		editor.remove(getString(R.string.preference_file_key_password));
		editor.commit();
	}
	
}
