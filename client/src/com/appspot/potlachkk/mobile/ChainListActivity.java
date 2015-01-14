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


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.appspot.potlachkk.mobile.events.ChainListLoad;
import com.appspot.potlachkk.mobile.events.ChainListLoaded;
import com.appspot.potlachkk.mobile.events.ErrorBadOrMissingCredentials;
import com.appspot.potlachkk.mobile.events.LoginOKResponse;
import com.appspot.potlachkk.mobile.models.Chain;
import com.appspot.potlachkk.mobile.ui.ChainAdapter;
import com.appspot.potlachkk.mobile.updater.UpdateAlarmReceiver;
import com.squareup.otto.Subscribe;

public class ChainListActivity extends BaseActivity{

	// private final static String LOG_TAG = ChainListActivity.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_chain_list);
		
		// first GET before alarm scheduler start - is is sometimes delayed
		PotlachApplication.getBus().post(new ChainListLoad());
	}

	// Menu specific for Chain List
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chain_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.chain_list_menu_add:
				Intent intent = new Intent(this, CreateGiftActivity.class);
				intent.putExtra("caller", ChainListActivity.class.getSimpleName());
				startActivity(intent);
				return true;
			
			default:
				return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// Create new receiver for alarm
		// we don't need do get any notification from 
		// alarm if the activity is in background
		rr = new UpdateAlarmReceiver();
		
		IntentFilter intentFilter = new IntentFilter(UpdateAlarmReceiver.ACTION);
		getApplicationContext().registerReceiver(rr, intentFilter);
		
		// register receiver to the bus, so it can send events
		PotlachApplication.getBus().register(rr);
		
		// schedule alarm for this activity
		scheduleAlarm("CHAIN_LIST", null);
	}

	
	@Override
	public void onPause() {
		super.onPause();

		// we don't need the alarm if the activity is in background
		cancelAlarm();
		getApplicationContext().unregisterReceiver(rr);
		PotlachApplication.getBus().unregister(rr);
		rr=null;
	}

	
	// if ChainListLoaded event occurs we have new data to load it
	// in the list view
	@Subscribe
	public void onChainListLoaded(ChainListLoaded event){
		//Log.d(OTTO_TAG, LOG_TAG + ": list of chains arrived");
		List<Chain> chainList = event.getChainList();
		populateChainList(chainList);
	}
	
	
	// Fill in the list view
	private void populateChainList(List<Chain> chainList) {
		
		// Construct the data source
		ArrayList<Chain> list = (ArrayList<Chain>) chainList;
		
		// Create the adapter to convert the array to views
		ChainAdapter adapter = new ChainAdapter(this, list);
		
		// Attach the adapter to a ListView
		ListView listView = (ListView) findViewById(R.id.chain_list);
		listView.setAdapter(adapter);
		
		// Create a listener for every row
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> adapter, View v, int position,long arg3){
				Chain selectedChain = (Chain)adapter.getItemAtPosition(position); 
				showGiftListActivity(selectedChain.getId());
			}
		});
		
	}
	
	// If row clicked start new activity showing choosen chain
	public void showGiftListActivity(Long id){
		Intent intent = new Intent(this, ChainActivity.class);	
		intent.putExtra("chainId", id);
		startActivity(intent);
	}
	
	// In case we somehow lost session there will be attempt to log in
	// made in background; This event and reload the chain list
	@Subscribe
	public void onLoginResponse(LoginOKResponse event){
		PotlachApplication.getBus().post(new ChainListLoad());
	}
	
	// In case the session is gone, and we don't have correct credentials
	// in SP, go to LoginActivity
	// This can not be in BaseActivity since Otto will not reach it
	@Subscribe
	public void onErrorBadCredentials(ErrorBadOrMissingCredentials event) {
		toast(getString(R.string.bad_credentials));
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("caller", ChainListActivity.class.getSimpleName());
		startActivity(intent);
	}	
}
	

