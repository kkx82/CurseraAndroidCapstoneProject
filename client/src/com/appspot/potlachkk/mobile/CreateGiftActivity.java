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


import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.appspot.potlachkk.mobile.events.GiftCreate;
import com.appspot.potlachkk.mobile.models.Gift;
import com.squareup.picasso.Picasso;

public class CreateGiftActivity extends BaseActivity{
	
	private final static String LOG_TAG = CreateGiftActivity.class.getCanonicalName();
	// private final static String OTTO_TAG = "otto";
	
	public final static String APP_TAG = "PotlachMobileClient";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 7;
	public final static int GALLERY_ACTIVITY_REQUEST_CODE = 8;
	
	//TODO may it block?
	public String photoFileName = "pphoto.jpg";
	
	private Long currentChainId = null;
	private Class<Activity> callerClass = null;
	private Uri takenPhotoUri = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_gift_create);
		
		Intent i = getIntent();
		currentChainId = i.getLongExtra("chainId", 0);
		
		// get caller class if any
		String caller = getIntent().getStringExtra("caller");
		
		if(caller!=null){
			try {
				callerClass = (Class<Activity>) Class.forName(caller);
			}
			catch(Exception e){} // we don't care, we can cope without caller class
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				
				takenPhotoUri = getPhotoFileUri(photoFileName);
				ImageView preview = (ImageView) findViewById(R.id.gift_create_image);
				Picasso.with(getApplicationContext()).load(takenPhotoUri).resize(500, 0).into(preview); 
			
			} 
			else if (resultCode == RESULT_CANCELED) {
	            toast(getString(R.string.camera_cancelled));
			} 
			else {
				toast(getString(R.string.camera_error));
	       }
	    }
	}
	

	
	// Returns the Uri for a photo stored on disk given the fileName
	public Uri getPhotoFileUri(String fileName) {
	 
		// Get safe storage directory for photos
	    File mediaStorageDir = new File(
	        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
	        Log.d(LOG_TAG, "failed to create directory");
	    }

	    // Return the file target for the photo based on filename
	    return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
	}
	
	
	
	// Listeners
	public void onLaunchCamera(View view) {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	public void createCancel(View view){
		goBack();
	}
	
	public void createPostGift(View view){
		
		// We may now validate these field using TextUtils 
		// and android.util.Patterns
		// In this case we are free to put any kind of data
		// or leave it blanc
		
		EditText titleET = (EditText) findViewById(R.id.gift_create_form_title);
		EditText textET = (EditText) findViewById(R.id.gift_create_form_text);
		
		Editable titleE = titleET.getText();
		Editable textE = textET.getText();
		
		// We left picture field blanc - it will be filled in after the picture is
		// uploaded to the server
		Gift g = new Gift(null, String.valueOf(titleE.toString()), String.valueOf(textE.toString()));
		
		// we have chain id - add gift to this chain
		if(currentChainId!=0){
			g.setChainId(currentChainId);
		}
		
		// if we have image get its content type
		
		String contentType = null;
		if(takenPhotoUri!=null){
			ContentResolver contentResolver = getApplicationContext().getContentResolver();
			contentType = contentResolver.getType(takenPhotoUri);
			
			// in case of using wired camera program it may return null
			// set it manually to "image/jpg"
			if (contentType==null){
				contentType = "image/jpg";
			}
		}
		
		PotlachApplication.getBus().post(new GiftCreate(g, takenPhotoUri, contentType ));
		goBack();
	}
	
	// Helpers
	// We don't want to count on "back button"
	private void goBack(){
		if(callerClass!=null){
			Intent intent = new Intent(this, callerClass);
			startActivity(intent);
			return;
		}
		else {
			Intent intent = new Intent(this, ChainListActivity.class);
			startActivity(intent);
		}
	}
}
