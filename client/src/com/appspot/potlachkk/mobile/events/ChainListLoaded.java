package com.appspot.potlachkk.mobile.events;

import java.util.List;

import com.appspot.potlachkk.mobile.models.Chain;

public class ChainListLoaded {

	private List<Chain> chainList;

	public List<Chain> getChainList() {
		return chainList;
	}

	public void setChainList(List<Chain> chainList) {
		this.chainList = chainList;
	}

	public ChainListLoaded(List<Chain> chainList) {
		super();
		this.chainList = chainList;
	}
	
}
