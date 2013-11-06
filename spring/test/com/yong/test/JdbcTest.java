package com.yong.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext.xml")
public class JdbcTest {

	
	
	@Before
	public void before(){
		try{
			/*if(simpleTamplate == null){
				ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
				simpleTamplate = (SimpleTemplate)ctx.getBean("simpleTemplate");
				userDao = (UserDao)ctx.getBean("userDao");
			}*/
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
