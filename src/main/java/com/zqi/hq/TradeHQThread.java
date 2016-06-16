package com.zqi.hq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;
import com.zqi.hq.hqfunc.HQFuncManager;
import com.zqi.primaryData.fileDataBase.TradeFileDataBase;
import com.zqi.unit.DateUtil;

public class TradeHQThread implements Runnable{

	Map<String, Object> context;
	Map<String, Map<String, Object>> lastHqmap;
	Map<String, Map<String, Object>> lastRHismap;
	List<Map<String, Object>> unusualList = new ArrayList<Map<String,Object>>();
	ZqiDao zqiDao;
	
	public TradeHQThread(Map<String, Object> context){
		this.context = context;
		zqiDao = (ZqiDao)context.get("dao");
	}
	
	@Override
	public void run() {
		List<Map<String, Object>> gpList = (List<Map<String, Object>>)context.get("gpList");
		lastHqmap = (Map<String, Map<String, Object>>)context.get("lastHqmap");
		lastRHismap = (Map<String, Map<String, Object>>)context.get("lastRHismap");
		TradeFileDataBase tradeFileDataBase = new TradeFileDataBase("2016-06-08");
		for(Map<String, Object> gp : gpList){
			String code = gp.get("code").toString();
			String name = gp.get("name").toString();
			List<Map<String, Object>> tradeList = tradeFileDataBase.readList(code);
			if(tradeList.size()==0){
				Map<String, Object> unusualMap = new HashMap<String, Object>();
				unusualMap.put("datetime", "2016-06-08");
				unusualMap.put("code", code);
				unusualMap.put("name", name);
				unusualMap.put("mtype", "0");
				unusualMap.put("ptype", "0");
				unusualMap.put("message", "停牌");
				unusualList.add(unusualMap);
			}else{
				tradeList.remove(0);
				parse(gp,"2016-06-08",tradeList);
			}
		}
		zqiDao.addList(unusualList, "i_hqRadar");
	}
	
	public void parse(Map<String, Object> gp,String date ,List<Map<String, Object>> tradeList){
		String code = gp.get("code").toString();
		String name = gp.get("name").toString();
		Map<String, Object> rData = lastRHismap.get(code);
		for(Map<String, Object> tradeData: tradeList){
			Map<String, Object> lastHq = lastHqmap.get(code);
			tradeData.put("status", "-1");
			String datetime = tradeData.get("datetime").toString();
			tradeData.put("datetime", date+" "+datetime);
			tradeData.put("code", code);
			tradeData.put("name", name);
			if(rData==null){
				continue;
			}
			Object settlement = rData.get("settlement");
			if(settlement!=null){
				tradeData.put("settlement", settlement);
			}
			
			if(lastHq==null){
				String yesterday = settlement.toString();
				String now = tradeData.get("close").toString();
				if(yesterday!=null&&!yesterday.equals("")&&now!=null&&!now.equals("")){
					BigDecimal n = new BigDecimal(now.toString());
					tradeData.put("now", n);
					BigDecimal y = new BigDecimal(yesterday);
					tradeData.put("settlement", y);
					BigDecimal limitPrice = y.multiply(new BigDecimal(0.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal limitUpPrice = y.add(limitPrice);
					BigDecimal limitDownPrice = y.subtract(limitPrice);
					tradeData.put("limitUpPrice",limitUpPrice); 
					tradeData.put("limitDownPrice",limitDownPrice); 
					lastHqmap.put(code,tradeData);
				}
				continue;
			}else{
				HQFuncManager.radar(tradeData, lastHq, unusualList);
				synchonizHqMap(code,tradeData);
			}
			
		}
		System.out.println(name);
	}
	
	synchronized private void synchonizHqMap(String code , Map<String, Object> hqData){
		lastHqmap.put(code, hqData);
	}

}
