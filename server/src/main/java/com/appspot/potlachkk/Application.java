package com.appspot.potlachkk;

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

import org.gmr.web.multipart.GMultipartResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.appspot.potlachkk.repository.ChainRepository;
import com.appspot.potlachkk.repository.GiftRepository;
import com.appspot.potlachkk.repository.ImageRepository;
import com.appspot.potlachkk.repository.PotlachUserDetailsService;
import com.appspot.potlachkk.repository.UserRepository;

@Configuration
@ComponentScan("com.appspot.potlachkk.*")
@EnableWebMvc
@Controller
public class Application extends WebMvcConfigurerAdapter {
	
	// this may be extended in the future to port the application 
	// to different environments
	@Bean
	public GiftRepository giftRepository(){
		return new GiftRepository();
	}
	
	@Bean
	public UserRepository userRepository(){
		return new UserRepository();
	}
	
	@Bean
	public ChainRepository chainRepository(){
		return new ChainRepository();
	}
	
	@Bean
	public ImageRepository imageRepository(){
		return new ImageRepository();
	}
	
	@Bean
	public UserDetailsService userDetailsService(){
		return new PotlachUserDetailsService();
	}

	// Encoder for all passwords in the app
	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		GMultipartResolver  resolver = new GMultipartResolver();
	    resolver.setMaxUploadSize(1048576);   
	    return resolver;
	}

}
