package com.appspot.potlachkk.mobile.events;

import java.util.List;

import com.appspot.potlachkk.mobile.models.Gift;

public class SearchResultsLoaded {
	private List<Gift> gifts;

	public List<Gift> getGifts() {
		return gifts;
	}

	public void setGifts(List<Gift> gifts) {
		this.gifts = gifts;
	}

	public SearchResultsLoaded(List<Gift> gifts) {
		super();
		this.gifts = gifts;
	}
	
	
}
