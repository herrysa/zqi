package com.zqi.strategy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;
import com.zqi.strategy.StrategyOut.OUTTYPE;
import com.zqi.unit.FileUtil;

public class StrategyJs implements IStrategy{

	private ZqiDao zqiDao;
	private StrategyTitle title = new StrategyTitle();
	private Map<String, StrategyHead> initMap;
	private List<String> initSeq;
	private List<StrategyOut> outList;
	private String body;
	public static String lib = "Data,indicator,lib";
	
	
	private Map<String, Object> context;
	Map<String, StrategyHead> parentInitMap;


	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public String getInitParam(String name ){
		StrategyHead strategyHead = initMap.get(name);
		if(strategyHead!=null){
			return strategyHead.getValue();
		}else{
			return null;
		}
	}
	
	public void setJavaParam(String name , String param){
		StrategyHead strategyHead = initMap.get(name);
		if(strategyHead!=null){
			strategyHead.setJavaValue(param,null);
		}
	}
	public void setJavaParam(String name , String param , Integer type){
		StrategyHead strategyHead = initMap.get(name);
		if(strategyHead!=null){
			strategyHead.setJavaValue(param,type);
		}
	}
	
	public void setJsParam(String name , String param){
		StrategyHead strategyHead = initMap.get(name);
		if(strategyHead==null){
			strategyHead = new StrategyHead();
			strategyHead.setName(name);
		}
		strategyHead.setJsValue(param);
		initMap.put(name, strategyHead);
	}
	
	public void setJsLocalParam(String name , String param){
		StrategyHead strategyHead = initMap.get(name);
		if(strategyHead==null){
			strategyHead = new StrategyHead();
			strategyHead.setName(name);
			strategyHead.setLocal(true);
		}
		strategyHead.setJsValue(param);
		initMap.put(name, strategyHead);
	}
	

	public void setxData(List<Object> xData) {
		//JSONArray xArr = JSONArray.fromObject(xData);
		Gson gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd") .create();
		String xDataStr = gson.toJson(xData);
		//String xDataStr = xArr.toString();
		setJsParam("xData", xDataStr);
	}

