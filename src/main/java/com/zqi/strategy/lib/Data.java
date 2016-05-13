package com.zqi.strategy.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.zqi.frame.dao.impl.ZqiDao;

public class Data extends BaseLib{

	
	public Data(ZqiDao zqiDao) {
		super(zqiDao);
	}

	public String getGPData(String code ,String col,String start,String end){
		if(!code.startsWith("'")){
			code = "'"+code+"'";
		}
		List<String> extendCol = new ArrayList<String>();
		String dbCol = initCol(col,extendCol);
		List<Map<String,String>> codeList = zqiDao.findAll("select "+dbCol+" from daytable_all where code="+code+" and period between "+start+" and "+end+" order by period asc");
		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String,String>>();
		for(Map<String,String> data : codeList){
			String period = data.get("period");
			dataMap.put(period, data);
		}
		JSONObject dataJsonObject = JSONObject.fromObject(dataMap);
		String dataStr = dataJsonObject.toString();
		return dataStr;
	}
	
	private String initCol(String col,List<String> extendCol){
		String dbCol = "";
		col = col.replaceAll("'", "");
		String[] colArr = col.split(",");
		for(String c:colArr){
			if(c.startsWith("@")){
				extendCol.add(c);
			}else{
				dbCol += c+",";
			}
		}
		if(dbCol.contains("period")){
			dbCol = dbCol.substring(0, dbCol.length()-1);
		}else{
			dbCol += "period";
		}
		return dbCol;
	}
	
	private void getExendColData(List<Map<String,String>> codeList,List<String> extendCol){
		for(String ecol : extendCol){
			ecol = ecol.substring(1);
			String[] ecolArr = ecol.split("\\.");
			String method = ecolArr[0];
			String param = ecolArr[1];
			if("avg".equals(method)){
		        
			}
		}
	}
	

}
