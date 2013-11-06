package com.yong.test;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yong.base.ORMUtil;
import com.yong.base.Page;
import com.yong.base.SimpleTemplate;
import com.yong.dao.UserDao;
import com.yong.dto.UserSpringDto;
import com.yong.entity.SpringTest;
import com.yong.entity.SpringUser;
import com.yong.entity.User;
import com.yong.service.SpringUserService;
import com.yong.service.UserService;


@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext.xml")
public class MappingUtilTest {
	
	@Test
	public void columnFieldMappingTest(){
		ORMUtil  orm = new ORMUtil ();
		User u = new User();
		try {
			System.out.println(orm.getColumnVSFieldMapping(u));
			System.out.println(orm.getTableName(u));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void columnVauleTest(){
		ORMUtil  orm = new ORMUtil ();
		User u = new User();
		u.setAddress("templateAddress");
		u.setAge(3);
		u.setName("templateName");
		try {
			System.out.println(orm.getColumnVSValue(u,true,false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void conditioinsTest(){
		ORMUtil  orm = new ORMUtil ();
		User u = new User();
		u.setAddress("templateAddress");
		u.setAge(3);
		u.setName("templateName-2");
		try {
			//System.out.println(orm.createInsertSql(u));
			userDao.add(u);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void queryStringCreateTest(){
		ORMUtil  orm = new ORMUtil ();
		User u = new User();
		u.setAddress("templateAddress");
		SpringTest test = new SpringTest();
		//u.setAge(3);
		//u.setName("templateName");
		//u.setId(1);
		try {
			System.out.println(orm.createQuerySql(u));
			System.out.println(orm.getColumnFieldTypeMapping(u));
			System.out.println(orm.getColumnFieldTypeMapping(test));
			System.out.println(orm.getColumnSetterMapping(test));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void simpleTamplateTest(){
		User u = new User();
		u.setId(1);
		try {
			List<User> list = (List<User>)simpleTamplate.list(u);
			for(User user:list){
				System.out.println(user.getAddress()+"   "+user.getId()+"  "+user.getName()+"  "+user.getAge());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void queryTest(){
		SpringTest spring = new SpringTest();
		spring.setStatus(1);
		List<SpringTest> list;
		try {
			list = (List<SpringTest>)simpleTamplate.list(spring);
			for(SpringTest test:list){
				System.out.println(test.getId()+"  "+test.getName()+"  "+test.getRemark()+"  "+test.getStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void countTest(){
		SpringTest spring = new SpringTest();
		try {
			Page page = simpleTamplate.findByPage(spring, 2, 20);
			List<SpringTest> list = (List<SpringTest>)page.getList();
			System.out.println(page.getCurrentPage()+"  "+page.getPageSize()+"  "+page.getTotal()+"  "+page.getTotalPage());
			for(SpringTest test:list){
				System.out.println(test.getName()+"  "+test.getRemark()+"  "+test.getStatus()+"  "+test.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void saveTest(){
		User u = new User();
		u.setAddress("save address");
		u.setAge(1);
		u.setName("save name");
		u.setSpring_id(1);
		try{
			simpleTamplate.save(u);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void getSetterSqlTest(){
		User u = new User();
		u.setAddress("update1");
		u.setAge(1);
		u.setName("update1");
		u.setSpring_id(1);
		u.setId(410);
		
		User param = new User();
		param.setId(1);
		try{
			simpleTamplate.update(u, param);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*@Test
	public void joinQueryTest(){
		UserSpringDto usDto = new UserSpringDto();
		List<UserSpringDto> list = userDao.getUserSpringByConditions(usDto);
		for(UserSpringDto dto:list){
			System.out.println(dto.getUserId()+"  "+dto.getSpringName()+"  "+dto.getSpringName()+"  "+dto.getSpringId()+"  "+dto.getUserName());
		}
	}
	
	@Test
	public void joinPageTest(){
		UserSpringDto usDto = new UserSpringDto();
		Page page = userDao.findByPage(usDto, 2, 2);
		System.out.println(page.getCurrentPage()+"  "+page.getPageSize()+"  "+page.getStartIndex()+"  "+page.getTotal()+"  "+page.getTotalPage());
		for(UserSpringDto dto:(List<UserSpringDto>)page.getList()){
			System.out.println(dto.getUserId()+"  "+dto.getSpringName()+"  "+dto.getSpringName()+"  "+dto.getSpringId()+"  "+dto.getUserName());
		}
	}*/
	
	@Test
	public void transactionTest()throws Exception{
		/*List<User> list = new ArrayList<User>();
		for(int i=0;i<5;i++){
			User u = new User();
			u.setAddress("transaction test "+i);
			u.setName("transaction name "+i);
			u.setSpring_id(i);
			u.setAge(i);
			list.add(u);
		}
		userDao.addAllUser(list);*/
		User u = new User();
		u.setAddress("transaction test ");
		u.setName("transaction name ");
		u.setSpring_id(1);
		u.setAge(1);
		SpringUser spu = new SpringUser();
		spu.setName("springName");
		spu.setRemark(1);
		spu.setStatus("status");
		userService.addTwo(u, spu);
		/*try{
			userDao.addTwo(u, spu);
		}catch(Exception e){
			e.printStackTrace();
		}*/
	}
	
	@Test
	public void tranSaveTest(){
		try{
			SpringUser spu = new SpringUser();
			spu.setName("springName");
			spu.setRemark(1);
			spu.setStatus("status");
			springUserService.add(spu);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void UserListTest()throws Exception{
		User u = new User();
		u.setId(34);
		List<User> list = userService.findList(u);
		for(User usr : list){
			System.out.println(usr.getId()+"   "+usr.getName()+"  "+usr.getAddress()+"  "+usr.getAge());
		}
	}
	
	
	@Test
	public void dtoFindTest()throws Exception{
		User u = new User();
		u.setId(33);
		List<UserSpringDto> list = userService.findDto(u);
		for(UserSpringDto usr : list){
			//System.out.println(usr.getId()+"   "+usr.getName()+"  "+usr.getAddress()+"  "+usr.getAge());
			System.out.println(usr.getUserId()+"  "+usr.getUserName());
		}
	}
	
	public static SimpleTemplate simpleTamplate;
	public static ORMUtil  orm ;
	public static UserDao userDao;
	public static UserService userService;
	public static SpringUserService springUserService;
	
	@Before
	public void before(){
		try{
			orm = new ORMUtil ();
			if(springUserService == null){
				ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
				//simpleTamplate = (SimpleTemplate)ctx.getBean("simpleTemplate");
				//userDao = (UserDao)ctx.getBean("userDao");
				userService = (UserService)ctx.getBean("userService");
				springUserService = (SpringUserService)ctx.getBean("springUserService");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
