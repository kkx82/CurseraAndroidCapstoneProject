package com.appspot.potlachkk.mobile.events;

import com.appspot.potlachkk.mobile.models.Gift;

public class GiftPost {
	private Gift gift;

	public Gift getGift() {
		return gift;
	}

	public void setGift(Gift gift) {
		this.gift = gift;
	}

	public GiftPost(Gift gift) {
		super();
		this.gift = gift;
	}
	
	
}
