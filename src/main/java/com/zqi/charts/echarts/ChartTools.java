package com.zqi.charts.echarts;

import sun.security.action.GetBooleanAction;

public class ChartTools {

	public static String getStr(String str){
		return "'"+str+"'";
	}
	
	public static String getArrayStr(String[] arr){
		String str = "";
		for(String a : arr){
			str += getStr(a)+",";
		}
		str = str.substring(0, str.length()-1);
		return "["+str+"]";
	}
	
	public static String getBooleanStr(Boolean b){
		return b.toString();
	}
	
	public static String getIntegerStr(Integer i){
		return i.toString();
	}
}
