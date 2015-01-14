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
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.appspot.potlachkk.mobile.PotlachApplication;
import com.appspot.potlachkk.mobile.R;
import com.appspot.potlachkk.mobile.models.Gift;
import com.squareup.picasso.Picasso;

public class GiftAdapter extends ArrayAdapter<Gift> {
	
	String currentUser;
	boolean showFlagged;
	private List<Gift> gifts;
	
	public GiftAdapter(Context context, ArrayList<Gift> gifts, String user, boolean showFlagged) {
		 super(context, 0, gifts);
		 this.currentUser = user;
		 this.showFlagged = showFlagged;
		 this.gifts = gifts;
	}
	
	public List<Gift> getData(){
		return this.gifts;
	}
	
	public void setShowFlag(boolean v){
		this.showFlagged = v;
	}
	
	private static class ViewHolder {

		ImageView gift_image;
		
		TextView gift_title;
		TextView gift_created_by_text;
		TextView gift_created_on_text;
		TextView gift_like_count_text;
		TextView gift_text_text;
		
		ToggleButton likeButton;
		ToggleButton flagButton;
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Get the data item for this position
		Gift gift = getItem(position);
		
		// If we don't show flagged gifts we can return empty view at this point
		if(gift.getFlaggedBy().contains(currentUser)){	
			if(!showFlagged){
				return new View(getContext());
			}
		}
		
		ViewHolder viewHolder;
		
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			viewHolder = new ViewHolder();
					
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_gift_list_row, parent, false);
			
			viewHolder.gift_title =  (TextView)convertView.findViewById(R.id.gift_title_text);
			viewHolder.gift_created_by_text = (TextView)convertView.findViewById(R.id.gift_created_by_text);
			viewHolder.gift_created_on_text = (TextView)convertView.findViewById(R.id.gift_created_on_text);
			viewHolder.gift_like_count_text = (TextView)convertView.findViewById(R.id.gift_like_count_text);
			viewHolder.gift_text_text = (TextView)convertView.findViewById(R.id.gift_text_text);
						
			viewHolder.gift_image = (ImageView)convertView.findViewById(R.id.gift_image);
			
			viewHolder.likeButton = (ToggleButton)convertView.findViewById(R.id.gift_like_button);
			viewHolder.likeButton.setTag(gift.getId());
			
			viewHolder.flagButton = (ToggleButton)convertView.findViewById(R.id.gift_flag_button);
			viewHolder.flagButton.setTag(gift.getId());
			
			convertView.setTag(viewHolder);
		}
		
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		// Populate the data into the template view using the data object	
		viewHolder.gift_title.setText(gift.getTitle());
		viewHolder.gift_created_by_text.setText(gift.getUsername());
		viewHolder.gift_text_text.setText(gift.getText());
		viewHolder.gift_like_count_text.setText(Integer.toString(gift.getLikeCount()));
		
		SimpleDateFormat  df = new SimpleDateFormat(getContext().getString(R.string.date_format));
		Date createDate = gift.getCreationDate();
		viewHolder.gift_created_on_text.setText(df.format(createDate));
		
		// Load image
		String url = PotlachApplication.createImgUrl(gift.getPicture());
		Picasso.with(getContext()).load(url).noFade().resize(400, 0).into(viewHolder.gift_image); 
		
		//label buttons
		if(gift.getLikedBy().contains(currentUser)){
			viewHolder.likeButton.setChecked(true);
		}
		
		if(gift.getFlaggedBy().contains(currentUser)){
			viewHolder.flagButton.setChecked(true);
		}
		
		return convertView;
	}
	
}
