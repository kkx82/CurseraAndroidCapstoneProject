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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import retrofit.http.Multipart;

import com.appspot.potlachkk.client.ImageSvcApi;
import com.appspot.potlachkk.model.GiftImage;
import com.appspot.potlachkk.repository.ImageRepository;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;


//it can not implement ImageSvcApi 
//public class ImageSvc implements ImageSvcApi
//https://class.coursera.org/mobilecloud-001/forum/thread?thread_id=280
@Controller
public class ImageSvc{
	
	// private static final Logger log = Logger.getLogger(ChainSvc.class.getName());
	
	@Autowired
	private ImageRepository images;
	
	@Multipart
	@RequestMapping(value=ImageSvcApi.IMG_SVC_PATH, method=RequestMethod.POST)
	public  @ResponseBody Long addImage(@RequestParam("imageData") MultipartFile imageFile) throws Exception {
		
		//TODO here could be some kind of validation to
		// see what kind of data is really uploaded to the 
		// server
		GiftImage img = new GiftImage();
		
		img.setMimeType(imageFile.getContentType());
		img.setFilename(imageFile.getOriginalFilename());

		Blob data = new Blob(imageFile.getBytes());
		
		img.setData(data);
	
		GiftImage retImg = images.save(img);
		return retImg.getId();
	}

	@RequestMapping(value=ImageSvcApi.IMG_SVC_PATH_ID, method=RequestMethod.GET)
	public ResponseEntity<byte[]> getImageById(@PathVariable("id") Long id) {
		
		GiftImage img = images.findOne(id);	
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(img.getMimeType()));
	    return new ResponseEntity<byte[]>(img.getData().getBytes(), headers, HttpStatus.OK);
	}
	
	// return smaller version of requested image
	// it uses ImageService to manipulate image data
	@RequestMapping(value=ImageSvcApi.IMG_SVC_PATH_ID+"/mini", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getImageByIdMini(@PathVariable("id") Long id) {
		GiftImage img = images.findOne(id);
		
		byte[] oldImageData = img.getData().getBytes();
		
		ImagesService imagesService = ImagesServiceFactory.getImagesService(); 
		Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
		Transform resize = ImagesServiceFactory.makeResize(200, 300, false);

		Image newImage = imagesService.applyTransform(resize, oldImage);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(img.getMimeType()));

	    return new ResponseEntity<byte[]>(newImage.getImageData(), headers, HttpStatus.OK);
	}	
}
