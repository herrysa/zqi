package com.zqi.PrimaryData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HisContext {

	String dateFrom;
	String dateTo;
	Map<String, Integer> recordMap;
	Map<String, Map<String, String>> log;
	List<String> daytableList;
	
	public Map<String, Integer> getRecordMap() {
		if(recordMap==null){
			recordMap = new HashMap<String, Integer>();
		}
		return recordMap;
	}
	public void setRecordMap(Map<String, Integer> recordMap) {
		this.recordMap = recordMap;
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
