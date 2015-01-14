package com.appspot.potlachkk.mobile.client;

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



import retrofit.RetrofitError;

import com.appspot.potlachkk.mobile.events.ErrorNotAuthorized;
import com.appspot.potlachkk.mobile.events.NotFoundError;
import com.appspot.potlachkk.mobile.events.OtherServiceError;
import com.appspot.potlachkk.mobile.events.ServiceError;
import com.squareup.otto.Bus;

// Common error handler to cope with service errors

public class ErrorHandler {
	Bus bus;
	
	public ErrorHandler(Bus bus) {
		super();
		this.bus = bus;
	}

	public void handleFailure(RetrofitError error) {
		if(error!=null){
		if (error.getResponse().getStatus() == 401){
			bus.post(new ErrorNotAuthorized());
			return;
		}
		if (error.getResponse().getStatus() == 500){
			bus.post(new ServiceError());
			return;
		}
		if (error.getResponse().getStatus() == 404){
			bus.post(new NotFoundError());
			return;
		}
		}
		bus.post(new OtherServiceError());
		
	}
}