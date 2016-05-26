package com.zqi.PrimaryData;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.zqi.dataFinder.wy163.Finder163RHis;
import com.zqi.unit.FileUtil;
import com.zqi.unit.SpringContextHelper;

public class HisDataAddThread implements Runnable{

	DataSource dataSource = (DataSource)SpringContextHelper.getBean("dataSource");
	SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
	List<Map<String, Object>> gpList;
	String daytable;
	String dateFrom;
	String dateTo;
	HisContext hisContext;
	String[] colArr;
	
	public HisDataAddThread(List<Map<String, Object>> gpList,String daytable,HisContext hisContext){
		this.gpList = gpList;
		this.daytable = daytable;
		this.dateFrom = hisContext.getDateFrom();
		this.dateTo = hisContext.getDateTo();
		this.hisContext = hisContext;
		this.colArr = hisContext.getColArr();
	}
	
	@Override
	public void run() {
		Finder163RHis finder163rHis = new Finder163RHis(hisContext);
		int count = 0;
		simpleJdbcInsert.withTableName(daytable);
		StringBuffer insertbBuffer = new StringBuffer();
		for(Map<String, Object> gp : gpList){
			List<Map<String,Object>> dataListTemp = finder163rHis.findRHis(gp, dateFrom, dateTo);
			count += dataListTemp.size();
			for(Map<String, Object> data : dataListTemp){
				//simpleJdbcInsert.execute(data);
				String dataLine = getInsert(data,daytable);
				insertbBuffer.append(dataLine);
			}
		}
		FileUtil.writeFile(insertbBuffer.toString(), "D:/t/"+daytable+".txt");
		hisContext.getRecordMap().put(daytable, count);
	}

	private String getInsert(Map<String,Object> dataMap,String daytable){
		String dataLine = "";
		for(String col : colArr){
			dataLine += dataMap.get(col)+"\t";
		}
		return dataLine+"\n";
	}
}
