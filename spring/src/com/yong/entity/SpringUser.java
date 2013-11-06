package com.yong.entity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="spring_test")
public class SpringUser {

	private Long id;
	
	@Column(name="spring_name")
	private String name;
	
	@Column(name="spring_status")
	private String status;
	
	@Column(name="spring_remark")
	private Integer remark;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getRemark() {
		return remark;
	}
	public void setRemark(Integer remark) {
		this.remark = remark;
	}
	
}
