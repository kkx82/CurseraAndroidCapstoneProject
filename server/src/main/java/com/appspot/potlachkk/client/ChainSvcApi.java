package com.appspot.potlachkk.client;

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

import retrofit.http.GET;
import retrofit.http.Path;

import com.appspot.potlachkk.model.Chain;

// Retrofit Interface used for testing and compliance 
public interface ChainSvcApi {
	public static final String CHAIN_SVC_PATH = "/chain";
	public static final String CHAIN_SVC_PATH_CHAIN = "/chain/{id}";
	
	@GET(CHAIN_SVC_PATH)
	public Collection<Chain> getChainList();
	
	@GET(CHAIN_SVC_PATH_CHAIN)
	public Chain getChain(@Path("id") Long id);
	
}