	@SuppressWarnings("unchecked")
	public void addLib(String fileName){
		String basePath = context.get("basePath").toString();
		File lib = new File(basePath+fileName);
		String libSript = FileUtil.readFile(basePath+fileName);
		libSript = libSript.replaceAll("\t", "");
		libSript = libSript.replaceAll("\n", "");
		Map<String, String> utilMap = (Map<String, String>)context.get("util");
		utilMap.put(lib.getName(), libSript);
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

	public Map<String, StrategyHead> getInitMap() {
		return initMap;
	}

	public void setInitMap(Map<String, StrategyHead> initMap) {
		this.initMap = initMap;
	}

	@SuppressWarnings("unchecked")
	public void init(Map<String, Object> context,String filePath){
		this.context = context;
		zqiDao = (ZqiDao)context.get("dao");
		String basePath = context.get("basePath").toString();
		//String lib = context.get("lib").toString();
		String fileFullpath = basePath+filePath;
		List<String> contentList = StrategyJsFactoy.readStrategyFile(fileFullpath);
		File file = new File(fileFullpath);
		String fileName = file.getName();
		title.setCode( fileName.split("\\.")[0]);
		body = "";
		parentInitMap = (Map<String, StrategyHead>)context.get("initMap");
		initMap = new HashMap<String, StrategyHead>();
		initSeq = new ArrayList<String>();
		outList = new ArrayList<StrategyOut>();
		
		if(!contentList.isEmpty()){
			contentList = addTitleMap(contentList);
			contentList = addInitParam(contentList);
			contentList = addLocalParam(contentList);
			contentList = addBody(contentList);
		}
	}
	
	private List<String> addTitleMap(List<String> contentList){
		String content = contentList.get(0);
		if(!StringUtils.isEmpty(content)&&content.startsWith("//")){
			content = content.replaceAll("/","");
			String[] titleContent = content.split(":");
			String key = titleContent[0].trim();
			String value = "";
			if(titleContent.length>1){
				value = titleContent[1].trim();
			}
			//title.put(key, value);
			contentList = contentList.subList(1, contentList.size());
		}
		return contentList;
	}
	private List<String> addInitParam(List<String> contentList){
		boolean initEnd = false;
		int i = 0;
		for(String content : contentList){
			if(!initEnd){
				if(content.equals("//init")){
					initEnd = true;
					contentList = contentList.subList(i+1, contentList.size());
					if(parentInitMap!=null){
						Set<Entry<String, StrategyHead>> initSet = parentInitMap.entrySet();
						for(Entry<String, StrategyHead> init : initSet){
							String key = init.getKey();
							StrategyHead headValue = init.getValue();
							StrategyHead initHeadValue = initMap.get(key);
							if(initHeadValue==null){
								initMap.put(key,headValue);
								if(!initSeq.contains(key)){
									initSeq.add(key);
								}
							}else{
								String value = initHeadValue.getValue();
								if(StringUtils.isEmpty(value)){
									initMap.put(key,headValue);
									if(!initSeq.contains(key)){
										initSeq.add(key);
									}
								}
							}
						}
					}
					this.context.put("initMap",initMap);
					break;
				}
				String[] initArr = content.split(";");
				for(String param :initArr){
					if(!param.contains("=")){
						continue;
					}
					String[] paramArr = param.split("=");
					String var = paramArr[0].trim();
					String value = paramArr[1].trim();
					if(paramArr.length>1){
						if("out".equals(var)){
							dealOut(value);
						}else{
							setJsParam(var, value);
							initSeq.add(var);
						}
					}
				}
			}
			i++;
		}
		return contentList;
	}
	
	private List<String> addLocalParam(List<String> contentList){
		boolean localEnd = false;
		int i = 0;
		for(String content : contentList){
			if(!localEnd){
				if(content.equals("//local")){
					localEnd = true;
					contentList = contentList.subList(i+1, contentList.size());
					break;
				}
				String[] initArr = content.split(";");
				for(String param :initArr){
					if(!param.contains("=")){
						continue;
					}
					String[] paramArr = param.split("=");
					String var = paramArr[0].trim();
					String value = paramArr[1].trim();
					if(paramArr.length>1){
						setJsLocalParam(var, value);
						initSeq.add(var);
					}
				}
			}
			i++;
		}
		return contentList;
	}
	
	private List<String> addBody(List<String> contentList){
		for(String content : contentList){
			body += content;
		}
		return contentList;
	}
	
	public void parse(){
		try {
			Set<Entry<String, StrategyHead>> initSet = initMap.entrySet();
			for(Entry<String, StrategyHead> param : initSet){
				StrategyHead func = param.getValue();
				if(func.getType()==4&&!func.isInited()){
					String funcName = func.getInitValue();
					String replaceStr = "";
					if(funcName.startsWith("indicator.")||funcName.startsWith("lib.")){
						replaceStr = dataFromJsLib(funcName);
						func.setValue(replaceStr);
						if(!func.isLocal()){
							func.setInited(true);
						}
					}else if(funcName.startsWith("Data.")){
						replaceStr = dataFromJavaLib(funcName);
						func.setValue(replaceStr);
						if(!func.isLocal()){
							func.setInited(true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDataMap(String code){
		try {
			StrategyHead func = initMap.get(code);
			if(func.getType()==4){
				String funcName = func.getInitValue();
				return (Map<String, Object>)getDataFromJavaLib(funcName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String getStrategyScript(){
		String explainedStrategyHead ="";
		for(String varName : initSeq){
			StrategyHead func = initMap.get(varName);
			String varLine = varName+"="+func.getJsValue()+";";
			explainedStrategyHead += varLine;
		}
		Map<String, String> utilMap = (Map<String, String>)context.get("util");
		String utilSript = "";
		Set<Entry<String, String>> utilSet = utilMap.entrySet();
		for(Entry<String, String> util : utilSet){
			String value = util.getValue();
			utilSript += "\n"+value;
		}
		return utilSript+"\n"+explainedStrategyHead+"\n"+body;
	}
	
	@SuppressWarnings("unchecked")
	public void eval(){
		parse();
		ScriptEngineManager manager = new ScriptEngineManager();  
		ScriptEngine engine = manager.getEngineByName("js");
		Bindings bindings  = engine.createBindings();
		try {
			String evalStr = getStrategyScript();
			//System.out.println(evalStr);
			engine.eval(evalStr,bindings);
			Object result = bindings.get("result");
			//System.out.println(result.toString());
			Gson gson = new Gson();
			Map<String, Object> rsMap = gson.fromJson(result.toString(), Map.class);
			for(StrategyOut strategyOut : outList){
				String name = strategyOut.getName();
				OUTTYPE type = strategyOut.getType();
				Object outValue = rsMap.get(name);
				if(outValue!=null){
					if(type==OUTTYPE.json){
						strategyOut.addValueOnlyOne((Map)outValue);
					}else{
						strategyOut.setValues((List)outValue);
					}
				}
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void dealFuncParam(String str,List<Class> paramTypeList,List<Object> paramList){
		List<String> matcherList = new ArrayList<String>();
		if(str.contains("{")){
			str = str.replaceAll("\"", "\'");
			String patternStr = "'";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(str);
			List<Integer> quoteList = new ArrayList<Integer>();
			while(matcher.find()){
				quoteList.add(matcher.start());
			}
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			Map<String, Object> paramMap = gson.fromJson(str, Map.class);
			Set<String> paramKeySet = paramMap.keySet();
			int charIndex = 1,quotePointer = 0;
			for(String p : paramKeySet){
				Object pvObj = paramMap.get(p);
				String pv = pvObj.toString();
				charIndex += p.length()+1;
				
				Integer quotePosition = 0;
				if(quotePointer<quoteList.size()){
					quotePosition = quoteList.get(quotePointer);
				}
				if(quotePosition!=charIndex){
					StrategyHead strategyHead = initMap.get(pv);
					String paramValue = "";
					if(strategyHead!=null){
						paramValue = strategyHead.getValue();
					}
					paramMap.put(p, paramValue);
					charIndex += pv.length()+1;
				}else{
					quotePointer += 2;
					charIndex += pv.length()+3;
				}
			}
			str = gson.toJson(paramMap);
			paramList.add(str);
			paramTypeList.add(String.class);
		}else{
			String funcParamR = Tools.findStr(str,matcherList);
			String[] funcParamRArr = funcParamR.split(",");
			for(String p : funcParamRArr){
				StrategyHead strategyHead = initMap.get(p);
				String paramValue = strategyHead.getValue();
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
		
	}
	
	@SuppressWarnings("unchecked")
	private void dealOut(String out){
		outList.clear();
		if(out.startsWith("{")){
			Gson gson = new Gson();
			Map<String,Object> outMap = gson.fromJson(out, Map.class);
			Set<String> outKeySet = outMap.keySet();
			for(String outKey : outKeySet){
				StrategyOut strategyOut = new StrategyOut();
				Object paramObj = outMap.get(outKey);
				String type = null;
				if(paramObj instanceof Map){
					Map<String, Object> paramMap = (Map<String, Object>)paramObj;
					type = paramMap.get("type").toString();
					Boolean accu = (Boolean)paramMap.get("accu");
					strategyOut.setParamMap(paramMap);
					if(accu!=null){
						strategyOut.setAccu(accu);
					}
				}else{
					type = paramObj.toString();
				}
				strategyOut.setName(outKey);
				strategyOut.setType(OUTTYPE.valueOf(type));
				outList.add(strategyOut);
			}
		}else{
			if(out.contains(",")){
				out = getPureStr(out);
				String[] outArr = out.split(",");
				for(String o :outArr){
					StrategyOut strategyOut = new StrategyOut();
					strategyOut.setName(o);
					strategyOut.setType(OUTTYPE.line);
					outList.add(strategyOut);
				}
			}else{
				out = getPureStr(out);
				StrategyOut strategyOut = new StrategyOut();
				strategyOut.setName(out);
				strategyOut.setType(OUTTYPE.line);
				outList.add(strategyOut);
			}
		}
	}
	
	private Object getDataFromJavaLib(String func){
		try{
			String parenthesesStr = Tools.findParentheses(func);
			if(parenthesesStr!=null&&!"".equals(parenthesesStr)){
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
				return result;
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
		return null;
	}
	
	private String dataFromJavaLib(String func){
		String rsStr = "";
		try{
			Object result = getDataFromJavaLib(func);
			//JSONObject dataJsonObject = JSONObject.fromObject(result);
			Gson gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd") .create();
			rsStr = gson.toJson(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsStr;
	}
	
	private String dataFromJsLib(String func){
		String rsStr = "";
		try{
			func = func.replace(".", "/")+".js";
			StrategyJs subStrategy = getStrategy(func);
			subStrategy.eval();
			List<StrategyOut> outList = subStrategy.getOutList();
			if(outList!=null&&outList.size()!=0){
				StrategyOut strategyOut = outList.get(0);
				List<Object> values = strategyOut.getValues();
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
				rsStr = gson.toJson(values);
				System.out.println(rsStr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rsStr;
	}
	
	public StrategyJs getStrategy(String fileName){
		StrategyJs strategy = new StrategyJs();
		strategy.init(context,fileName);
		return strategy;
	}
	
	public String getPureStr(String str){
		str = str.replaceAll("'", "");
		str = str.replaceAll("\"", "");
		return str;
	}
	
	public static void main(String[] args) {
		String pattern = "'";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher("{a:1,aa:'22',bb:''}");
		int matcherCount = 0;
		while(matcher.find()){
			String matcherStr = matcher.group();
			System.out.println(matcher.start());
		}
	}

	public StrategyTitle getTitle() {
		return title;
	}
}
