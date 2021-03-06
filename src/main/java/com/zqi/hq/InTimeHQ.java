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
import com.zqi.frame.quartz.Zjob;
import com.zqi.frame.util.TestTimer;

@Service("inTimeHQ")
public class InTimeHQ implements Zjob{

	Map<String, Map<String, Object>> lastHqmap = new HashMap<String, Map<String,Object>>();
	public static Map<String, Map<String, Object>> zsHqMap = new HashMap<String, Map<String,Object>>();
	public static Map<String, Map<String, Object>> funcRuleMap = new HashMap<String, Map<String,Object>>();
	public static ZqiDao zqiDao;
	
	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	static List<String> gpCodeStrList = null;
	
	public static void init() {
		gpCodeStrList = new ArrayList<String>();
		String gpdicSql = "select * from d_gpdic where type in ('0','1')";
		List<Map<String, Object>> dicList = zqiDao.findAll(gpdicSql);
		int i=0;
		String zsStr = "sh000001,sz399001,sz399005,sz000300,sz399005";
		String[] zsArr = zsStr.split(",");
		for(String zs : zsArr){
			Map<String, Object> szFuncRule = new HashMap<String, Object>();
			funcRuleMap.put(zs,szFuncRule);
			szFuncRule.put("limit", "0");
		}
		String gpCodeStr = zsStr;
		for(Map<String, Object> gp : dicList){
			String code = gp.get("symbol").toString();
			if(i==500){
				gpCodeStrList.add(gpCodeStr);
				gpCodeStr = "";
				i=0;
			}
			gpCodeStr += ","+code;
			i++;
		}
	}
	@Override
	public int execute() {
		TestTimer ttTestTimer = new TestTimer("1111");
		ttTestTimer.begin();
		if(gpCodeStrList == null){
			init();
		}
		//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(gpCodeStrList.size()); 
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3); 
		for(String codeStr : gpCodeStrList){
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("codeStr", codeStr);
			context.put("lastHqmap", lastHqmap);
			context.put("dao", zqiDao);
			InTimeHQThread inTimeHQThread = new InTimeHQThread(context);
			fixedThreadPool.execute(inTimeHQThread);
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
