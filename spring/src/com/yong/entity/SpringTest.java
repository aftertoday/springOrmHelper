package com.yong.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="spring_test")
public class SpringTest {

	@Id
	@Column(name="spring_id")
	private Integer id;
	
	@Column(name="spring_status")
	private Integer status;
	
	@Column(name="spring_name")
	private String name;
	
	@Column(name="spring_remark")
	private String remark;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
