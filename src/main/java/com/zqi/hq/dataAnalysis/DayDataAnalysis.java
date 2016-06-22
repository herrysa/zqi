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
	public void dayWaveAnalysis(){
		List<Map<String, Object>> dayDataList = zqiDao.findAll("select * from daytable_all where code='002432' order by period asc");
		Map maxMap = new HashMap<String, BigDecimal>();
		List<Map<String, Object>> waveList = new ArrayList<Map<String,Object>>();
		BigDecimal lastClose = null;
		BigDecimal transClose = null;
		int transCount = 0;
		int direct = 0;
		for(Map<String, Object> dayData : dayDataList){
			BigDecimal close = (BigDecimal)dayData.get("close");
			if(lastClose==null){
				lastClose = close;
			}else{
				if(transCount>0){
					int directTemp = close.compareTo(lastClose);
					if(directTemp>0){
						if(direct==-1||direct==0){
							
						}
				}
				int directTemp = close.compareTo(lastClose);
				if(directTemp>0){
					if(direct==-1||direct==0){
						if(transCount<3){
							if(transClose==null){
								transClose = close;
							}else{
								
							}
							transCount++;
						}else{
							transCount = 0;
						}
					}
				}else if(directTemp<0){
					
				}
			}
		}
	}
}
