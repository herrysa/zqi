package com.zqi.hq.hqfunc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitFunc {

	public static void parse(Map<String, Object> hqData,Map<String, Object> lastHqData,List<Map<String, Object>> unusualList) {
		Object close = hqData.get("close");
		BigDecimal limitUpPrice = (BigDecimal)lastHqData.get("limitUpPrice");
		BigDecimal limitDownPrice = (BigDecimal)lastHqData.get("limitDownPrice");
		BigDecimal now = new BigDecimal(close.toString());
		
		hqData.put("limitUpPrice", limitUpPrice);
		hqData.put("limitDownPrice", limitDownPrice);
		
		String status = lastHqData.get("status").toString();
		String message = "";
		
		int limitFlag = 0,unusualFlag = 0;
		if(limitUpPrice.compareTo(now)==0){
			limitFlag = 10;
		}else if(limitDownPrice.compareTo(now)==0){
			limitFlag = -10;
		}
		
		if("10".equals(status)){
			if(limitFlag!=10){
				status = "r10";
				message = "打开涨停";
				unusualFlag=1;
			}
		}else if("-10".equals(status)){
			if(limitFlag!=-10){
				status = "r-10";
				message = "打开跌停";
				unusualFlag=1;
			}
		}else{
			if(limitFlag==10){
				status = "10";
				message = "涨停";
				unusualFlag=1;
			}else if(limitFlag==-10){
				status = "-10";
				message = "跌停";
				unusualFlag=1;
			}
		}
		
		if(unusualFlag==1){
			Map<String, Object> unusualMap = new HashMap<String, Object>();
			
			unusualMap.put("datetime", hqData.get("datetime"));
			unusualMap.put("code", hqData.get("code"));
			unusualMap.put("name", hqData.get("name"));
			unusualMap.put("message", message);
			unusualList.add(unusualMap);
		}
	}

}
