package com.appspot.potlachkk.mobile.models;

import java.util.Date;
import java.util.List;


public class Chain {

	private Long id;
	
	private Date creationtionDate;

	private Date updateDate;
	
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
	}

	@Override
	public String toString() {
		return "Chain [id=" + id + ", creationtionDate=" + creationtionDate
				+ ", updateDate=" + updateDate + ", gifts=" + gifts + "]";
	};

	
	
}
