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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.potlachkk.model.Chain;
import com.appspot.potlachkk.model.Gift;

public class ChainRepository extends JDOCrudRepository<Chain, Long>{
	
	private static final Logger log = Logger.getLogger(ChainRepository.class.getName());

	public ChainRepository() {
		super(Chain.class);
	}
	
	public Chain findById(Long id){
		
		Chain ch = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			ch = (Chain) pm.getObjectById(Chain.class, id);
		}
		catch(Exception e){} //TODO
		finally{
			pm.close();
		}
		return ch;
	}
	
	
	/**
	 * Updates chain's updateDate
	 * 
	 * @return            	chain
	 */
	public Chain update(Chain ch) {
		
		// may it become a bottleneck?
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			if(ch.getId()==null){
				ch = pm.makePersistent(ch);
			}
			else{
				ch = pm.getObjectById(Chain.class, ch.getId());
			}
			ch.setUpdateDate(new Date());
		}
		finally{
			pm.close();
		}
		return ch;	
	}	

	/**
	 * Loads chain from DB and populate it with its
	 * last added gifts
	 * 
	 * @param giftLimit   	limit number of gifts for the fain, if 0 no limit
	 * @return            	chain
	 */
	@SuppressWarnings("unchecked")
	public Chain findByIdAndLoadGifts(Long id, int giftLimit){
		Chain ch = findById(id);
		
		if(ch!=null){
			PersistenceManager pm = PMF.get().getPersistenceManager();	
			List<Gift> gifts = new ArrayList<Gift>();
			try{
				Query query = pm.newQuery(Gift.class);
				query.setFilter("chainId == id");
				query.declareParameters("Long id");
				query.setOrdering("creationDate desc");
				if(giftLimit>0){
					query.setRange(0, giftLimit);
				}
				try{
					gifts = (List<Gift>) query.execute(id);
					gifts.size(); //leazy loading
				}
				catch(Exception e){} //TODO
				finally{
					query.closeAll();
				}
			}
			catch(Exception e){}
			finally{
				pm.close();
			}
			ch.setGifts(gifts);
		}
		return ch;
	}

	
	/**
	 * Loads list of chains from DB and sort by updateDate
	 * 
	 * @param loadGifts		if true load also related gifts
	 * @param chainLimit	limit number of chains, if 0 no limit
	 * @param giftLimit   	limit number of gifts for each chain, if 0 no limit
	 * @return            	list of chains
	 */
	
	@SuppressWarnings("unchecked")
	public Collection<Chain> findAllByUpdateDate(boolean loadGifts, int chainLimit, int giftLimit){
		
		List<Chain> retList = new ArrayList<Chain>();

		PersistenceManager pm = PMF.get().getPersistenceManager();		
		try{
			Query query = pm.newQuery(Chain.class);
			query.setOrdering("updateDate desc");
			if(chainLimit>0){
				query.setRange(0, chainLimit);
			}
			try{
				retList = (List<Chain>) query.execute();
				retList.size();
			}
			catch(Exception e){} //TODO
			finally{
				query.closeAll();
			}
		}
		catch(Exception c){} //TODO
		finally{
			pm.close();
		}
		
		if(loadGifts){
			Iterator<Chain> retListIter = retList.iterator();
			while(retListIter.hasNext()){
				Chain tmp = (Chain) retListIter.next();
				tmp.setGifts( (List<Gift>) getGiftsByCreationDate(tmp.getId(),giftLimit) );
			}
		}
		return retList;
	}	

	public Integer getGiftsSize(Long chainId){
		List<Gift> giftList = (List<Gift>) getGiftsByCreationDate(chainId, 0);
		return giftList.size();
	}
	
	
	/**
	 * Returns lists of gifts ordered by creation date
	 * 
	 * @param lomit   	limit number of gifts, if 0 no limit
	 * @return          list of gifts
	 */
	@SuppressWarnings("unchecked")
	private Collection<Gift> getGiftsByCreationDate(Long id, int limit){
		
		List<Gift> retList = new ArrayList<Gift>();
		PersistenceManager pm = PMF.get().getPersistenceManager();		
		
		try{
			Query query = pm.newQuery(Gift.class);
			query.setFilter("chainId == id");
			query.declareParameters("Long id");
			query.setOrdering("creationDate desc");
			if(limit>0){
				query.setRange(0, limit);
			}
			try{
				retList = (List<Gift>)query.execute(id);
				retList.size(); //lazy loading
			}
			catch(Exception e){}
			finally{
				query.closeAll();;
			}
		}
		catch(Exception c){} //TODO
		finally{
			pm.close();
		}
		return retList;
	}
	
	
	public boolean deleteAll(){
		
		PersistenceManager pm = PMF.get().getPersistenceManager();	
		try{
			Query q = pm.newQuery(Chain.class);
			try{
				q.deletePersistentAll();
			}
			catch(Exception e){}
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
}
