package com.yong.dto;

import javax.persistence.Column;

public class UserSpringDto {

	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="spring_id")
	private Integer springId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="user_address")
	private String address;
	
	@Column(name="spring_name")
	private String springName;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getSpringId() {
		return springId;
	}
	public void setSpringId(Integer springId) {
		this.springId = springId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSpringName() {
		return springName;
	}
	public void setSpringName(String springName) {
		this.springName = springName;
	}
}
