package com.yong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yong.dao.SpringUserDao;
import com.yong.dao.UserDao;
import com.yong.entity.SpringUser;
import com.yong.entity.User;

@SuppressWarnings("all")
@Transactional
@Service
public class SpringUserService{

	@Autowired
	SpringUserDao springUserDao;
	@Autowired
	UserDao userDao;
	
	public void add(SpringUser u)throws Exception{
		springUserDao.add(u);
	}
	
	
	public SpringUserDao getSpringUserDao() {
		return springUserDao;
	}
	public void setSpringUserDao(SpringUserDao springUserDao) {
		this.springUserDao = springUserDao;
	}
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
}
