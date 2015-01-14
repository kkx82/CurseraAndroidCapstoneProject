package com.appspot.potlachkk.controller;

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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appspot.potlachkk.client.GiftSvcApi;
import com.appspot.potlachkk.model.Chain;
import com.appspot.potlachkk.model.Gift;
import com.appspot.potlachkk.model.User;
import com.appspot.potlachkk.repository.ChainRepository;
import com.appspot.potlachkk.repository.GiftRepository;
import com.appspot.potlachkk.repository.UserRepository;

/**
 * Some issues with this:
 * it would be nice to have method level security and it was my intention to use it
 * something like @PreAuthorize("#g.username == authentication.name")...
 * but it seems problematic with spring mvc (@RequestMappings annotation)
 * and also together with Retrofit may lead to great confusion 
 * (although it is possible to make it work by adding one more interface to 
 * controller class) more info about this issue here: 
 * http://www.javatronic.fr/articles/2014/03/15/method_level_security_with_spring_security_and_spring_mvc.html
 * 
 * 
 * @author KK
 *
 */

@Controller
public class GiftSvc implements GiftSvcApi {
	
	private static final Logger log = Logger.getLogger(GiftSvc.class.getName());
	
	@Autowired
	private GiftRepository gifts;

	@Autowired
	private ChainRepository chains;
	
	@Autowired
	private UserRepository users;
	
	// create a gift and return its chain id
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Long addGift(@RequestBody Gift g){
		
		//it is suspicious the gift to have id
		//TODO change exception kind 
		if(g.getId()!=null){
			throw new ResourceNotFoundException();
		}
		
		Long chainId = null;
		
		//if gift has chainId check if chain exits
		//if gift has no chain id - create new chain 
		//and add gift to that chain
		if(g.getChainId()!=null){
			Chain ch = chains.findById(g.getChainId());
			if(ch==null){
				throw new ResourceNotFoundException();
			}
			
			//if chain exists update its creationDate
			chains.update(ch); 
			chainId = g.getChainId();
		}
		else{
			Chain ch = new Chain();
			ch = chains.save(ch);
			g.setChainId(ch.getId());
			chainId = g.getChainId();
		}
		
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); //get logged in username
		log.info("New gift posted by: "+ name +".");
		
		//if the gift has user name and it is different than the session owner
		//throw exception
		if(g.getUsername()!=null){
			if(!g.getUsername().equals(name))
			throw new ResourceNotFoundException(); //TODO add new kinds of exceptions
		}
		
		g.setUsername(user.getUsername());
		g.setCreationDate(new Date());

		gifts.save(g);
		return chainId;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH_GIFT, method=RequestMethod.GET)
	public @ResponseBody Gift getGiftById(@PathVariable("id") Long id) {
		Gift g = gifts.findById(id);
		
		if(g==null){
			throw new ResourceNotFoundException();
		}
			return g;
	}
	
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH_GIFT, method=RequestMethod.DELETE)
	public @ResponseBody Void deleteGiftById(@PathVariable("id") Long id) {
		
		Gift g = gifts.findById(id);
		
		//check if user has right to delete the gift 
		//it would be nice to have it method level security
		//but id doeasn't work well with retrofit 
		//(see description in file header)
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); //get logged in username
		
		if(!g.getUsername().equals(name)){
			//TODO different exception
			throw new ResourceNotFoundException();
		}
		
		//check if the gift is not last in his chain
		//if it is the last one remove also the chain
		Integer giftsInChain = chains.getGiftsSize(g.getChainId());
		
		if(giftsInChain.intValue()==1){
			chains.delete(g.getChainId());
		}
		
		gifts.delete(id); 
		return null;
	}
	
	
	@RequestMapping(value=GiftSvcApi.GIFT_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Gift> findByTitle(@RequestParam(TITLE_PARAMETER) String title){
		List<Gift> retList = (List<Gift>) gifts.findByTitle(title, 0);
		if(retList.size()==0){
			throw new ResourceNotFoundException();
		}
		return retList;
	}

	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH_LIKE, method=RequestMethod.GET)
	public @ResponseBody Void like(@PathVariable("id") Long id) {
		
		Gift g = gifts.findById(id);

		//check if it is like or unlike
		//user already like gift -> unlike it
		//otherwise like
		
		//get current user
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); 
		
		//check if he likes gift
		List<String> usersIds = g.getLikedBy();
		
		if(usersIds.contains(name)){
			//do unlike
			gifts.unlike(id, name);
		 	return null;
		}
		//do like
		gifts.like(id, name);
		
		return null;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH_FLAG, method=RequestMethod.GET)
	public @ResponseBody Void flag(@PathVariable("id") Long id) {
		
		Gift g = gifts.findById(id);

		//check if it is flag or unflag
		//user already like gift -> unflag it
		//otherwise like
		
		//get current user
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); 
		
		//check if he likes gift
		List<String> usersIds = g.getFlaggedBy();
		
		if(usersIds.contains(name)){
			//do unlike
			gifts.unflag(id, name);
		 	return null;
		}
		//do like
		gifts.flag(id, name);
		return null;
	}
		
}
