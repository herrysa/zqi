package com.zqi.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.unit.FileUtil;

public class StrategyFactoy {

	String basePath = "E:/git/zqi/src/main/webapp/strategy/";
	String utilName = "util.js";
	String utilSript;
	String strategyHeadScript;
	String strategySript;
	StrategyHead strategyHead;
	Map<String, String> initMap;
	
	public StrategyFactoy(String fileName){
		utilSript = FileUtil.readFile(basePath+utilName);
		strategySript = FileUtil.readFile(basePath+fileName);
		String[] strategyArr = strategySript.split("//init");
		strategyHeadScript = strategyArr[0];
		String[] initArr = strategyHeadScript.split(";");
		strategyHead = new StrategyHead();
		initMap = new HashMap<String, String>();
		for(String param :initArr){
			String[] paramArr = param.split("=");
			if(paramArr.length>1){
				initMap.put(paramArr[0], paramArr[1]);
			}
		}
		strategyHead.start = initMap.get("start");
		initMap.remove("start");
		strategyHead.end = initMap.get("end");
		initMap.remove("end");
		strategyHead.code = initMap.get("code");
		initMap.remove("code");
		strategyHead.benchmark = initMap.get("benchmark");
		initMap.remove("benchmark");
		strategyHead.capital_base = initMap.get("capital_base");
		initMap.remove("capital_base");
		strategyHead.freq = initMap.get("freq");
		initMap.remove("freq");
		
		ZqiDao zqiDao = null;
		try {
			Class<?> StrategyFunc = Class.forName("com.zqi.strategy.StrategyFunc");
			Constructor<?> strategyFuncConstructor = StrategyFunc.getConstructor(ZqiDao.class);
			Object strategyFunc = strategyFuncConstructor.newInstance(zqiDao);
			Set<Entry<String, String>> initSet = initMap.entrySet();
			for(Entry<String, String> param : initSet){
				String func = param.getValue();
				String[] funcArr = func.split("(");
				String funcName = funcArr[0];
				String funcParam = funcArr[1].substring(0, 1);
				Method method = StrategyFunc.getMethod(funcName);
				Object result = method.invoke(strategyFunc,"");
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public String getStrategyScript(){
		return utilSript+"\n"+strategySript;
	}
}
