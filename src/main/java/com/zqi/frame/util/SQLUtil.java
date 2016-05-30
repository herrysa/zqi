package com.zqi.frame.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class SQLUtil {

	public String[] columns;
	public String tableName;
	public String columnsStr;
	public String pkName;
	public String seq;
	
	public SQLUtil(String[] columns, String tableName, String pkName,String seq) {
		this.columns = columns;
		this.tableName = tableName;
		this.pkName = pkName;
		this.seq = seq;
		this.columnsStr = StringUtils.join(columns, ",");
	}
	
	public String sql_inseart(Map<String, String> inseartMap){
		StringBuilder sql_build = new StringBuilder();
		List<String> values = new ArrayList<String>();
		for(String column : columns){
			String value = inseartMap.get(column);
			if(value!=null){
				if(value.contains("'")){
					value = value.replace("'", "''");
				}
				value = "'"+value+"'";
			}else{
				value = "null";
			}
			values.add(value);
		}
		sql_build.append("INSERT INTO ").append(tableName).append("(").append(columnsStr).append(")values(")
		.append(StringUtils.join(values, ",")).append(")");
		String sql = sql_build.toString();
		return sql;
	}
	
	public String sql_update(Map<String, String> updateMap,String id){
		StringBuilder sql_build = new StringBuilder();
		List<String> values = new ArrayList<String>();
		for(String column : columns){
			String value = updateMap.get(column);
			if(value!=null){
				if(value.contains("'")){
					value = value.replace("'", "''");
				}
				value = column+"='"+value+"'";
			}else{
				value = column+"=null";
			}
			values.add(value);
		}
		sql_build.append("UPDATE ").append(tableName).append(" SET ")
        .append(StringUtils.join(values, ",")).append(" WHERE ")
        .append(pkName).append(" = ").append("'"+id+"'");
		String sql = sql_build.toString();
		return sql;
	}
	
	public String sql_delete(String id){
		StringBuilder sql_build = new StringBuilder();
		sql_build.append("DELETE FROM ").append(tableName).append(" WHERE ")
		.append(pkName).append(" = ").append("'"+id+"'");
		String sql = sql_build.toString();
		return sql;
	}
	
	public String sql_get(String id){
		StringBuilder sql_build = new StringBuilder();
		sql_build.append("SELECT * FROM ").append(tableName).append(" WHERE ")
		.append(pkName).append(" = ").append("'"+id+"'");
		String sql = sql_build.toString();
		return sql;
	}
	
	public String getId(HttpServletRequest request){
		String id = request.getParameter(pkName);
		return id;
	}
	
	public String sql_get(HttpServletRequest request){
		String id = getId(request);
		StringBuilder sql_build = new StringBuilder();
		sql_build.append("SELECT * FROM ").append(tableName).append(" WHERE ")
		.append(pkName).append(" = ").append("'"+id+"'");
		String sql = sql_build.toString();
		return sql;
	}
	
	public Map<String, String> getSaveMap(HttpServletRequest request){
		Map<String, String> saveMap = new HashMap<String, String>();
		for(String column : columns){
			String value = request.getParameter(column);
			saveMap.put(column, value);
		}
		return saveMap;
	}
}
