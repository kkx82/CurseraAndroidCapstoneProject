package com.appspot.potlachkk.config;


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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.appspot.potlachkk.auth.LoginStatusBuilder;
import com.appspot.potlachkk.auth.LoginStatusBuilder.LoginStatusCode;
import com.appspot.potlachkk.repository.PotlachUserDetailsService;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		
	// This anonymous inner class' onAuthenticationSuccess() method is invoked
	// whenever a client successfully logs in. The class just sends back an
	// HTTP 200 OK status code to the client so that they know they logged
	// in correctly. The class does not redirect the client anywhere like the
	// default handler does with a HTTP 302 response. The redirect has been
	// removed to be friendlier to mobile clients and Retrofit.
	private static final AuthenticationSuccessHandler NO_REDIRECT_SUCCESS_HANDLER = new AuthenticationSuccessHandler() {
	
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
				throws IOException, ServletException {
					response.setStatus(HttpStatus.SC_OK);
					response.setContentType("application/json");
					LoginStatusBuilder lsb = new LoginStatusBuilder(LoginStatusCode.LOGIN_OK, "you are logged in");	
					response.getWriter().write(lsb.toJson());
		}
	};
		
	private static final AuthenticationFailureHandler NO_REDIRECT_FAILURE_HANDLER = new AuthenticationFailureHandler() {
		
		@Override
		public void onAuthenticationFailure(HttpServletRequest request,
				HttpServletResponse response, AuthenticationException authentication)
				throws IOException, ServletException {
			
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			LoginStatusBuilder lsb = new LoginStatusBuilder(LoginStatusCode.LOGIN_FAILURE, "bad credentials");
			response.getWriter().write(lsb.toJson());
		}
		
	};
	
	// Normally, the logout success handler redirects the client to the login page. We
	// just want to let the client know that it successfully logged out and make the
	// response a bit of JSON so that Retrofit can handle it. The handler sends back
	// a 200 OK response and an empty JSON object.
	private static final LogoutSuccessHandler JSON_LOGOUT_SUCCESS_HANDLER = new LogoutSuccessHandler() {
		@Override
		public void onLogoutSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
				throws IOException, ServletException {
			
			response.setStatus(HttpStatus.SC_OK);
			response.setContentType("application/json");
			LoginStatusBuilder lsb = new LoginStatusBuilder(LoginStatusCode.LOGIN_OK, "you are logged out");
			response.getWriter().write(lsb.toJson());
		}
	};
	
	//I don't need any login form, just kind of confirmation using JSON
	private static final AuthenticationEntryPoint JSON_AUTHENTICATION_ENTRY_POINT = new AuthenticationEntryPoint() {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)throws IOException, ServletException {

			response.setHeader("Cache-Control", "no-cache");
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);  
			
			response.setContentType("application/json");
			LoginStatusBuilder lsb = new LoginStatusBuilder(LoginStatusCode.NOT_AUTHORIZED, "you need to log in");
			response.getWriter().write(lsb.toJson());
		}
	};
    
	//The name of the configureGlobal method is not important. However, 
	//it is important to only configure AuthenticationManagerBuilder in a 
	//class annotated with either @EnableWebSecurity, @EnableWebMvcSecurity, 
	//@EnableGlobalMethodSecurity, or @EnableGlobalAuthentication. 
	//Doing otherwise has unpredictable results.
	
	
	@Autowired
	private PotlachUserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        
		// We don't want to cache requests during login
		http.requestCache().requestCache(new NullRequestCache());
		
		//I am not sure if this configuration is not a "work-aroud"
		//maybe there is a simpler/more elegant solution
		
		//Avoid CSRF token related problems with mobile clients
		http.csrf().disable();
		
		//if attempt to access protected URL without authentication
		//send the client HTTP code (instead of redirecting to login form)
		//now to login a POST to /login with password=pass1&username=user1 
		//Content-Type: application/x-www-form-urlencoded must be sent
		http.exceptionHandling().authenticationEntryPoint(JSON_AUTHENTICATION_ENTRY_POINT);
		
		http
			.formLogin()
         	.successHandler(NO_REDIRECT_SUCCESS_HANDLER)
        	.failureHandler(NO_REDIRECT_FAILURE_HANDLER)
            .permitAll()
            .and()
        .logout()
        	.logoutUrl("/logout")
        	.logoutSuccessHandler(JSON_LOGOUT_SUCCESS_HANDLER)
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true) 
        	.permitAll();
		
		//GAE - specific localhost maintenance URL
		http.authorizeRequests().antMatchers("/_ah/**").permitAll();
		
		//configuration URL - should be disabled in production
		http.authorizeRequests().antMatchers("/config").permitAll();
		http.authorizeRequests().antMatchers("/delconfig").permitAll();
        
		//test
		http.authorizeRequests().antMatchers("/image/**").permitAll();
		//http.authorizeRequests().antMatchers("/chain/**").permitAll();
		//http.authorizeRequests().antMatchers("/gift/**").permitAll();
		
		http.authorizeRequests().anyRequest().authenticated();
    }
	
}
