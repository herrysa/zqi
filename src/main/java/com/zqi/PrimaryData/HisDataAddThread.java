package com.zqi.PrimaryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.zqi.dataFinder.wy163.Finder163RHis;
import com.zqi.unit.SpringContextHelper;

public class HisDataAddThread implements Runnable{

	DataSource dataSource = (DataSource)SpringContextHelper.getBean("dataSource");
	SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
	List<Map<String, Object>> gpList;
	String daytable;
	String dateFrom;
	String dateTo;
	HisContext hisContext;
	
	public HisDataAddThread(List<Map<String, Object>> gpList,String daytable,HisContext hisContext){
		this.gpList = gpList;
		this.daytable = daytable;
		this.dateFrom = hisContext.getDateFrom();
		this.dateTo = hisContext.getDateTo();
		this.hisContext = hisContext;
	}
	
	@Override
	public void run() {
		Finder163RHis finder163rHis = new Finder163RHis(hisContext);
		int count = 0;
		simpleJdbcInsert.withTableName(daytable);
		for(Map<String, Object> gp : gpList){
			List<Map<String,Object>> dataListTemp = finder163rHis.findRHis(gp, dateFrom, dateTo);
			count += dataListTemp.size();
			for(Map<String, Object> data : dataListTemp){
				simpleJdbcInsert.execute(data);
			}
		}
		hisContext.getRecordMap().put(daytable, count);
		hisContext.getDaytableList().remove(daytable);
	}

}
