package com.zqi.hq.hqfunc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.hq.InTimeHQ;
import com.zqi.unit.DateUtil;

public class HQFuncManager {
	
	static int dataType = 0;
	
	public static void radar(Map<String, Object> hqData,Map<String, Object> lastHqData,List<Map<String, Object>> unusualList){
		String code = hqData.get("code").toString();
		Object close = hqData.get("close");
		Object settlementObj = hqData.get("settlement");
		BigDecimal now = new BigDecimal(close.toString());
		BigDecimal settlement = new BigDecimal(settlementObj.toString());
		hqData.put("now", now);
		hqData.put("settlement", settlement);
		Map<String, Object> ruleMap = InTimeHQ.funcRuleMap.get(code);
		if(ruleMap!=null){
			hqData.putAll(ruleMap);
		}
		limitFunc(hqData, lastHqData, unusualList);
		fastUpFunc(hqData, lastHqData, unusualList);
	}
	private static void limitFunc(Map<String, Object> hqData,Map<String, Object> lastHqData,List<Map<String, Object>> unusualList) {
		Object thisRuleObj = hqData.get("limit");
		if(thisRuleObj!=null){
			String thisRule = thisRuleObj.toString();
			if("0".equals(thisRule)){
				return ;
			}
		}
		
		String status = lastHqData.get("status").toString();
		
		BigDecimal limitUpPrice = (BigDecimal)lastHqData.get("limitUpPrice");
		BigDecimal limitDownPrice = (BigDecimal)lastHqData.get("limitDownPrice");
		BigDecimal now = (BigDecimal)hqData.get("now");
		
		hqData.put("limitUpPrice", limitUpPrice);
		hqData.put("limitDownPrice", limitDownPrice);
		
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
		
		hqData.put("status", status);
		
		if(unusualFlag==1){
			Map<String, Object> unusualMap = new HashMap<String, Object>();
			
			unusualMap.put("datetime", hqData.get("datetime"));
			unusualMap.put("code", hqData.get("code"));
			unusualMap.put("name", hqData.get("name"));
			unusualMap.put("close", hqData.get("close"));
			unusualMap.put("volume", hqData.get("volume"));
			unusualMap.put("amount", hqData.get("turnover"));
			unusualMap.put("mtype", status);
			unusualMap.put("ptype", "0");
			unusualMap.put("message", message);
			unusualList.add(unusualMap);
		}
	}
	
