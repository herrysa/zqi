package com.zqi.PrimaryData;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.zqi.unit.SpringContextHelper;

public class DataAddThread implements Runnable{

	DataSource dataSource = (DataSource)SpringContextHelper.getBean("dataSource");
	SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
	List<Map<String, Object>> dayTableList;
	String daytable;
	
	public DataAddThread(List<Map<String, Object>> dayTableList,String daytable){
		this.dayTableList = dayTableList;
		this.daytable = daytable;
	}
	
	@Override
	public void run() {
		simpleJdbcInsert.withTableName(daytable);
		for(Map<String, Object> data : dayTableList){
			simpleJdbcInsert.execute(data);
		}
	}

}
