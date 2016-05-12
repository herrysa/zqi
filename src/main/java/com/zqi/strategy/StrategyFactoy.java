package com.zqi.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.unit.FileUtil;

@Component("strategyFactoy")
public class StrategyFactoy {

	ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}

	String basePath = "E:/git/zqi/src/main/webapp/strategy/";
	String utilName = "util.js";
	String utilSript;
	String strategyHeadScript;
	String strategySript;
	StrategyHead strategyHead;
	Map<String, String> initMap;
	
	public StrategyFactoy(){
		
	}
	
	public void init(String fileName){
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
				initMap.put(paramArr[0].trim(), paramArr[1].trim());
			}
		}
		/*strategyHead.start = initMap.get("start");
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
		initMap.remove("freq");*/
		
		
	}
	
	public void parse(String code){
		initMap.put("code", code);
		try {
			Class<?> StrategyFunc = Class.forName("com.zqi.strategy.StrategyFunc");
			Constructor<?> strategyFuncConstructor = StrategyFunc.getConstructor(ZqiDao.class);
			Object strategyFunc = strategyFuncConstructor.newInstance(zqiDao);
			Set<Entry<String, String>> initSet = initMap.entrySet();
			for(Entry<String, String> param : initSet){
				String func = param.getValue();
				func = func.trim();
				if(!func.startsWith("func_")){
					continue;
				}
				String[] funcArr = func.split("\\(");
				String funcName = funcArr[0];
				String funcParam = funcArr[1].substring(0, funcArr[1].length()-1);
				String[] funcParamArr = funcParam.split(",");
				List<Class> paramTypeList = new ArrayList<Class>();
				List<Object> paramList = new ArrayList<Object>();
				Object[] paramObject = null;
				Class[] paramTypeObject = null;
				for(String p : funcParamArr){
					String paramValue = initMap.get(p);
					if(p.startsWith("'")){
						paramList.add(p);
					}else{
						paramList.add(paramValue);
					}
					paramTypeList.add(String.class);
				}
				paramObject = paramList.toArray(new Object[paramList.size()]); 
				paramTypeObject = paramTypeList.toArray(new Class[paramTypeList.size()]);
				Method method = StrategyFunc.getMethod(funcName,paramTypeObject);
				Object result = method.invoke(strategyFunc,paramObject);
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
