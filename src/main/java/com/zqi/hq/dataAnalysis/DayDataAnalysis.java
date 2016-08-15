package com.zqi.hq.dataAnalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;

@Component("dayDataAnalysis")
public class DayDataAnalysis {

	private ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}
	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	public void dayAnalysis(){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			dayWaveAnalysis(gp);
		}
	}
	
	public void dayWaveAnalysis(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String name = gp.get("name").toString();
		List<Map<String, Object>> dayDataList = zqiDao.findAll("select * from daytable_all where code='"+code+"' order by period asc");
		Map maxMap = new HashMap<String, BigDecimal>();
		List<Map<String, Object>> waveList = new ArrayList<Map<String,Object>>();
		Map<String, Object> lastDayData = null;
		String periodBegin = null;
		BigDecimal waveBegin = null;
		String waveHighPeriod = null;
		BigDecimal waveHigh = null;
		String waveLowPeriod = null;
		BigDecimal waveLow = null;
		String waveMaxVolPeriod = null;
		BigDecimal waveMaxVol = null;
		String waveMinVolPeriod = null;
		BigDecimal waveMinVol = null;	//
		BigDecimal waveAvgVol = null;	//平均量能
		String waveMaxZfPeriod = null;
		int waveNum = 0;
		
		int direct = 2;
		for(int i=0;i<dayDataList.size();i++){
			Map<String, Object> dayData = dayDataList.get(i);
			BigDecimal close = (BigDecimal)dayData.get("close");
			BigDecimal high = (BigDecimal)dayData.get("high");
			BigDecimal low = (BigDecimal)dayData.get("low");
			BigDecimal amount = (BigDecimal)dayData.get("amount");
			String period = dayData.get("period").toString();
			if(lastDayData==null){
				lastDayData = dayData;
				periodBegin = period;
				waveBegin = close;
				waveHigh = high;
				waveLow = low;
				waveHigh = amount;
			}else{
				BigDecimal lastClose = (BigDecimal)lastDayData.get("close");
				if(lastClose.compareTo(new BigDecimal(0))==0){
					continue;
				}
				String lastPeriod = lastDayData.get("period").toString();
				int directTemp = close.compareTo(lastClose);
 				if(directTemp>0){
					if(direct==-1||direct==0){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							/*if(waveNum<3){
								waveMap.put("state", "9");
								lastDayData = dayData;
							}else{
								waveMap.put("state", "0");
							}*/
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.divide(lastClose).divide(lastClose,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = 1;
							i += 2;
							waveNum = 1;
							lastDayData = dayData;
						}
					}else if(direct==1){
						waveNum ++;
						if(high.compareTo(waveHigh)>0){
							waveHigh = high;
						}
						if(low.compareTo(waveLow)<0){
							waveLow = low;
						}
						lastDayData = dayData;
						
					}else{
						waveNum ++;
						direct = 1;
					}
				}else if(directTemp==0){
					if(direct==-1||direct==1){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.divide(lastClose).divide(lastClose,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							/*if(waveNum<3){
								waveMap.put("state", "9");
								lastDayData = dayData;
							}else{
								waveMap.put("state", "0");
							}*/
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = 0;
							i += 2;
							waveNum = 1;
							lastDayData = dayData;
						}
					}else if(direct==0){
						waveNum ++;
						lastDayData = dayData;
					}else{
						waveNum ++;
						direct = 0;
					}
				}else if(directTemp<0){
					if(direct==1||direct==0){
						boolean isEnd= isEnd(direct,i,lastClose,dayDataList);
						if(isEnd){
							Map<String, Object> waveMap = new HashMap<String, Object>();
							waveMap.put("code", code);
							waveMap.put("name", name);
							waveMap.put("periodBegin", periodBegin);
							waveMap.put("periodEnd", lastPeriod);
							waveMap.put("waveBegin", waveBegin);
							waveMap.put("waveEnd", lastClose);
							waveMap.put("direct", direct);
							BigDecimal zf = lastClose.divide(lastClose).divide(lastClose,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							waveMap.put("waveNum", waveNum);
							waveMap.put("zf", zf);
							/*if(waveNum<3){
								waveMap.put("state", "9");
								lastDayData = dayData;
							}else{
								waveMap.put("state", "0");
							}*/
							waveList.add(waveMap);
							periodBegin = lastPeriod;
							waveBegin = lastClose;
							direct = -1;
							i += 2;
							waveNum = 1;
							lastDayData = dayData;
						}
					}else if(direct==-1){
						waveNum ++;
						lastDayData = dayData;
					}else{
						waveNum ++;
						direct = -1;
					}
				}
			}
		}
		zqiDao.addList(waveList, "i_gpwave");
		System.out.println();
	}
	
	
		
	private boolean isEnd(int direct,int i,BigDecimal lastClose,List<Map<String, Object>> dayDataList){
		boolean waveEnd = false;
		int transCount = 0;
		
		//Map<String, Object> dayData1 = dayDataList.get(i+1);
		//Map<String, Object> dayData2 = dayDataList.get(i+2);
		if(i+3>dayDataList.size()-1){
			return waveEnd;
		}
		Map<String, Object> dayData3 = dayDataList.get(i+3);
		
		//BigDecimal close1 = (BigDecimal)dayData1.get("close");
		//BigDecimal close2 = (BigDecimal)dayData2.get("close");
		BigDecimal close3 = (BigDecimal)dayData3.get("close");
		int directTemp = close3.compareTo(lastClose);
		if(direct==1){
			if(directTemp==-1||directTemp==0){
				waveEnd = true;
			}
		}else if(direct==-1){
			if(directTemp==1||directTemp==0){
				waveEnd = true;
			}
		}else{
			if(directTemp==-1||directTemp==1){
				waveEnd = true;
			}
		}
		
		return waveEnd;
	}
}
