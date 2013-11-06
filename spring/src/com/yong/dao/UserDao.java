package com.yong.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.yong.base.ORMUtil;
import com.yong.base.SimpleTemplate;
import com.yong.dto.UserSpringDto;
import com.yong.entity.User;

@SuppressWarnings("all")
@Repository
public class UserDao{

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	SimpleTemplate simpleTemplate;
	
	public void add(User u)throws Exception{
		String sql = ORMUtil.createInsertSql(u);
		//jdbcTemplate.execute(sql);
		simpleTemplate.add(u);
	}
	
	public List<User> findListByConditions(User u)throws Exception{
		List<User> list = new ArrayList<User>();
		list = simpleTemplate.list(u);
		return list;
	}
	
	public List<UserSpringDto> findDto(User u)throws Exception{
		List<UserSpringDto> list = new ArrayList<UserSpringDto>();
		String sql = "select id user_id,name user_name from user where id = "+u.getId();
		list = simpleTemplate.list(sql, UserSpringDto.class);
		return list;
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
