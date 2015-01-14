package com.appspot.potlachkk.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

// Represents authentication status
public class LoginStatusBuilder {
	
	public enum LoginStatusCode {
		LOGIN_OK, 
		LOGIN_FAILURE,
		NOT_AUTHORIZED
	}
	
	public class LoginStatus {
		private LoginStatusCode code; //OK|FAIL
		private String message;
	
		private LoginStatus(LoginStatusCode code, String message) {
			this.code = code;
			this.message = message;
		}

		public LoginStatusCode getCode() {
			return this.code;
		}

		public void setCode(LoginStatusCode code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
		
	}
	
	private LoginStatus loginStatus;
	
	//no default constructor allowed
	@SuppressWarnings("unused")
	private LoginStatusBuilder(){};
	
	public LoginStatusBuilder(LoginStatusCode code, String message){
		this.loginStatus = new LoginStatus(code, message);
	}
	
	// this is really outsite of mvc so we need to build the response by hand
	public String toJson() throws JsonProcessingException{
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(this.loginStatus); 
	}
	
}
