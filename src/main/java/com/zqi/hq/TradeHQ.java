package com.zqi.hq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.TestTimer;
import com.zqi.primaryData.fileDataBase.TradeFileDataBase;

@Service("tradeHQ")
public class TradeHQ {
	
	Map<String, Map<String, Object>> lastHqmap = new HashMap<String, Map<String,Object>>();
	Map<String, Map<String, Object>> lastRHismap = new HashMap<String, Map<String,Object>>();
	public static Map<String, Map<String, Object>> zsHqMap = new HashMap<String, Map<String,Object>>();
	public static Map<String, Map<String, Object>> funcRuleMap = new HashMap<String, Map<String,Object>>();
	static ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}
	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	static List<List<Map<String, Object>>> gpCodeStrList = null;
	
	public void init() {
		gpCodeStrList = new ArrayList<List<Map<String, Object>>>();
		String gpdicSql = "select *,1 'seq' from d_gpdic where  symbol in ('sh000001','sz399001','sz399005','sz000300','sz399005') UNION select *,2 'seq' from d_gpdic where type in ('0','1') ORDER BY seq asc";
		List<Map<String, Object>> dicList = zqiDao.findAll(gpdicSql);
		int i=0;
		String zsStr = "sh000001,sz399001,sz399005,sz000300,sz399005";
		String[] zsArr = zsStr.split(",");
		for(String zs : zsArr){
			Map<String, Object> szFuncRule = new HashMap<String, Object>();
			funcRuleMap.put(zs,szFuncRule);
			szFuncRule.put("limit", "0");
		}
		List<Map<String, Object>> threadGpList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> gp : dicList){
			if(i==500){
				gpCodeStrList.add(threadGpList);
				threadGpList = new ArrayList<Map<String,Object>>();
				i=0;
			}
			threadGpList.add(gp);
			i++;
		}
		String rHisDataSql = "select * from daytable_lastmonth where period='2016-06-07'";
		List<Map<String, Object>> rHisDataList = zqiDao.findAll(rHisDataSql);
		for(Map<String, Object> rData : rHisDataList){
			lastRHismap.put(rData.get("code").toString(),rData);
		}
	}
	
	public int parse() {
		TestTimer ttTestTimer = new TestTimer("1111");
		ttTestTimer.begin();
		if(gpCodeStrList == null){
			init();
		}
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(gpCodeStrList.size()); 
		for(List<Map<String, Object>> gpList : gpCodeStrList){
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("gpList", gpList);
			context.put("lastHqmap", lastHqmap);
			context.put("lastRHismap", lastRHismap);
			context.put("dao", zqiDao);
			TradeHQThread tradeHQThread = new TradeHQThread(context);
			fixedThreadPool.execute(tradeHQThread);
		}
		fixedThreadPool.shutdown();
		try {
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ttTestTimer.done();
		return 0;
	}
}
