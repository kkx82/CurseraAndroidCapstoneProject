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
import android.widget.ListView;

import com.appspot.potlachkk.mobile.events.ChainLoad;
import com.appspot.potlachkk.mobile.events.ChainLoaded;
import com.appspot.potlachkk.mobile.events.ErrorBadOrMissingCredentials;
import com.appspot.potlachkk.mobile.events.GiftFlag;
import com.appspot.potlachkk.mobile.events.GiftLike;
import com.appspot.potlachkk.mobile.events.LoginOKResponse;
import com.appspot.potlachkk.mobile.models.Chain;
import com.appspot.potlachkk.mobile.models.Gift;
import com.appspot.potlachkk.mobile.ui.GiftAdapter;
import com.appspot.potlachkk.mobile.updater.UpdateAlarmReceiver;
import com.squareup.otto.Subscribe;

// Shows a Chain which is basically a gift list,
// Some of its functionality is inherited into SearchableActivity,
// since it also shows s list of gifts
public class ChainActivity extends BaseActivity{

	// private final static String LOG_TAG = ChainActivity.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	private GiftAdapter adapter;
	private Long currentChainId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_gift_list);

		// If we came from ChainList activity we need a gift list
		// of given chain
		Intent i = getIntent();
		currentChainId = i.getLongExtra("chainId", 0);
		
		// Create the adapter to convert the array to views
		// The adapter will be populated when data arrives
		ArrayList<Gift> list = new ArrayList<Gift>();
		adapter = new GiftAdapter(this, list, getCurrentUser(), getShowFlagged());
		
		ListView listView = (ListView) findViewById(R.id.gift_list);
		listView.setAdapter(adapter);
		
		// request first GET before schedule 
		PotlachApplication.getBus().post(new ChainLoad(currentChainId));
	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		// Set up UpdateAlarmReceiver for this activity
		rr = new UpdateAlarmReceiver();
		
		IntentFilter intentFilter = new IntentFilter(UpdateAlarmReceiver.ACTION);
		getApplicationContext().registerReceiver(rr, intentFilter);
		
		// register receiver to the bus, so it can send events
		PotlachApplication.getBus().register(rr);
		
		// schedule alarm for this activity
		scheduleAlarm("CHAIN", currentChainId);

	}
	
	public void onPause() {
		super.onPause();
		// we don't need the alarm if the activity is in background
		cancelAlarm();
		getApplicationContext().unregisterReceiver(rr);
		PotlachApplication.getBus().unregister(rr);
		rr=null;
	}
	
	// Options specific for current activity
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chain_menu, menu);
		return true;
	}
	
	// In this activity we need immediate effect of switching
	// "show flagged" option
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.option_show_flagged:
				adapter.setShowFlag(getShowFlagged());
				adapter.notifyDataSetChanged();
				return true;
			
			case R.id.chain_menu_add:
				Intent intent = new Intent(this, CreateGiftActivity.class);
				intent.putExtra("caller", ChainActivity.class.getSimpleName());
				intent.putExtra("chainId", currentChainId);
				startActivity(intent);
				return true;
			
			default:
				return false;
		}
	}
	
	@Subscribe
	public void onChainLoaded(ChainLoaded event){
		Chain chain = event.getChain();
		populateGiftList(chain.getGifts());
	}
	
	private void populateGiftList(List<Gift> giftList) {	
		// When data from the service arrive populate the adapter
		// and notify observers
		adapter.getData().clear();
		adapter.getData().addAll(giftList);
		adapter.notifyDataSetChanged();	
	}

	// Listeners
	public void likeGiftAction(View view){
		Long id = (Long)view.getTag();
		if(id!=null){		
			PotlachApplication.getBus().post(new GiftLike(id));
		}
	}
	
	public void  flagGiftAction(View view){
		Long id = (Long)view.getTag();
		if(id!=null){		
			PotlachApplication.getBus().post(new GiftFlag(id));
		}
		adapter.notifyDataSetChanged();	
	}
		
	// In case we somehow lost session there will be attempt to log in
	// made in background, this tells what to do after
	@Subscribe
	public void onLoginResponse(LoginOKResponse event){
		PotlachApplication.getBus().post(new ChainLoad(currentChainId));
	}

	@Subscribe
	public void onErrorBadCredentials(ErrorBadOrMissingCredentials event) {
		toast(getString(R.string.bad_credentials));
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("caller", ChainActivity.class.getSimpleName());
		startActivity(intent);
	}
}
