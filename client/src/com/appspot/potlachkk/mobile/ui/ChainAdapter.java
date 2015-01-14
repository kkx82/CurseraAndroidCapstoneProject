package com.appspot.potlachkk.mobile.ui;

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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.potlachkk.mobile.PotlachApplication;
import com.appspot.potlachkk.mobile.R;
import com.appspot.potlachkk.mobile.models.Chain;
import com.appspot.potlachkk.mobile.models.Gift;
import com.squareup.picasso.Picasso;

public class ChainAdapter extends ArrayAdapter<Chain>{
	 
	public ChainAdapter(Context context, ArrayList<Chain> chains) {
		 super(context, 0, chains);
	}
	
	public static class ViewHolder {

		TextView chain_update_date;
		
		ImageView first_gift_image;
		ImageView second_gift_image;
		ImageView third_gift_image;
		ImageView fourth_gift_image;
	}
		
	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Get the data item for this position
		Chain chain = getItem(position);    
       
		// Get four first gifts
		// This could work well in a loop
		int gifts_size = chain.getGifts().size();
		
		Gift g1 = null;
		Gift g2 = null;
		Gift g3 = null;
		Gift g4 = null;
		
		if(gifts_size>0)
			g1 = chain.getGifts().get(0);
		
		if(gifts_size>1)
			g2 = chain.getGifts().get(1);
			
		if(gifts_size>2)
			g3 = chain.getGifts().get(2);
		
		if(gifts_size>3)
			g4 = chain.getGifts().get(3);
		
		ViewHolder viewHolder;
       
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			viewHolder = new ViewHolder();
			
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_chain_list_row, parent, false);
		
			viewHolder.chain_update_date = (TextView) convertView.findViewById(R.id.chain_update_date);
			
			viewHolder.first_gift_image = (ImageView)convertView.findViewById(R.id.first_gift_image);
			viewHolder.second_gift_image = (ImageView)convertView.findViewById(R.id.second_gift_image);
			viewHolder.third_gift_image = (ImageView)convertView.findViewById(R.id.third_gift_image);
			viewHolder.fourth_gift_image = (ImageView)convertView.findViewById(R.id.fourth_gift_image);
			
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Populate the data into the template view using the data object
		SimpleDateFormat  df = new SimpleDateFormat(getContext().getString(R.string.date_format));
		Date updateDate = chain.getUpdateDate();
		
		viewHolder.chain_update_date.setText(df.format(updateDate));

		// Populate images
		// This could be also a loop
		if(g1!=null){
			String url1 = PotlachApplication.createImgUrl(g1.getPicture());
			Picasso.with(getContext()).load(url1).noFade().resize(100, 100).centerCrop().into(viewHolder.first_gift_image); 
		}
		if(g2!=null){
			String url2 = PotlachApplication.createImgUrl(g2.getPicture());
			Picasso.with(getContext()).load(url2).noFade().resize(100, 100).centerCrop().into(viewHolder.second_gift_image);
		}
		if(g3!=null){
			String url3 = PotlachApplication.createImgUrl(g3.getPicture());
			Picasso.with(getContext()).load(url3).noFade().resize(100, 100).centerCrop().into(viewHolder.third_gift_image);
		}
		if(g4!=null){
			String url4 = PotlachApplication.createImgUrl(g4.getPicture());
			Picasso.with(getContext()).load(url4).noFade().resize(100, 100).centerCrop().into(viewHolder.fourth_gift_image);
		}
		
		return convertView;
	}
}
