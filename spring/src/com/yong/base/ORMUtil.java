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
 * �ù��������ڴ���ʵ���ൽ���ݿ���ӳ���ϵ�򵥴���
 * ʵ�������� javax.persistence.Table ע�� ��ʾ��ʵ��Ϊ�־û�ʵ��bean  name ���Ա���ָ������ʾ��Ӧ���ݿ����
 * ��Ҫӳ���ֶα���� javax.persistence.Column ע�� ��ʾӳ�����ݱ��еĶ�Ӧ�ֶ� name ���Ա���ָ������ʾ��Ӧ���е��ֶ�����
 * ������Ҫ�� javax.persistence.Id ע�� ���� 
 * ���ݿ���� mysql ����SQL ��װ
 * @author ZhixiangYong
 * @version 1.0
 */
@SuppressWarnings("all")
public class ORMUtil{
	
	public static final String COLUMNS_STRING = "column";
	public static final String VALUES_STRING = "value";
	
	/**
	 * ���ݳ־û����󴴽��ö���� ���� sql ���
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
	 * ���ݸ��¶��󼰸���������ȡ�������
	 * @param newer
	 * @param params ������Ϊ�������ò�ѯ����Ϊ�� ����ȫ����²���
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
	 * ��ȡ��ѯ���
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
	 * ��ȡ��ҳ��ѯsql
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
	 * ��װ��ҳ��ѯsql
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
	 * ��ȡͳ��sql
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
	 * ���ݲ�ѯ����ȡͳ�����
	 * @param sql
	 * @return
	 */
	public static String getCountSqlByQuery(String sql){
		sql = " select count(1) "+sql.substring(sql.toLowerCase().indexOf("from"));
		return sql;
	}
	
	/**
	 * ��ȡ���ݿ��ֶ����ʼ�ʵ���ֶε�getter������,��ȡ��ѯ����
	 * ʵ���������ֶζ������ͱ���Ϊ�������ͣ�����ʹ�� int float �ȵ�ֵ���ͣ�����ʹ�ð�װ���� Integer Float �ȣ�
	 * @param includeNullColumn true:��ȡ�����ֶΰ���ֵΪ�յĹ�ϵ��false/null��ֻ��ȡֵ��Ϊ�յ��ֶ�
	 * @param includeIdentify true:��ȡ�ֶΰ���������false/null:����������
	 * @return
	 */
	public static Hashtable<String,String> getColumnVSValue(Object t,Boolean includeNullColumn,Boolean includeIdentify)throws Exception{
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		Field[] fields = t.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String columnName = getDecalaredColumnName(field);
			Object columnValue = getFieldValue(t,field);
			//���ֶ�ֵΪ���򲻼���sql
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
	 * ��ȡ�ֶκ�ֵ���ַ�������
	 * @param t
	 * @param includeNullColumn �Ƿ���� ֵΪ�յ��ֶ�
	 * @param includeIdentify  �Ƿ��������
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
	 * ��ȡ�ֶ����ƺ�����֮���ӳ���ϵ��key:columnName  ; value:fieldName;
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
			//���ֶ�ֵΪ���򲻼���
			if(StringUtils.isEmpty(columnName)){
				continue;
			}
			mapping.put(field.getName(),columnName);
		}
		return mapping;
	}
	
	/**
	 * ��ȡ ���ݱ��ֶκ�ʵ��bean setter ӳ���ϵ
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
	 * ��ȡ���ݱ��ֶκ�ʵ��bean getter ӳ���ϵ
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
	 * ��ȡ���ݿ���ֶζ�Ӧʵ��bean��������
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
	 * ��ȡʵ���Ӧ���ݿ����
	 * @param t
	 * @return
	 */
	public static String getTableName(Object t){
		Annotation[] classAnnotations = t.getClass().getAnnotations();
		String tableName = "";
		//����ָ���κ���ע���� ʹ�õ�ǰ��������Ϊ���ݿ�����Ľ���ӳ��
		if(classAnnotations == null || classAnnotations.length <= 0){
			tableName = t.getClass().getSimpleName();
		}
		//������ע�����ж�ע���еı���ע�⣬
		else{
			for(int i=0;i<classAnnotations.length;i++){
				Annotation annto = classAnnotations[i];
				String anntoName = annto.annotationType().getName();
				if(anntoName.equals("javax.persistence.Table")){
					tableName = ((javax.persistence.Table)annto).name();
					break;
				}
			}
			//��ע����δ������������ȡ������Ϊӳ��
			if(classAnnotations == null || classAnnotations.length <= 0){
				tableName = t.getClass().getSimpleName();
			}
		}
		return tableName;
	}
	/**
	 * ��ȡ �ֶ�getter����
	 * @param fieldName
	 * @return
	 */
	public static String getGetter(String fieldName){
		String firstChar = fieldName.substring(0,1).toUpperCase();
		String getter = "get"+firstChar+fieldName.substring(1,fieldName.length());
		return getter;
	}
	
	/**
	 * ��ȡ �ֶ�setter����
	 * @param fieldName
	 * @return
	 */
	public static String getSetter(String fieldName){
		String firstChar = fieldName.substring(0,1).toUpperCase();
		String setter = "set"+firstChar+fieldName.substring(1,fieldName.length());
		return setter;
	}
	
	/**
	 * �����ֶλ�ȡ�ֶ�ֵ
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
	 * ����field��ȡfield�ϵ�columnע�⣬������column����,ʵ����Ҫӳ�����ݿ�������javax.persistence.Column ����ע�⡢
	 * ������ name����ֵΪ���ݿ��ֶ�
	 * @param field
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static String getDecalaredColumnName(Field field) throws Exception{
		String columnName="";
		//�ж�ע�⣬ӳ�������ֶ�
		Annotation[] annotations = field.getDeclaredAnnotations();
		//���ֶ���� @Transient ע������Ը��ֶ�
		if(isTransient(field)){
			return null;
		}
		//���ֶ�δ���κ�ע����ȡ��ǰ��������Ϊ���ݿ��ֶ���
		else if(annotations == null || annotations.length <= 0){
			columnName = field.getName();
		}
		//���ֶ������column ע�� ��ȡ column ע����Ϊ���ݿ��ֶ�ӳ��
		else{
			for(int i=0;i<annotations.length;i++){
				Annotation ata = annotations[i];
				if(ata.annotationType().getName().equals("javax.persistence.Column")){
					javax.persistence.Column column = (javax.persistence.Column)ata;
					columnName = column.name();
				}
			}
			//���ֶ�ע���в������ֶ�ӳ������ע����ȡ�ֶ�����Ϊ���ݿ�ӳ���ֶ�����
			if(annotations == null || annotations.length <= 0){
				columnName = field.getName();
			}
		}
		return columnName;
	}
	
	/**
	 * �жϵ�ǰ�ֶ��Ƿ�ע��Transiant
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
	 * �ж��ֶ��Ƿ�Ϊ����
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
	 * ��ȡwhere��ѯ����
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
	 * ��ȡ��������ֵ
	 * @param t
	 * @return
	 * @throws Exception �����¶���Ϊ�����׳��쳣��������id�ֶ�
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
	 * ��ѯ����� ת��Ϊ�����б�
	 * @param list
	 * @param t ʵ��
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
	 * ��ѯ����� ת��Ϊ�����б�
	 * @param list
	 * @param clazz �ඨ��
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
	 * ��map �ж�Ӧ�����ݿ��ѯ����ת��Ϊָ�����󲢷���
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
	 * ��map �ж�Ӧ�����ݿ��ѯ����ת��Ϊָ�����󲢷���
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
