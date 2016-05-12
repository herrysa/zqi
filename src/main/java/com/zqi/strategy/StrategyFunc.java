package com.zqi.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.zqi.frame.dao.impl.ZqiDao;

public class StrategyFunc {

	private ZqiDao zqiDao;

	public StrategyFunc(ZqiDao zqiDao){
		this.zqiDao = zqiDao;
	}
	
	public String func_getGPData(String code ,String col,String start,String end){
		if(code.startsWith("'")){
			code = code.replace("'", "");
		}
		col = col.replaceAll("'", "");
		if(!col.contains("period")){
			col = "period,"+col;
		}
		List<Map<String,String>> codeList = zqiDao.findAll("select "+col+" from daytable_all where code='"+code+"' and period between "+start+" and "+end+" order by period asc");
		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String,String>>();
		for(Map<String,String> data : codeList){
			String period = data.get("period");
			dataMap.put(period, data);
		}
		JSONObject dataJsonObject = JSONObject.fromObject(dataMap);
		String dataStr = dataJsonObject.toString();
		return dataStr;
	}
	
	
}
