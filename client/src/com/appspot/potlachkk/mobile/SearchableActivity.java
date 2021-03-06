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

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.appspot.potlachkk.mobile.events.Search;
import com.appspot.potlachkk.mobile.events.SearchResultsLoaded;
import com.appspot.potlachkk.mobile.models.Gift;
import com.appspot.potlachkk.mobile.ui.GiftAdapter;
import com.squareup.otto.Subscribe;


// Integrated in the Android Search Interface
// uses some functions from ChainActivity
public class SearchableActivity extends ChainActivity {

	private GiftAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_gift_list);
	    
		ArrayList<Gift> list = new ArrayList<Gift>();
		adapter = new GiftAdapter(this, list, getCurrentUser(), getShowFlagged());
		
		ListView listView = (ListView) findViewById(R.id.gift_list);
		listView.setAdapter(adapter);

	    handleIntent(getIntent());
	}
	
	
	@Override
    protected void onNewIntent(Intent intent) {       
        handleIntent(intent);
    }

	private void handleIntent(Intent intent) {
	
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        	Log.d("SEARCHING FOR", query);
	        	
	        	PotlachApplication.getBus().post(new Search(query));
	        }
	}
	
	@Subscribe
	public void onSearchResultsLoaded(SearchResultsLoaded event){
		populateGiftList(event.getGifts());
	}
	
	private void populateGiftList(List<Gift> giftList) {	
		adapter.getData().clear();
		adapter.getData().addAll(giftList);
		adapter.notifyDataSetChanged();	
	}
	   
}
