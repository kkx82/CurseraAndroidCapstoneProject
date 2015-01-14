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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appspot.potlachkk.client.UserSvcApi;
import com.appspot.potlachkk.model.User;
import com.appspot.potlachkk.repository.UserRepository;

@Controller
public class UserSvc implements UserSvcApi {
	
	// private static final Logger log = Logger.getLogger(ChainSvc.class.getName());
	
	@Autowired
	private UserRepository users;

	@Override
	@RequestMapping(value=UserSvcApi.USER_SVC_PATH_TOPGIVER, method=RequestMethod.GET)
	public @ResponseBody Collection<User> getTopGivers() {
		return users.getUsersByLikeCount(2);		
	}
	
}
