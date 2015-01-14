package com.appspot.potlachkk.mobile.events;

import com.appspot.potlachkk.mobile.models.Chain;

public class ChainLoaded {
	private Chain chain;

	public Chain getChain() {
		return chain;
	}

	public void setChain(Chain chain) {
		this.chain = chain;
	}

	public ChainLoaded(Chain chain) {
		super();
		this.chain = chain;
	}
}
