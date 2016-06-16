package com.zqi.hq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;
import com.zqi.hq.hqfunc.HQFuncManager;
import com.zqi.unit.DateUtil;

public class InTimeHQThread implements Runnable{

	Map<String, Object> context;
	Map<String, Map<String, Object>> lastHqmap;
	ZqiDao zqiDao;
	
	public InTimeHQThread(Map<String, Object> context){
		this.context = context;
		zqiDao = (ZqiDao)context.get("dao");
	}
	
	@Override
	public void run() {
		String codeStr = context.get("codeStr").toString();
		String url = "http://hq.sinajs.cn/";
		String result = Tools.getByHttpUrl(url+"list="+codeStr);
		String datetime = DateUtil.getDateTimeNow();
		lastHqmap = (Map<String, Map<String, Object>>)context.get("lastHqmap");
		List<Map<String, Object>> unusualList = new ArrayList<Map<String,Object>>();
		String[] resultArr = result.split(";");
		for(String dataRow : resultArr){
			String[] rowArr = dataRow.split("=");
			String code = rowArr[0].substring(rowArr[0].lastIndexOf("_")+1);
			String[] dataArr = rowArr[1].split(",");
			if(dataArr.length>1){
				Map<String, Object> lastHq = lastHqmap.get(code);
				String name = dataArr[0].substring(1);
				Map<String, Object> hqMap = new HashMap<String, Object>();
				String status = "-1";
				if(lastHq!=null){
					status = lastHq.get("status").toString();
				}
				if("0".equals(status)){
					continue;
				}
				String open = dataArr[1];
				if("0.000".equals(open)){
					hqMap.put("status", "0");
					Map<String, Object> unusualMap = new HashMap<String, Object>();
					unusualMap.put("datetime", datetime);
					unusualMap.put("code", code);
					unusualMap.put("name", name);
					unusualMap.put("mtype", "0");
					unusualMap.put("ptype", "0");
					unusualMap.put("message", "停牌");
					//unusualList.add(unusualMap);
				}else{
					hqMap.put("status", "-1");
				}

				hqMap.put("datetime", datetime);
				hqMap.put("code", code);
				hqMap.put("name", name);
				hqMap.put("open", dataArr[1]);
				hqMap.put("settlement", dataArr[2]);
				hqMap.put("close", dataArr[3]);
				hqMap.put("high", dataArr[4]);
				hqMap.put("low", dataArr[5]);
				hqMap.put("volume", dataArr[8]);
				hqMap.put("turnover", dataArr[9]);
				
				hqMap.put("b1",dataArr[11]); 
				hqMap.put("bl1",dataArr[10]); 
				hqMap.put("m1",dataArr[21]); 
				hqMap.put("ml1",dataArr[20]); 
				hqMap.put("b2",dataArr[13]); 
				hqMap.put("bl2",dataArr[12]); 
				hqMap.put("m2",dataArr[23]); 
				hqMap.put("ml2",dataArr[22]); 
				hqMap.put("b3",dataArr[15]); 
				hqMap.put("bl3",dataArr[14]); 
				hqMap.put("m3",dataArr[25]); 
				hqMap.put("ml3",dataArr[24]); 
				hqMap.put("b4",dataArr[17]); 
				hqMap.put("bl4",dataArr[16]); 
				hqMap.put("m4",dataArr[27]); 
				hqMap.put("ml4",dataArr[26]); 
				hqMap.put("b5",dataArr[19]); 
				hqMap.put("bl5",dataArr[18]); 
				hqMap.put("m5",dataArr[29]); 
				hqMap.put("ml5",dataArr[28]); 
				//dayHqData.add(hqMap);
				if(lastHq==null){
					String yesterday = dataArr[2];
					String now = dataArr[3];
					if(yesterday!=null&&!yesterday.equals("")&&now!=null&&!now.equals("")){
						BigDecimal n = new BigDecimal(now.toString());
						hqMap.put("now", n);
						BigDecimal y = new BigDecimal(yesterday);
						hqMap.put("settlement", y);
						BigDecimal limitPrice = y.multiply(new BigDecimal(0.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal limitUpPrice = y.add(limitPrice);
						BigDecimal limitDownPrice = y.subtract(limitPrice);
						hqMap.put("limitUpPrice",limitUpPrice); 
						hqMap.put("limitDownPrice",limitDownPrice); 
						lastHqmap.put(code,hqMap);
					}
					continue;
				}else{
					HQFuncManager.radar(hqMap, lastHq, unusualList);
					synchonizHqMap(code,hqMap);
				}
			}		
			
		}
		zqiDao.addList(unusualList, "i_hqRadar");
	}
	
	synchronized private void synchonizHqMap(String code , Map<String, Object> hqData){
		lastHqmap.put(code, hqData);
	}

}
