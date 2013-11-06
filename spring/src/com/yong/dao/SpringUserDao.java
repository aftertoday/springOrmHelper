package com.yong.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.yong.base.ORMUtil;
import com.yong.base.SimpleTemplate;
import com.yong.entity.SpringUser;

@SuppressWarnings("all")
@Repository
public class SpringUserDao{

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	SimpleTemplate simpleTemplate;
	
	public void add(SpringUser user)throws Exception{
		simpleTemplate.add(user);
	}

	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public SimpleTemplate getSimpleTemplate() {
		return simpleTemplate;
	}


	public void setSimpleTemplate(SimpleTemplate simpleTemplate) {
		this.simpleTemplate = simpleTemplate;
	}
	
}
