package com.appspot.potlachkk.model;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonFormat;

//Chain is only a bucket for storing gifts,
//make sense only if there is at least one gift
//so if the only gift is deleted from chain it also
//deletes a chain (in real world we should not delete object
//just flag them, but here I go simple)

//Chain has no owner - owners only complicates stuff

//the updateDate is updated by every operation on gifts
//a list of chains is ordered by updateDate


@PersistenceCapable
public class Chain {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Persistent
	private Date creationtionDate;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Persistent
	private Date updateDate;
	
	@NotPersistent
	private List<Gift> gifts;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationtionDate() {
		return creationtionDate;
	}

	public void setCreationtionDate(Date creationtionDate) {
		this.creationtionDate = creationtionDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	} 

	public List<Gift> getGifts() {
		return gifts;
	}

	public void setGifts(List<Gift> gifts) {
		this.gifts = gifts;
	}	
	
	public Chain(){
		this.creationtionDate = new Date();
		this.updateDate = new Date();
	};
	
}
