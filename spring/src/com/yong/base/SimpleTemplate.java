package com.yong.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 * springOnly ��װ���Ĺ����࣬�������ݲ���
 * @author ZhixiangYong
 */
@SuppressWarnings("all")
@Repository
public class SimpleTemplate{
	
	/**springOnly �������߲���*/
	private ORMUtil ormUtil;
	
	/**springOnly ����spring jdbcTemplate ���� CRUD����*/
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**���ò����Ƿ���� sql ��� true:�����false���������Ĭ��false*/
	private static Boolean showSql = false;
	
	/**
	 * ��������
	 * @param t
	 * @throws Exception
	 */
	public void add(Object t)throws Exception{
		String sql = ormUtil.createInsertSql(t);
		printSql(sql);
		jdbcTemplate.execute(sql);
	}
	
	/**
	 * ���ݴ���������ѯ�б�
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
	 * ����sql����Ŀ�����ѯ�б�
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
	 * ����������ѯָ����Χ�ڵļ�¼
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
	 * ����sql��ҳ��ѯ
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
	 * ��������ͳ�Ƽ�¼����
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
	 * ��������ͳ�Ƽ�¼����
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
	 * ����������ҳ��ѯ
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
	 * ����sql��ҳ��ѯ
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
	 * ����������ݿ�
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
	 * ���ݶ����������
	 * @param newer ���²�����������id
	 * @param params ��������
	 * @return
	 * @throws Exception ���²���Ϊ��ʱ�׳��쳣
	 */
	public int update(Object newer,Object params)throws Exception{
		String sql = ormUtil.createUpdateSql(newer, params);
		printSql(sql);
		return jdbcTemplate.update(sql);
	}
	
	/**
	 * sql����������
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
