package com.zqi.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;
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
	
	private StrategyFactoy strategyFactoy;
	
	private String code;
	private List<Object> xData;
	
	String basePath = "E:/git/zqi/src/main/webapp/strategy/";
	String utilName = "util.js";
	String utilSript;
	String strategyHeadScript;
	String strategyBodyScript;
	String strategySript;
	Map<String, String> initMap;
	String xDataStr;
	List<StrategyOut> outList;
	
	public StrategyFactoy(){
		utilSript = FileUtil.readFile(basePath+utilName);
		utilSript = utilSript.replaceAll("\t", "");
		utilSript = utilSript.replaceAll("\n", "");
	}
	
	public StrategyFactoy getInstance(){
		if(strategyFactoy==null){
			strategyFactoy = new StrategyFactoy();
		}
		return strategyFactoy;
	}
	
	public Strategy getStrategy(String fileName){
		Strategy strategy = new Strategy();
		List<String> contentList = readStrategyFile(basePath+fileName);
		strategy.init(contentList);
		return strategy;
	}
	
	public void init(String fileName){
		strategySript = FileUtil.readFile(basePath+fileName);
		String[] strategyArr = strategySript.split("//init");
		strategyHeadScript = strategyArr[0];
		strategyBodyScript = strategyArr[1];
		String[] initArr = strategyHeadScript.split(";");
		initMap = new HashMap<String, String>();
		outList = new ArrayList<StrategyOut>();
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
	
	public void parse(){
		try {
			Set<Entry<String, String>> initSet = initMap.entrySet();
			for(Entry<String, String> param : initSet){
				String varName = param.getKey();
				String func = param.getValue();
				if(func!=null&&func.contains(".")){
					func = func.trim();
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
						String rsStr = result.toString();
						initMap.put(varName, rsStr);
						//JSONObject jsonResult = JSONObject.fromObject(result);
						//return jsonResult;
					}
				}
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
		//return null;
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
		return utilSript+"\n"+explainedStrategyHead+"\n"+strategyBodyScript;
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
		//return 
	}
	
	public List<String> readStrategyFile( String filePath ) {
        File ds = null;
        FileReader fr = null;
        BufferedReader br = null;
        //String fileContent = "";
        List<String> contentList = new ArrayList<String>();
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                fr = new FileReader( ds );
                br = new BufferedReader( fr );
                temp = br.readLine();
                while ( temp != null ) {
                    //fileContent += temp;
                    contentList.add(temp);
                    temp = br.readLine();
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( br != null ) {
                    br.close();
                }
                if ( fr != null ) {
                    fr.close();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        return contentList;
    }
	
	public static void main(String[] args) {
		try {
			String str = "{a:[1,2,3]}";
			JSONObject jsonObject = JSONObject.fromObject(str);
			JSONArray a = (JSONArray)jsonObject.get("a");
			Object[] aa = (Object[])JSONArray.toArray(a);
			List<Object> userList = new ArrayList<Object>();
			Collections.addAll(userList, aa);
			System.out.println();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		initMap.put("code", code);
	}

	public List<Object> getxData() {
		return xData;
	}

	public void setxData(List<Object> xData) {
		this.xData = xData;
		JSONArray xArr = JSONArray.fromObject(xData);
		xDataStr = xArr.toString();
		initMap.put("xData", xDataStr);
	}
	
	public List<StrategyOut> getOutList() {
		return outList;
	}

	public void setOutList(List<StrategyOut> outList) {
		this.outList = outList;
	}
}
