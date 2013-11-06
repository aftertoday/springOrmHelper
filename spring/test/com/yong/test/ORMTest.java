package com.yong.test;

import org.junit.Before;
import org.junit.Test;

import com.yong.base.ORMUtil;
import com.yong.entity.Product;
import com.yong.entity.User;

public class ORMTest {

	ORMUtil orm = null;
	
	@Before
	public void before(){
		if(orm == null){
			orm = new ORMUtil();
		}
	}
	@Test
	public void ormTest()throws Exception {
		User u = new User();
		User up = new User();
		up.setAddress("addd");
		Product p = new Product();
		
		System.out.println(orm.getTableName(u)+"   "+orm.getTableName(p));
		
		System.out.println(orm.createInsertSql(u));
		
		System.out.println(orm.createInsertSql(p));
		try{
			System.out.println(orm.createUpdateSql(up, u));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
