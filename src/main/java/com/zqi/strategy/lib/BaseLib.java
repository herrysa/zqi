package com.zqi.strategy.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.zqi.frame.dao.impl.ZqiDao;

public class BaseLib {

	protected ZqiDao zqiDao;

	public BaseLib(ZqiDao zqiDao){
		this.zqiDao = zqiDao;
	}
	
	private List<Map<String,String>> getInternalMethod(String methodName , List<Map<String,String>> codeList,Map<String, Object> paramMap){
		List<Map<String,String>> dataList = null;
		try {
        	Class<?> clazz = Class.forName("com.zqi.strategy.lib.Data");  
        	Method method = clazz.getMethod(methodName, List.class,Map.class);  
        	dataList = (List<Map<String,String>>)method.invoke(null, codeList,paramMap);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dataList==null){
			dataList = codeList;
		}
		return dataList;
	}
}
