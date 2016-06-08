package com.zqi.primaryData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HisContext {

	String year;
	String dateFrom;
	String dateTo;
	Map<String, Map<String, String>> log;
	List<String> daytableList;
	String[] colArr;
	
	public String[] getColArr() {
		return colArr;
	}
	public void setColArr(String[] colArr) {
		this.colArr = colArr;
	}
	public Map<String, Map<String, String>> getLog() {
		if(log==null){
			log = new HashMap<String, Map<String,String>>();
		}
		return log;
	}
	public void setLog(Map<String, Map<String, String>> log) {
		this.log = log;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	public List<String> getDaytableList() {
		return daytableList;
	}
	public void setDaytableList(List<String> daytableList) {
		this.daytableList = daytableList;
	}
	
}
