package com.yong.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 * springOnly 封装核心工具类，用于数据操作
 * @author ZhixiangYong
 */
@SuppressWarnings("all")
@Repository
public class SimpleTemplate{
	
	/**springOnly 辅助工具参数*/
	private ORMUtil ormUtil;
	
	/**springOnly 集成spring jdbcTemplate 进行 CRUD操作*/
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**配置参数是否输出 sql 语句 true:输出；false：不输出；默认false*/
	private static Boolean showSql = false;
	
	/**
	 * 插入数据
	 * @param t
	 * @throws Exception
	 */
	public void add(Object t)throws Exception{
		String sql = ormUtil.createInsertSql(t);
		printSql(sql);
		jdbcTemplate.execute(sql);
	}
	
	/**
	 * 根据传入条件查询列表
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public List list(Object t)throws Exception{
		List list = new ArrayList();
		System.out.println(jdbcTemplate.getDataSource().getConnection());
		try{
			String sql = ormUtil.createQuerySql(t);
			printSql(sql);
			list = ormUtil.getObjectList(jdbcTemplate.queryForList(sql),t);
		}
		catch(Exception e){
			throw new Exception("query error:"+e.getMessage());
		}
		return list;
	}
	

	/**
	 * 根据sql，和目标类查询列表
	 * @param sql
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List list(String sql,Class clazz)throws Exception{
		List list = new ArrayList();
		System.out.println(jdbcTemplate.getDataSource().getConnection());
		try{
			list = ormUtil.getObjectList(jdbcTemplate.queryForList(sql),clazz);
		}
		catch(Exception e){
			throw new Exception("query error:"+e.getMessage());
		}
		return list;
	}
	
	/**
	 * 根据条件查询指定范围内的记录
	 * @param t
	 * @param start
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List findListLimited(Object t,Integer start,Integer pageSize)throws Exception{
		String sql = ormUtil.createPageQuerySql(t, start, pageSize);
		printSql(sql);
		List list = ormUtil.getObjectList(jdbcTemplate.queryForList(sql),t);
		return list;
	}
	
	/**
	 * 根据sql分页查询
	 * @param sql
	 * @param start
	 * @param pageSize
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List findListLimited(String sql,Integer start,Integer pageSize,Class clazz)throws Exception{
		String pageSql = ormUtil.wrapPageQuerySql(sql, start, pageSize);
		printSql(pageSql);
		List list = ormUtil.getObjectList(jdbcTemplate.queryForList(pageSql),clazz);
		return list;
	}
	
	/**
	 * 根据条件统计记录总数
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public Integer count(Object t)throws Exception{
		String countSql = ormUtil.createCountQuerySql(t);
		printSql(countSql);
		Integer count = jdbcTemplate.queryForInt(countSql);
		return count;
	} 
	/**
	 * 根据条件统计记录总数
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Integer count(String sql)throws Exception{
		String countSql = ormUtil.getCountSqlByQuery(sql);
		printSql(countSql);
		Integer count = jdbcTemplate.queryForInt(countSql);
		return count;
	} 
	
	/**
	 * 根据条件分页查询
	 * @param t
	 * @param start
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public Page findByPage(Object t,Integer start,Integer pageSize)throws Exception{
		Page page = new Page();
		Integer count = this.count(t);
		List list = findListLimited(t, start, pageSize);
		page.setList(list);
		if(start == 0){
			page.setCurrentPage(1);
		}else{
			page.setCurrentPage(start%pageSize==0?start/pageSize:start/pageSize+1);
		}
		page.setTotal(count);
		page.setPageSize(pageSize);
		page.setTotalPage(count%pageSize==0?count/pageSize:count/pageSize+1);
		return page;
	}
	
	/**
	 * 根据sql分页查询
	 * @param sql
	 * @param start
	 * @param pageSize
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public Page findByPage(String sql,int start ,int pageSize,Class clazz)throws Exception{
		
		Page page = new Page();
		Integer count = this.count(sql);
		List list = findListLimited(sql, start, pageSize,clazz);
		page.setList(list);
		if(start == 0){
			page.setCurrentPage(1);
		}else{
			page.setCurrentPage(start%pageSize==0?start/pageSize:start/pageSize+1);
		}
		page.setTotal(count);
		page.setPageSize(pageSize);
		page.setTotalPage(count%pageSize==0?count/pageSize:count/pageSize+1);
		return page;
	}
	
	/**
	 * 保存对象到数据库
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public Integer save(Object t)throws Exception{
		String sql = ormUtil.createInsertSql(t);
		printSql(sql);
		jdbcTemplate.execute(sql);
		return null;
	}
	
	/**
	 * 根据对象更新数据
	 * @param newer 更新参数，不更新id
	 * @param params 更新条件
	 * @return
	 * @throws Exception 更新参数为空时抛出异常
	 */
	public int update(Object newer,Object params)throws Exception{
		String sql = ormUtil.createUpdateSql(newer, params);
		printSql(sql);
		return jdbcTemplate.update(sql);
	}
	
	/**
	 * sql语句输出处理
	 * @param sql
	 */
	protected void printSql(String sql){
		if(showSql){
			System.out.println(sql);
		}
	}
	
	public ORMUtil getOrmUtil() {
		return ormUtil;
	}
	public void setOrmUtil(ORMUtil ormUtil) {
		this.ormUtil = ormUtil;
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static Boolean getShowSql() {
		return showSql;
	}

	public static void setShowSql(Boolean showSql) {
		SimpleTemplate.showSql = showSql;
	}
	
}
