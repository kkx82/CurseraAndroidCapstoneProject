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


import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appspot.potlachkk.mobile.events.TopGiversLoad;
import com.appspot.potlachkk.mobile.events.TopGiversLoaded;
import com.appspot.potlachkk.mobile.models.User;
import com.squareup.otto.Subscribe;

// Top Giver activity

public class TopGiversActivity extends BaseActivity {
	
	// private final static String LOG_TAG = ChainListActivity.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_top_givers_list);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		PotlachApplication.getBus().post(new TopGiversLoad());
	}

	@Subscribe
	public void onTopGiversLoaded(TopGiversLoaded event){
		populateListView(event.getUserList());
	}
	
	public void populateListView(List<User> userList){
		
		// We use simple list of string
		String [] users = new String[userList.size()];
		int index = 0;
		for (User u : userList) {
			users[index] = u.toTopGivers();
			index++;
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_row, users);
		ListView listView = (ListView) findViewById(R.id.top_givers_list);
		listView.setAdapter(adapter);	
	}
	
}
