package com.yong.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yong.dao.SpringUserDao;
import com.yong.dao.UserDao;
import com.yong.dto.UserSpringDto;
import com.yong.entity.SpringUser;
import com.yong.entity.User;

@SuppressWarnings("all")
@Transactional
@Service
public class UserService{

	@Autowired
	SpringUserDao springUserDao;
	@Autowired
	UserDao userDao;
	
	public void addTwo(User u,SpringUser spu)throws Exception{
			springUserDao.add(spu);
			userDao.add(u);
	}
	
	public List<User> findList(User u)throws Exception{
		return userDao.findListByConditions(u);
	}
	
	public List<UserSpringDto> findDto(User u)throws Exception{
		return userDao.findDto(u);
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
