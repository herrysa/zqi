package com.zqi.strategy.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;

public class BaseLib {

	protected ZqiDao zqiDao;

	public BaseLib(ZqiDao zqiDao){
		this.zqiDao = zqiDao;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Map<String,String>> getInternalMethod(String methodName , Class[] paramTypeObject,Object[] paramObject){
		List<Map<String,String>> dataList = null;
		try {
			
        	Class<?> clazz = Class.forName(this.getClass().getName());  
        	Method method = clazz.getMethod(methodName,paramTypeObject);  
        	dataList = (List<Map<String,String>>)method.invoke(null, paramObject);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if(dataList==null){
			dataList = new ArrayList<Map<String,String>>();
		}
		return dataList;
	}
	
	protected void getExendColData(List<Map<String,Object>> codeList,List<String> extendCol){
		for(String ecol : extendCol){
			ecol = ecol.substring(1);
			String methodParam = Tools.findParentheses(ecol);
			String methodName = ecol.replace(methodParam, "");
			methodParam = methodParam.substring(1,methodParam.length()-1);
			/*
			List<Class> paramTypeList = new ArrayList<Class>();
			List<Object> paramList = new ArrayList<Object>();
			Object[] paramObject = null;
			Class[] paramTypeObject = null;
			paramTypeList.add(List.class);
			paramList.add(codeList);
			dealFuncParam(methodParam,paramTypeList,paramList);
			paramObject = paramList.toArray(new Object[paramList.size()]); 
			paramTypeObject = paramTypeList.toArray(new Class[paramTypeList.size()]);*/
			getInternalMethod(methodName,new Class[]{List.class,String.class},new Object[]{codeList,methodParam});
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void dealFuncParam(String str,List<Class> paramTypeList,List<Object> paramList){
		List<String> matcherList = new ArrayList<String>();
		String funcParamR = Tools.findMethod(str,matcherList);
		String[] funcParamRArr = funcParamR.split(",");
		for(String p : funcParamRArr){
			if(p.startsWith("@_")){
				String pIndex = p.replace("@_", "");
				int index = Integer.parseInt(pIndex);
				String oriStr = matcherList.get(index);
				paramList.add(oriStr);
			}else{
				paramList.add(p);
			}
			paramTypeList.add(String.class);
		}
		//return 
	}
}
