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

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.stereotype.Service;

import com.appspot.potlachkk.model.Gift;
import com.appspot.potlachkk.model.User;

@Service
public class GiftRepository extends JDOCrudRepository<Gift, Long>{

	private static final Logger log = Logger.getLogger(GiftRepository.class.getName());

	public GiftRepository() {
		super(Gift.class);
	}
	
	/**
	 * Find gift by title
	 * 
	 * @param title   	
	 * @param limit		limit number of gifts, if 0 no limit
	 * @return          list of gifts
	 */
	@SuppressWarnings("unchecked")
	public Collection<Gift> findByTitle(String title, Integer limit){
		
		List<Gift> retList = new ArrayList<Gift>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
			
		try{
			Query query = pm.newQuery(Gift.class);
			query.setFilter("title == n");
			query.declareParameters("String n");
			query.setOrdering("creationDate desc");
			
			if(limit > 0){
				query.setRange(0, limit);
			}
			
			try{
				retList = (List<Gift>) query.execute(title);
				retList.size();
			}
			catch(Exception e){}
			finally{
				query.closeAll();
			}
		}
		catch(Exception e){} //TODO
		finally{
			pm.close();
		}
		
		return retList;
	}
	
	public Gift findById(Long id){
		
		Gift tmp = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			tmp = (Gift) pm.getObjectById(Gift.class, id);
		}
		catch(Exception e){} //TODO
		finally{
			pm.close();
		}
		return tmp;
	}
	
	public Gift findById(Gift g){
		
		Gift tmp = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			tmp = (Gift) pm.getObjectById(Gift.class, g.getId());
		}
		catch(Exception e){} //TODO
		finally{
			pm.close();
		}
		return tmp;
	}
		
	public boolean deleteAll(){
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			Query q = pm.newQuery(Gift.class);
			try{
				q.deletePersistentAll();
			}
			catch(Exception e){} //TODO
			finally{
				q.closeAll();
			}
		}
		catch(Exception e){
			return false;
		}
		finally{
			pm.close();
		}
		return true;
	}
		
	@Override
	public void delete(Gift g){
	
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			Gift tmp = pm.getObjectById(Gift.class, g.getId());
			pm.deletePersistent(tmp);
		}
		catch (Exception e){}
		finally{
			pm.close();		
		}
	}
	
	public void like(Long id, String username){
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		Transaction tx = pm.currentTransaction();
		try {
	        tx.begin();
		
	        Gift tmp = pm.getObjectById(Gift.class, id);

			//increase counter
			tmp.setLikeCount(tmp.getLikeCount()+1);
			
			//add to likers
			tmp.addLiker(username);

			//TODO - czy to ma sens?
			//add gift to liked gift list of current user
			User usr1 = pm.getObjectById(User.class, username);
			usr1.addLikedGift(id);
			
			
			//many-to-many
			//add like count to gift owner
			String giftOwner = tmp.getUsername();
			User usr2 = pm.getObjectById(User.class, giftOwner);
			usr2.setLikeCount(usr2.getLikeCount()+1);
			
			pm.makePersistent(tmp);
			pm.makePersistent(usr1);
			pm.makePersistent(usr2);
			
			tx.commit();
		}
		catch (Exception e){} //TODO
		finally{
			if (tx.isActive()) {	
	            tx.rollback();
	        }
			pm.close();		
		}
	}
	
	public void unlike(Long id, String username){
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			
			Gift tmp = pm.getObjectById(Gift.class, id);
			
			//increase counter
			tmp.setLikeCount(tmp.getLikeCount()-1);
			
			//add to likers
			tmp.deleteLiker(username);
			
			//add gift to liked gift list of current user
			User usr1 = pm.getObjectById(User.class, username);
			usr1.removeLikedGift(id);
			
			//add like count to gift owner
			String giftOwner = tmp.getUsername();
			User usr2 = pm.getObjectById(User.class, giftOwner);
			usr2.setLikeCount(usr2.getLikeCount()-1);
			
			pm.makePersistent(tmp);
			pm.makePersistent(usr1);
			pm.makePersistent(usr2);
			
			tx.commit();
		}
		catch (Exception e){}
		finally{
			if (tx.isActive()) {	
	            tx.rollback();
	        }
			pm.close();		
		}
	}

	public void flag(Long id, String username){
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		
		try{
			Gift tmp = pm.getObjectById(Gift.class, id);

			//increase counter
			tmp.setFlagCount(tmp.getFlagCount()+1);
			
			//add to flaggers
			tmp.addFlagger(username);		
			pm.makePersistent(tmp);
		}
		catch (Exception e){}
		finally{
			pm.close();		
		}
	}
	
	public void unflag(Long id, String username){
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			Gift tmp = pm.getObjectById(Gift.class, id);
			
			//decrease counter
			tmp.setFlagCount(tmp.getFlagCount()-1);
			
			//delete from laggers
			tmp.deleteFlagger(username);
			
			pm.makePersistent(tmp);
			//pm.makePersistent(usr);
		}
		catch (Exception e){}
		finally{
			pm.close();		
		}
	}
}