	private static void fastUpFunc(Map<String, Object> hqData,Map<String, Object> lastHqData,List<Map<String, Object>> unusualList) {
		Object thisRuleObj = hqData.get("fastUp");
		if(thisRuleObj!=null){
			String thisRule = thisRuleObj.toString();
			if("0".equals(thisRule)){
				return ;
			}
		}
		
		String status = lastHqData.get("status").toString();
		if("10".equals(status)||"-10".equals(status)){
			return ;
		}
		
		BigDecimal nowPrice = (BigDecimal)hqData.get("now");
		BigDecimal lastPrice = (BigDecimal)lastHqData.get("now");
		Object drectObj = lastHqData.get("drect");	//上一次价格是上涨:1;下跌:-1;横盘:0
		Object fuStartObj = lastHqData.get("fuStart");//是否开始计算快速上涨/下跌
		Object fuBasePriceObj = lastHqData.get("fuBasePrice");//上涨/下跌只计算基数
		Object fuBaseTimeObj = lastHqData.get("fuBaseTime");
		String fuBaseTime = "";
		String drect = "",fuStart = "";
		BigDecimal fuBasePrice = null;
		if(drectObj!=null){
			drect = drectObj.toString();
		}else{
			drect = "0";
		}
		
		if(fuStartObj!=null){
			fuStart = fuStartObj.toString();
		}else{
			fuStart = "0";
		}
		
		if(fuBasePriceObj!=null){
			fuBasePrice = (BigDecimal)fuBasePriceObj;
		}
		
		if(fuBaseTimeObj!=null){
			fuBaseTime = fuBaseTimeObj.toString();
		}
		
		int compareRs = nowPrice.compareTo(lastPrice);
		
		if("1".equals(fuStart)){
			BigDecimal upPrice = nowPrice.subtract(fuBasePrice);
			if(upPrice.compareTo(new BigDecimal(0))<=0){
				fuStart = "0";
			}else{
				BigDecimal settlement = (BigDecimal)hqData.get("settlement");
				BigDecimal basePercent = fuBasePrice.subtract(settlement).divide(settlement,10,BigDecimal.ROUND_HALF_DOWN);
				BigDecimal nowPercent = nowPrice.subtract(settlement).divide(settlement,10,BigDecimal.ROUND_HALF_DOWN);
				BigDecimal fuPercent = nowPercent.subtract(basePercent).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				int fu = fuPercent.compareTo(new BigDecimal("1"));
				if(fu>=0){
					status = "fu";
					Map<String, Object> unusualMap = new HashMap<String, Object>();
					
					unusualMap.put("datetime", hqData.get("datetime"));
					unusualMap.put("code", hqData.get("code"));
					unusualMap.put("name", hqData.get("name"));
					unusualMap.put("close", hqData.get("close"));
					unusualMap.put("volume", hqData.get("volume"));
					unusualMap.put("amount", hqData.get("turnover"));
					unusualMap.put("mtype", status);
					unusualMap.put("ptype", "0");
					unusualMap.put("message", "快速上涨-"+fuBaseTime);
					unusualMap.put("info", fuPercent);
					unusualList.add(unusualMap);
					
					fuBaseTime = hqData.get("datetime").toString();
					fuBasePrice = nowPrice;
				}else{
					String datetime = hqData.get("datetime").toString();
					try {
						Date nowDateTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",datetime);
						Date fuFaseDateTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",fuBaseTime);
						Long timeCha = nowDateTime.getTime()-fuFaseDateTime.getTime();
						if(timeCha>=60000){
							fuStart = "0";
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
		}else if("-1".equals(fuStart)){
			BigDecimal upPrice = nowPrice.subtract(fuBasePrice);
			if(upPrice.compareTo(new BigDecimal(0))>=0){
				fuStart = "0";
			}else{
				BigDecimal settlement = (BigDecimal)hqData.get("settlement");
				BigDecimal basePercent = fuBasePrice.subtract(settlement).divide(settlement,10,BigDecimal.ROUND_HALF_DOWN);
				BigDecimal nowPercent = nowPrice.subtract(settlement).divide(settlement,10,BigDecimal.ROUND_HALF_DOWN);
				BigDecimal fuPercent = nowPercent.subtract(basePercent).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				int fu = fuPercent.compareTo(new BigDecimal("-1"));
				if(fu<=0){
					status = "fd";
					Map<String, Object> unusualMap = new HashMap<String, Object>();
					
					unusualMap.put("datetime", hqData.get("datetime"));
					unusualMap.put("code", hqData.get("code"));
					unusualMap.put("name", hqData.get("name"));
					unusualMap.put("close", hqData.get("close"));
					unusualMap.put("volume", hqData.get("volume"));
					unusualMap.put("amount", hqData.get("turnover"));
					unusualMap.put("mtype", status);
					unusualMap.put("ptype", "0");
					unusualMap.put("message", "快速下跌-"+fuBaseTime);
					unusualMap.put("info", fuPercent);
					unusualList.add(unusualMap);
					fuBaseTime = hqData.get("datetime").toString();
					fuBasePrice = nowPrice;
				}else{
					String datetime = hqData.get("datetime").toString();
					try {
						Date nowDateTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",datetime);
						Date fuFaseDateTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",fuBaseTime);
						Long timeCha = nowDateTime.getTime()-fuFaseDateTime.getTime();
						if(timeCha>=60000){
							fuStart = "0";
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}else if("0".equals(fuStart)){
			//同向开始计算
			if(compareRs>0&&"1".equals(drect)){
				fuStart = "1";
				fuBasePrice = nowPrice;
				fuBaseTime = hqData.get("datetime").toString();
			}else if(compareRs<0&&"-1".equals(drect)){
				fuStart = "-1";
				fuBasePrice = nowPrice;
				fuBaseTime = hqData.get("datetime").toString();
			}
		}
		if(compareRs>0){
			drect = "1";
		}else{
			drect = "-1";
		}
		hqData.put("drect", drect);
		hqData.put("fuStart", fuStart);
		hqData.put("fuBasePrice", fuBasePrice);
		hqData.put("fuBaseTime", fuBaseTime);
	}
	
	
}
