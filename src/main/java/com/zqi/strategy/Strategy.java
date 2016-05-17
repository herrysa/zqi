package com.zqi.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;

public class Strategy {

	private ZqiDao zqiDao;
	private Map<String, String> title;
	private Map<String, String> initMap;
	private List<StrategyOut> outList;
	private String body;
	
	
	private Map<String, Object> context;
	Map<String, String> parentInitMap;


	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public void setInitParam(String name , String param){
		initMap.put(name, param);
	}
	
	public void setCode(String code) {
		initMap.put("code", code);
	}

	public void setxData(List<Object> xData) {
		JSONArray xArr = JSONArray.fromObject(xData);
		String xDataStr = xArr.toString();
		initMap.put("xData", xDataStr);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<StrategyOut> getOutList() {
		return outList;
	}

	public void setOutList(List<StrategyOut> outList) {
		this.outList = outList;
	}

	public Map<String, String> getInitMap() {
		return initMap;
	}

	public void setInitMap(Map<String, String> initMap) {
		this.initMap = initMap;
	}

	public Map<String, String> getTitle() {
		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	
	public void init(Map<String, Object> context,String filePath){
		this.context = context;
		zqiDao = (ZqiDao)context.get("dao");
		String basePath = context.get("basePath").toString();
		String lib = context.get("lib").toString();
		List<String> contentList = StrategyFactoy.readStrategyFile(basePath+filePath);
		boolean titleEnd = false,initEnd = false;
		title = new HashMap<String, String>();
		body = "";
		parentInitMap = (Map<String, String>)context.get("initMap");
		initMap = new HashMap<String, String>();
		outList = new ArrayList<StrategyOut>();
		for(String content : contentList){
			if("".equals(content)){
				continue;
			}else {
				content = content.trim();
				if(!titleEnd){
					if(content.startsWith("//")){
						if(content.equals("//init")){
							titleEnd = true;
							initEnd = true;
							continue;
						}
						content = content.replaceAll("/","");
						String[] titleContent = content.split(":");
						String key = titleContent[0].trim();
						String value = "";
						if(titleContent.length>1){
							value = titleContent[1].trim();
						}
						title.put(key, value);
						continue;
					}else{
						titleEnd = true;
					}
				}
				if(!initEnd){
					if(content.equals("//init")){
						initEnd = true;
						if(parentInitMap!=null){
							Set<Entry<String, String>> initSet = parentInitMap.entrySet();
							for(Entry<String, String> init : initSet){
								String key = init.getKey();
								String value = init.getValue();
								boolean addInit = false;
								if(value!=null&&value.contains(".")){
									String libName  = value.split("\\.")[0];
									if(!lib.contains(libName)){
										addInit = true;
									}
								}else{
									addInit = true;
								}
								if(addInit){
									String thisInitValue = initMap.get(key);
									if(thisInitValue==null||"".equals(thisInitValue)||"null".equals(thisInitValue)||"''".equals(thisInitValue)||"\"\"".equals(thisInitValue)){
										initMap.put(key,value);
									}
								}
							}
						}
						this.context.put("initMap",initMap);
						continue;
					}
					String[] initArr = content.split(";");
					for(String param :initArr){
						String[] paramArr = param.split("=");
						String var = paramArr[0].trim();
						String value = paramArr[1].trim();
						if(paramArr.length>1){
							if("out".equals(var)){
								String out = value;
								if(out.startsWith("{")){
									JSONObject outObject = JSONObject.fromObject(out);
									Iterator outIt = outObject.keys();
									while(outIt.hasNext()){
										String key = outIt.next().toString();
										String type = outObject.get(key).toString();
										StrategyOut strategyOut = new StrategyOut();
										strategyOut.setName(key);
										strategyOut.setType(type);
										outList.add(strategyOut);
									}
								}else{
									if(out.contains(",")){
										String[] outArr = out.split(",");
										for(String o :outArr){
											StrategyOut strategyOut = new StrategyOut();
											strategyOut.setName(o);
											strategyOut.setType("line");
											outList.add(strategyOut);
										}
									}else{
										if(value.contains("'")){
											value = value.replaceAll("'", "");
										}
										if(value.contains("\"")){
											value = value.replaceAll("\"", "");
										}
										StrategyOut strategyOut = new StrategyOut();
										strategyOut.setName(value);
										strategyOut.setType("line");
										outList.add(strategyOut);
									}
								}
							}else{
								initMap.put(var, value);
							}
						}
					}
					continue;
				}
				body += content;
			}
		}
	}
	
	public void parse(){
		try {
			Set<Entry<String, String>> initSet = initMap.entrySet();
			for(Entry<String, String> param : initSet){
				String varName = param.getKey();
				String func = param.getValue();
				if(func!=null&&func.contains(".")){
					func = func.trim();
					String replaceStr = "";
					if(func.startsWith("indicator.")||func.startsWith("lib.")){
						replaceStr = dataFromJsLib(func);
					}else {
						replaceStr = dataFromJavaLib(func);
					}
					initMap.put(varName, replaceStr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getStrategyScript(){
		Set<Entry<String, String>> initSet = initMap.entrySet();
		String explainedStrategyHead ="";
		for(Entry<String, String> param : initSet){
			String varName = param.getKey();
			String func = param.getValue();
			if(func!=null){
				func = func.trim();
			}
			String varLine = varName+"="+func+";";
			explainedStrategyHead += varLine;
		}
		Map<String, String> utilMap = (Map<String, String>)context.get("util");
		String utilSript = utilMap.get("util");
		return utilSript+"\n"+explainedStrategyHead+"\n"+body;
	}
	
	public void eval(){
		parse();
		ScriptEngineManager manager = new ScriptEngineManager();  
		ScriptEngine engine = manager.getEngineByName("js");
		Bindings bindings  = engine.createBindings();
		try {
			String evalStr = getStrategyScript();
			engine.eval(evalStr,bindings);
			Object result = bindings.get("result");
			JSONObject resultJson = JSONObject.fromObject(result);
			for(StrategyOut strategyOut : outList){
				String name = strategyOut.getName();
				String type = strategyOut.getType();
				Object outValue = resultJson.get(name);
				if(outValue!=null){
					if("table".equals(type)){
						
					}else{
						JSONArray outValueStr = (JSONArray)outValue;
						Object[] valueArr = (Object[])JSONArray.toArray(outValueStr);
						List<Object> valueList = new ArrayList<Object>();
						Collections.addAll(valueList, valueArr);
						strategyOut.setValues(valueList);
					}
				}
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	private void dealFuncParam(String str,List<Class> paramTypeList,List<Object> paramList){
		List<String> matcherList = new ArrayList<String>();
		String funcParamR = Tools.findStr(str,matcherList);
		String[] funcParamRArr = funcParamR.split(",");
		for(String p : funcParamRArr){
			String paramValue = initMap.get(p);
			if(p.startsWith("STR_")){
				String pIndex = p.replace("STR_", "");
				int index = Integer.parseInt(pIndex);
				String oriStr = matcherList.get(index);
				paramList.add(oriStr);
			}else{
				paramList.add(paramValue);
			}
			paramTypeList.add(String.class);
		}
	}
	
	private String dataFromJavaLib(String func){
		String rsStr = "";
		try{
			String parenthesesStr = Tools.findParentheses(func);
			if(parenthesesStr!=null){
				String funcName = func.replace(parenthesesStr, "");
				String funcParam = parenthesesStr.substring(1, parenthesesStr.length()-1);
				String[] funcArr = funcName.split("\\.");
				String libName = funcArr[0];
				String methodName = funcArr[1];
				Class<?> StrategyLib = Class.forName("com.zqi.strategy.lib."+libName);
				Constructor<?> strategyLibConstructor = StrategyLib.getConstructor(ZqiDao.class);
				Object strategyLib = strategyLibConstructor.newInstance(zqiDao);
				List<Class> paramTypeList = new ArrayList<Class>();
				List<Object> paramList = new ArrayList<Object>();
				Object[] paramObject = null;
				Class[] paramTypeObject = null;
				dealFuncParam(funcParam,paramTypeList,paramList);
				paramObject = paramList.toArray(new Object[paramList.size()]); 
				paramTypeObject = paramTypeList.toArray(new Class[paramTypeList.size()]);
				Method method = StrategyLib.getMethod(methodName,paramTypeObject);
				Object result = method.invoke(strategyLib,paramObject);
				rsStr = result.toString();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return rsStr;
	}
	
	private String dataFromJsLib(String func){
		String rsStr = "";
		try{
			func = func.replace(".", "/")+".js";
			Strategy subStrategy = getStrategy(func);
			subStrategy.eval();
			List<StrategyOut> outList = subStrategy.getOutList();
			String rs = "";
			if(outList!=null&&outList.size()!=0){
				StrategyOut strategyOut = outList.get(0);
				String outType = strategyOut.getType();
				List<Object> values = strategyOut.getValues();
				if("line".equals(outType)||"bar".equals(outType)){
					for(Object value : values){
						String v = value.toString();
						if(v.equals("-")){
							rs += "'"+value.toString()+"',";
						}else{
							rs += value.toString()+",";
						}
					}
					rs = rs.substring(0, rs.length()-1);
					rsStr = "["+rs+"]";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rsStr;
	}
	
	public Strategy getStrategy(String fileName){
		Strategy strategy = new Strategy();
		strategy.init(context,fileName);
		return strategy;
	}
}
