package com.appspot.potlachkk.mobile.events;

import android.net.Uri;

import com.appspot.potlachkk.mobile.models.Gift;

public class GiftCreate {
	private Gift gift;
	private Uri photo;
	private String mimeType;
	
	public Gift getGift() {
		return gift;
	}

	public void setGift(Gift gift) {
		this.gift = gift;
	}
	
	public Uri getPhoto() {
		return photo;
	}

	public void setPhoto(Uri photo) {
		this.photo = photo;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public GiftCreate(Gift gift, Uri photo, String mimeType) {
		super();
		this.gift = gift;
		this.photo = photo;
		this.mimeType = mimeType;
	}

	public GiftCreate(Gift gift, Uri photo) {
		super();
		this.gift = gift;
		this.photo = photo;
	}

	public GiftCreate(Gift gift) {
		super();
		this.gift = gift;
	}
	

}
