package com.yong.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

/**
 * 该工具类用于处理实体类到数据库表的映射关系简单处理
 * 实体类必须加 javax.persistence.Table 注解 表示改实体为持久化实体bean  name 属性必须指定，表示对应数据库表名
 * 需要映射字段必须加 javax.persistence.Column 注解 表示映射数据表中的对应字段 name 属性必须指定，表示对应表中的字段名称
 * 主键需要加 javax.persistence.Id 注解 必须 
 * 数据库基于 mysql 进行SQL 封装
 * @author ZhixiangYong
 * @version 1.0
 */
@SuppressWarnings("all")
public class ORMUtil{
	
	public static final String COLUMNS_STRING = "column";
	public static final String VALUES_STRING = "value";
	
	/**
	 * 根据持久化对象创建该对象的 插入 sql 语句
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static String createInsertSql(Object t)throws Exception{
		String tableName = getTableName(t);
		Hashtable<String,String> conditions = getColumnAndValuesArray(t,true,false);
		String columns = conditions.get(COLUMNS_STRING);
		String values = conditions.get(VALUES_STRING);
		String sql = "insert into "+tableName+"("+columns+") values("+values+")";
		return sql;
	}
	
	/**
	 * 根据更新对象及更新条件获取更新语句
	 * @param newer
	 * @param params 若条件为空则设置查询条件为空 进行全表更新操作
	 * @return
	 * @throws Exception
	 */
	public static String createUpdateSql(Object newer,Object params)throws Exception{
		String tableName = getTableName(newer);
		String modifier = getUpdateModifier(newer);
		String where = params == null?null:getWhereConditions(params);
		return "update "+tableName+" "+modifier+" "+((where==null||where.equals(""))?"":where);
	}
	
	/**
	 * 获取查询语句
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static String createQuerySql(Object t)throws Exception{
		String tableName = getTableName(t);
		String sql = "select * from "+tableName+getWhereConditions(t);
		return sql;
	}
	
	/**
	 * 获取分页查询sql
	 * @param t
	 * @param start
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public static String createPageQuerySql(Object t,Integer start,Integer pageSize)throws Exception{
		String sql = createQuerySql(t);
		return sql+" limit "+start+","+pageSize;
	}
	
	/**
	 * 包装分页查询sql
	 * @param sql
	 * @param start
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public static String wrapPageQuerySql(String sql,Integer start,Integer pageSize)throws Exception{
		return sql+" limit "+start+","+pageSize;
	}
	
	/**
	 * 获取统计sql
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static String createCountQuerySql(Object t)throws Exception{
		String tableName = getTableName(t);
		String sql = "select count(*) from "+tableName+getWhereConditions(t);
		return sql;
	}
	
	/**
	 * 根据查询语句获取统计语句
	 * @param sql
	 * @return
	 */
	public static String getCountSqlByQuery(String sql){
		sql = " select count(1) "+sql.substring(sql.toLowerCase().indexOf("from"));
		return sql;
	}
	
