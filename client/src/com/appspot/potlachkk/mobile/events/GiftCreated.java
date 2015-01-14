package com.appspot.potlachkk.mobile.events;

public class GiftCreated {
	Long chainId;

	public Long getChainId() {
		return chainId;
	}

	public void setChainId(Long chainId) {
		this.chainId = chainId;
	}

	public GiftCreated(Long chainId) {
		super();
		this.chainId = chainId;
	}
	
}
