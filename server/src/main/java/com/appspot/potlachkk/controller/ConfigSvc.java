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

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appspot.potlachkk.model.Chain;
import com.appspot.potlachkk.model.Gift;
import com.appspot.potlachkk.model.User;
import com.appspot.potlachkk.repository.ChainRepository;
import com.appspot.potlachkk.repository.GiftRepository;
import com.appspot.potlachkk.repository.UserRepository;
import com.appspot.potlachkk.repository.UsernameExistsException;


@Controller
public class ConfigSvc {

	@Autowired
	private UserRepository users;
	
	@Autowired
	private GiftRepository gifts;
	
	@Autowired
	private ChainRepository chains;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// private static final Logger log = Logger.getLogger(ConfigSvc.class.getName());

	//configuration URL should be disabled in production environment
	@RequestMapping(value="/config", method=RequestMethod.GET)
	public @ResponseBody boolean configure() throws InterruptedException{
		
		//Create some users...
		User u1 = new User("user1@example.com", "user1", "pass1", new Date());
		User u2 = new User("user2@example.com", "user2", "pass2", new Date());
		User u3 = new User("user3@example.com", "user3", "pass3", new Date());
		User u4 = new User("user4@example.com", "user4", "pass4", new Date());
		User u5 = new User("user5@example.com", "user5", "pass5", new Date());
		
		
		try {
			users.save(u1);
			users.save(u2);
			users.save(u3);
			users.save(u4);
			users.save(u5);
		} catch (UsernameExistsException e) {
			return false;
		}
		/*
		//add a chain		
		Chain ch1 = new Chain();
		ch1 = chains.save(ch1);
		
		//add some gifts
		Gift g1 = new Gift("Picture1", "Title1", "Text1");
		g1.setCreationDate(new Date());
		g1.setChainId(ch1.getId());
		
		Gift g2 = new Gift("Picture2", "Title2", "Text2");
		g2.setCreationDate(new Date());
		g2.setChainId(ch1.getId());
		
		Gift g3 = new Gift("Picture3", "Title3", "Text3");
		g3.setCreationDate(new Date());
		g3.setChainId(ch1.getId());
		
		gifts.save(g1);
		gifts.save(g2);
		gifts.save(g3);
		*/
		return true;		
	}

	
	@RequestMapping(value="/delconfig", method=RequestMethod.GET)
	public @ResponseBody boolean unconfigure(){

		//users.delete("user1");
		//users.delete("user2");
		//users.delete("user3");
		//users.delete("user4");
		//users.delete("user5");
		
		gifts.deleteAll();
		chains.deleteAll();
		
		return true;		
	}
	
}
