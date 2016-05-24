package com.zqi.PrimaryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.zqi.dataFinder.Finder163RHis;
import com.zqi.unit.SpringContextHelper;

public class HisDataAddThread implements Runnable{

	DataSource dataSource = (DataSource)SpringContextHelper.getBean("dataSource");
	SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
	List<Map<String, Object>> gpList;
	String daytable;
	String dateFrom;
	String dateTo;
	
	public HisDataAddThread(List<Map<String, Object>> gpList,String daytable,HisContext hisContext){
		this.gpList = gpList;
		this.daytable = daytable;
		this.dateFrom = hisContext.getDateFrom();
		this.dateTo = hisContext.getDateTo();
	}
	
	@Override
	public void run() {
		Finder163RHis finder163rHis = new Finder163RHis();
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> gp : gpList){
			dataList.addAll(finder163rHis.findRHis(gp, dateFrom, dateTo));
		}
		simpleJdbcInsert.withTableName(daytable);
		for(Map<String, Object> data : dataList){
			simpleJdbcInsert.execute(data);
		}
	}

}
