package com.appspot.potlachkk.controller;

import java.util.Collection;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appspot.potlachkk.client.ChainSvcApi;
import com.appspot.potlachkk.client.GiftSvcApi;
import com.appspot.potlachkk.model.Chain;
import com.appspot.potlachkk.model.Gift;
import com.appspot.potlachkk.repository.ChainRepository;
import com.appspot.potlachkk.repository.GiftRepository;

@Controller
public class ChainSvc implements ChainSvcApi {

	private static final Logger log = Logger.getLogger(ChainSvc.class.getName());
	
	@Autowired
	private ChainRepository chains;
	
	// main service - returns 5 recentrly updated chains with max.
	// 4 gifts loaded
	
	@Override
	@RequestMapping(value=ChainSvcApi.CHAIN_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Chain> getChainList() {
		return chains.findAllByUpdateDate(true, 5, 4);
	}

	// get chain by given id and load all its gifts 
	// (number of gifts can be configured)
	
	@Override
	@RequestMapping(value=ChainSvcApi.CHAIN_SVC_PATH_CHAIN, method=RequestMethod.GET)
	public @ResponseBody Chain getChain(@PathVariable("id") Long id) {
		
		Chain ch = chains.findByIdAndLoadGifts(id, 0);
		
		if(ch==null){
			throw new ResourceNotFoundException();
		}
				
		return ch; 
	}

}