	/**
	 * 获取数据库字段名词及实体字段的getter方法表,获取查询条件
	 * 实体中所有字段定义类型必须为对象类型（不能使用 int float 等单值类型，必须使用包装类型 Integer Float 等）
	 * @param includeNullColumn true:获取所有字段包括值为空的关系；false/null：只获取值不为空的字段
	 * @param includeIdentify true:获取字段包含主键，false/null:不包含主键
	 * @return
	 */
	public static Hashtable<String,String> getColumnVSValue(Object t,Boolean includeNullColumn,Boolean includeIdentify)throws Exception{
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			Object columnValue = getFieldValue(t,field);
			//若字段值为空则不计入sql
			if(columnValue == null || StringUtils.isEmpty(columnValue.toString())){
				continue;
			}
			if(!includeIdentify && isIdentify(field)){
				continue;
			}
			if(includeNullColumn){
				mapping.put(columnName,columnValue==null?"":columnValue.toString());
			}
			else{
				if(columnValue!=null && !columnValue.toString().equals("")){
					mapping.put(columnName, columnValue.toString());
				}
			}
		}
		return mapping;
	}
	
	/**
	 * 获取字段和值得字符串序列
	 * @param t
	 * @param includeNullColumn 是否包含 值为空的字段
	 * @param includeIdentify  是否包含主键
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String,String> getColumnAndValuesArray(Object t,Boolean includeNullColumn,Boolean includeIdentify)throws Exception{
		Hashtable<String,String> columnTable = getColumnVSValue(t,includeNullColumn,includeIdentify);
		if(columnTable.isEmpty()){
			return null;
		}
		String[] column = new String[columnTable.size()];
		String[] values = new String[columnTable.size()];
		Hashtable<String,String> result = new Hashtable<String,String>();
		Iterator<String> keys = columnTable.keySet().iterator();
		int i = 0;
		while(keys.hasNext()){
			String keyName = keys.next();
			String keyValue = columnTable.get(keyName);
			column[i] = keyName;
			values[i] = "'"+keyValue+"'";
			i++;
		}
		result.put(COLUMNS_STRING,Arrays.toString(column).replaceAll("\\[", "").replaceAll("\\]", ""));
		result.put(VALUES_STRING,Arrays.deepToString(values).replaceAll("\\[", "").replaceAll("\\]", ""));
		return result;
	}
	
	/**
	 * 获取字段名称和属性之间的映射关系，key:columnName  ; value:fieldName;
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String,String> getColumnVSFieldMapping(Object t)throws Exception{
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			//若字段值为空则不计入
			if(StringUtils.isEmpty(columnName)){
				continue;
			}
			mapping.put(field.getName(),columnName);
		}
		return mapping;
	}
	
	/**
	 * 获取 数据表字段和实体bean setter 映射关系
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String,String> getColumnSetterMapping(Object t) throws Exception{
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			if(StringUtils.isEmpty(columnName)){
				continue;
			}
			mapping.put(columnName,getSetter(field.getName()));
		}
		return mapping;
	}
	
	/**
	 * 获取数据表字段和实体bean getter 映射关系
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String,String> getColumnGetterMapping(Object t) throws Exception{
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			if(StringUtils.isEmpty(columnName)){
				continue;
			}
			mapping.put(columnName,getGetter(field.getName()));
		}
		return mapping;
	}
	
	/**
	 * 获取数据库表字段对应实体bean属性类型
	 * @param t
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Hashtable<String,Class> getColumnFieldTypeMapping(Object t)throws Exception{
		Hashtable<String,Class> mapping = new Hashtable<String,Class>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			if(StringUtils.isEmpty(columnName)){
				continue;
			}
			mapping.put(columnName,field.getType());
		}
		return mapping;
	}
	
	/**
	 * 获取实体对应数据库表明
	 * @param t
	 * @return
	 */
	public static String getTableName(Object t){
		Annotation[] classAnnotations = t.getClass().getAnnotations();
		String tableName = "";
		//若不指定任何类注解则 使用当前类名称作为数据库表名的进行映射
		if(classAnnotations == null || classAnnotations.length <= 0){
			tableName = t.getClass().getSimpleName();
		}
		//若存在注解则判断注解中的表名注解，
		else{
			for(int i=0;i<classAnnotations.length;i++){
				Annotation annto = classAnnotations[i];
				String anntoName = annto.annotationType().getName();
				if(anntoName.equals("javax.persistence.Table")){
					tableName = ((javax.persistence.Table)annto).name();
					break;
				}
			}
			//若注解中未标明表名，则取类名作为映射
			if(classAnnotations == null || classAnnotations.length <= 0){
				tableName = t.getClass().getSimpleName();
			}
		}
		return tableName;
	}
	/**
	 * 获取 字段getter方法
	 * @param fieldName
	 * @return
	 */
	public static String getGetter(String fieldName){
		String firstChar = fieldName.substring(0,1).toUpperCase();
		String getter = "get"+firstChar+fieldName.substring(1,fieldName.length());
		return getter;
	}
	
	/**
	 * 获取 字段setter方法
	 * @param fieldName
	 * @return
	 */
	public static String getSetter(String fieldName){
		String firstChar = fieldName.substring(0,1).toUpperCase();
		String setter = "set"+firstChar+fieldName.substring(1,fieldName.length());
		return setter;
	}
	
	/**
	 * 根据字段获取字段值
	 * @param t
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static Object getFieldValue(Object t,Field field)throws Exception{
		String fieldName = field.getName();
		String getter = getGetter(fieldName);
		Method getMothed = t.getClass().getDeclaredMethod(getter);
		Object fieldValue = getMothed.invoke(t);
		return fieldValue;
	}
	
	/**
	 * 根据field获取field上的column注解，并返回column名称,实体如要映射数据库必须添加javax.persistence.Column 类型注解、
	 * 并设置 name属性值为数据库字段
	 * @param field
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static String getDecalaredColumnName(Field field) throws Exception{
		String columnName="";
		//判断注解，映射数据字段
		Annotation[] annotations = field.getDeclaredAnnotations();
		//若字段添加 @Transient 注解则忽略改字段
		if(isTransient(field)){
			return null;
		}
		//若字段未加任何注解则，取当前域名称作为数据库字段名
		else if(annotations == null || annotations.length <= 0){
			columnName = field.getName();
		}
		//若字段添加了column 注解 则取 column 注解作为数据库字段映射
		else{
			for(int i=0;i<annotations.length;i++){
				Annotation ata = annotations[i];
				if(ata.annotationType().getName().equals("javax.persistence.Column")){
					javax.persistence.Column column = (javax.persistence.Column)ata;
					columnName = column.name();
				}
			}
			//若字段注解中不存在字段映射的相关注解则取字段名称为数据库映射字段名称
			if(annotations == null || annotations.length <= 0){
				columnName = field.getName();
			}
		}
		return columnName;
	}
	
	/**
	 * 判断当前字段是否注解Transiant
	 * @param field
	 * @return
	 */
	public static Boolean isTransient(Field field){
		Boolean isTransient = false;
		Annotation[] annotations = field.getDeclaredAnnotations();
		for(int i=0;i<annotations.length;i++){
			Annotation ata = annotations[i];
			if(ata.annotationType().getName().equals("javax.persistence.Transient")){
				isTransient = true;
				break;
			}
		}
		return isTransient;
	}
	
	/**
	 * 判断字段是否为主键
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static Boolean isIdentify(Field field)throws Exception{
		Boolean isId = false;
		Annotation[] annotations = field.getDeclaredAnnotations();
		for(int i=0;i<annotations.length;i++){
			Annotation ata = annotations[i];
			if(ata.annotationType().getName().equals("javax.persistence.Id")){
				isId = true;
				break;
			}
		}
		return isId;
	}
	
	
	/**
	 * 获取where查询条件
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static String getWhereConditions(Object t)throws Exception{
		Hashtable<String,String> conditions = new Hashtable<String,String>();
		conditions = getColumnVSValue(t,false,true);
		if(conditions.isEmpty()){
			return "";
		}
		Set<String> keys = conditions.keySet();
		Iterator<String> keyIter = keys.iterator();
		StringBuilder select = new StringBuilder(" where ");
		int index = 0;
		while(keyIter.hasNext()){
			String key = keyIter.next();
			String value = " '"+conditions.get(key)+"' ";
			if(index == 0){
				select.append(key+" = "+value);
			}
			else{
				select.append(" and "+key+" = "+value);
			}
			
			index++;
		}
		return select.toString();
	}
	
	/**
	 * 获取更新设置值
	 * @param t
	 * @return
	 * @throws Exception 若更新对象为空则抛出异常，不更新id字段
	 */
	public static String getUpdateModifier(Object t)throws Exception{
		Hashtable<String,String> modifierTable = new Hashtable<String,String>();
		modifierTable = getColumnVSValue(t, false, false);
		if(modifierTable.isEmpty()){
			throw new Exception("no newer data for update,please check the update conditions");
		}
		Iterator<String> keyIter = modifierTable.keySet().iterator();
		StringBuilder modifier = new StringBuilder(" set ");
		int index = 0;
		while(keyIter.hasNext()){
			String key = keyIter.next();
			String value = " '"+modifierTable.get(key)+"' ";
			if(index == 0){
				modifier.append(key+" = "+value);
			}
			else{
				modifier.append(" , "+key+" = "+value);
			}
			index++;
		}
		return modifier.toString();
	}
	
	/**
	 * 查询结果集 转化为对象列表
	 * @param list
	 * @param t 实例
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static List getObjectList(List<Map<String,Object>> list,Object t)throws Exception{
		List objectList = new ArrayList();
		for(Map<String,Object> map:list){
			objectList.add(getObject(map,t));
		}
		return objectList;
	}
	

	/**
	 * 查询结果集 转化为对象列表
	 * @param list
	 * @param clazz 类定义
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List getObjectList(List<Map<String,Object>> list,Class clazz)throws Exception{
		List objectList = new ArrayList();
		for(Map<String,Object> map:list){
			objectList.add(getObject(map,clazz));
		}
		return objectList;
	}
	
	/**
	 * 将map 中对应的数据库查询数据转化为指定对象并返回
	 * @param map
	 * @param dest
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static Object getObject(Map<String,Object> map,Object dest)throws Exception{
		Object result = null;
		try {
			result = dest.getClass().newInstance();
			Hashtable<String,String> columnSetterMap = getColumnSetterMapping(result);
			Hashtable<String,Class>  columnFieldTypeMap = getColumnFieldTypeMapping(result);
			Set<String> keySet = map.keySet();
			Iterator<String> keyI = keySet.iterator();
			while(keyI.hasNext()){
				String column  = keyI.next();
				String setter = columnSetterMap.get(column);
				Method setMethod = dest.getClass().getDeclaredMethod(setter,columnFieldTypeMap.get(column));
				setMethod.invoke(result, map.get(column));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 将map 中对应的数据库查询数据转化为指定对象并返回
	 * @param map
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static Object getObject(Map<String,Object> map,Class clazz)throws Exception{
		Object result = null;
		try {
			result = clazz.newInstance();
			Hashtable<String,String> columnSetterMap = getColumnSetterMapping(result);
			Hashtable<String,Class>  columnFieldTypeMap = getColumnFieldTypeMapping(result);
			Set<String> keySet = map.keySet();
			Iterator<String> keyI = keySet.iterator();
			while(keyI.hasNext()){
				String column  = keyI.next();
				String setter = columnSetterMap.get(column);
				Method setMethod = clazz.getDeclaredMethod(setter,columnFieldTypeMap.get(column));
				setMethod.invoke(result, map.get(column));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
