package com.zqi.frame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
				value = column+"='"+value+"'";
			}else{
				value = column+"=null";
			}
			values.add(value);
		}
		sql_build.append("UPDATE ").append(tableName).append(" SET ")
        .append(StringUtils.join(values, ",")).append(" WHERE ")
        .append(pkName).append(" = ").append(id);
		String sql = sql_build.toString();
		return sql;
	}
}
