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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.appspot.potlachkk.mobile.events.LogoutRequest;
import com.appspot.potlachkk.mobile.updater.UpdateAlarmReceiver;

public class BaseActivity extends Activity{

	protected UpdateAlarmReceiver rr;
	
	@Override
	public void onResume() {
		super.onResume();
		PotlachApplication.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		PotlachApplication.getBus().unregister(this);
	}
	
	
	// Create Options Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.config_menu, menu);
		
		 // Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));

		return true;
	}
	
	
	// Set up checkable menus
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    super.onPrepareOptionsMenu(menu);   
	    menu.findItem(R.id.option_show_flagged).setChecked( getShowFlagged());
	    
	    int schedule = getSchedule();
	    if (schedule == 1) menu.findItem(R.id.one_min).setChecked(true);
	    if (schedule == 5) menu.findItem(R.id.five_min).setChecked(true);
	    if (schedule == 60 ) menu.findItem(R.id.sixty_min).setChecked(true);
	    return true;
	}
	
	// Process clicks on Options Menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()) {
		
		case R.id.option_show_flagged:
			
			if (item.isChecked()){
				item.setChecked(false);
				setShowFlagged(false);
				toast(getString(R.string.show_flagged_gift_disabled_text));
			}
            else{
            	item.setChecked(true);
            	setShowFlagged(true);
            	toast(getString(R.string.show_flagged_gift_enabled_text));
            }
			return true;
		
		case R.id.one_min:
			setSchedule(1);
			item.setChecked(true);
			toast(getString(R.string.scheduler_one_message));
			return true;
			
		case R.id.five_min:
			setSchedule(5);
			item.setChecked(true);
			toast(getString(R.string.scheduler_fife_message));
			return true;
		
		case R.id.sixty_min:
			setSchedule(60);
			item.setChecked(true);
			toast(getString(R.string.scheduler_sixty_message));
			return true;
			
		case R.id.option_log_out:
			
			//log out from server
			PotlachApplication.getBus().post(new LogoutRequest());
			
			//delete stored preferences
			forgetUserAndPassword();
			
			toast(getString(R.string.logout));
			
			//open login activity
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			
			return true;
		case R.id.top_givers:
			
			Intent intent_givers = new Intent(this, TopGiversActivity.class);
			startActivity(intent_givers);
			
			return true;
		default:
			return false;
		}
	}
	
	
	// SharedPreferences helpers
	public SharedPreferences getSharedPreferences(){
		Context contex =  getApplicationContext();
		return contex.getSharedPreferences( getString(R.string.preference_file_key), Context.MODE_PRIVATE);
	}
	
	public void setShowFlagged(boolean v){
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean(getString(R.string.preference_file_key_show_flagged),v);
		editor.commit();
	}
	
	public boolean getShowFlagged(){	
		return getSharedPreferences().getBoolean( getString(R.string.preference_file_key_show_flagged), false );
	}

	public String getCurrentUser(){
		String user = getSharedPreferences().getString( getString(R.string.preference_file_key_user), null );
		return user;
	}
	
	public void forgetUserAndPassword(){
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.remove(getString(R.string.preference_file_key_user));
		editor.remove(getString(R.string.preference_file_key_password));
		editor.commit();
	}
	
	public void setSchedule(int x){
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putInt(getString(R.string.preference_key_sheduler),x);
		editor.commit();
		return;
	}
	
	public int getSchedule(){
		return getSharedPreferences().getInt(getString(R.string.preference_key_sheduler),1);
	}

	// Other helpers
	public void toast(String messeage){		
		if (messeage==null)
			messeage = "";
	  	Toast.makeText(getApplicationContext(), messeage ,Toast.LENGTH_SHORT).show();
	}
	
	
	// Scheduler
	public void scheduleAlarm(String event, Long extra) {
		
	    // Construct an intent that will execute the UpdateAlarmReceiver
	    Intent intent = new Intent(UpdateAlarmReceiver.ACTION);
	    
	    intent.putExtra("event", event);
	    intent.putExtra("extra", extra);
	    
	    // Create a PendingIntent to be triggered when the alarm goes off
	    final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0 ,intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    // Set up schedule interval
	    int min = getSchedule();
	    int multiplicator = 60*1000; 
	    int intervalMillis = min*multiplicator; 
	    
	    long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
		    
	    AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis+100, intervalMillis, pIntent);
	  }
	
	public void cancelAlarm() {
		Intent intent = new Intent(getApplicationContext(), UpdateAlarmReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, UpdateAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}
	
	
}
