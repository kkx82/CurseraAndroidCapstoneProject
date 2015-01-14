package com.appspot.potlachkk.mobile.updater;

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


import com.appspot.potlachkk.mobile.PotlachApplication;
import com.appspot.potlachkk.mobile.events.ChainListLoad;
import com.appspot.potlachkk.mobile.events.ChainLoad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class UpdateAlarmReceiver extends BroadcastReceiver {
	
	public static final int REQUEST_CODE = 12345;
	public static final String ACTION = "potlach.update.alarm";

	// Triggered by the Alarm periodically 
	@Override
	public void onReceive(Context context, Intent intent) {
		    
		String eventType = intent.getStringExtra("event");
		Long extra = intent.getLongExtra("extra", 0);
			
		Log.d("ALARM", eventType);
			  
		if(eventType.equals("CHAIN_LIST")){
			PotlachApplication.getBus().post(new  ChainListLoad());
		}
		if(eventType.equals("CHAIN")){
			if(extra!=0){
				PotlachApplication.getBus().post(new  ChainLoad(extra));
			}
		}
	}
}

