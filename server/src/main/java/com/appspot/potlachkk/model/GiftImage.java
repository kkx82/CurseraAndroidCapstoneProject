package com.appspot.potlachkk.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Blob;

@JsonAutoDetect
@PersistenceCapable
public class GiftImage {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@JsonIgnore 
	@Persistent
	private Blob data;
	
	@Persistent
	private String filename;
	
	@Persistent
	private String mimeType;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Image [filename=" + filename + ", mimeType=" + mimeType + "]";
	}
	
}
