package com.appspot.potlachkk.repository;


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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.appspot.potlachkk.model.User;


public class UserRepository{

	private static final Logger log = Logger.getLogger(UserRepository.class.getName());

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// needed by UserDetailsService	
	public User findByUsername(String username){
		log.info("Searching for user by username");
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		User user;
		
		try {
			user = pm.getObjectById(User.class, username);
		}
		catch (JDOObjectNotFoundException e){
			user = null;
		}
		finally {
			pm.close();
		}
		
		return user;
	}
	
	//we need to be sure there is no other user with 
	//the same username
	public User save(User u) throws UsernameExistsException {
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		
		User newUser;

		//In real life when user registration is possible 
		//this should be a transaction
		User tmpUser = findByUsername(u.getUsername());

		if(tmpUser!=null){
			log.warning("Attempt to create a user with existing username: " + u.getUsername() + ".");
        	throw new UsernameExistsException("");
        }
		
		String p = u.getPassword();
		u.setPassword(passwordEncoder.encode(p));
		
		try{
			newUser = pm.makePersistent(u);
		}
		finally{
			pm.close();
		}
		return newUser;	
	}	
	
	public void delete(String username){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		User u = pm.getObjectById(User.class, username);

		try {
			pm.deletePersistent(u);
		}
		finally {
			pm.close();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<User> getUsersByLikeCount(Integer limit){
		List<User> retList = new ArrayList<User>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try{
			Query query = pm.newQuery(User.class);
			query.setOrdering("likeCount desc");
			if(limit > 0){
				query.setRange(0, limit);
			}
			try{
				retList = (List<User>) query.execute();
				retList.size(); //seems like not detachable object are "lazy loaded"
			}
			catch(Exception e){} //TODO
			finally{
				query.closeAll();;
			}
		}
		catch(Exception e){} //TODO
		finally{
			pm.close();
		}
		return retList;
	}

}
