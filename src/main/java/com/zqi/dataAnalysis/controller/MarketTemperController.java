package com.zqi.dataAnalysis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zqi.frame.controller.BaseController;
import com.zqi.unit.DateConverter;

@Controller
@RequestMapping("/marketTemper")
public class MarketTemperController extends BaseController{

	@RequestMapping("/main")
	public String marketTemperMain(){
		return "dataAnalysis/marketTemper";
	}
	
	@RequestMapping("/recentlyLimit")
	public String recentlyLimit(){
		String dicSql= "select * from d_gpDic order by symbol";
        List<Map<String, Object>> dicList = this.zqiDao.findAll(dicSql);
        Calendar calendar = Calendar.getInstance();
        Date dateTemp = calendar.getTime();
        DateConverter dateConverter = new DateConverter();
        String periods = "('"+dateConverter.convert(String.class, dateTemp).toString()+"'";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, day-1);
        dateTemp = calendar.getTime();
        periods += ",'"+dateConverter.convert(String.class, dateTemp).toString()+"'";
        calendar.set(Calendar.DAY_OF_MONTH, day-2);
        dateTemp = calendar.getTime();
        periods += ",'"+dateConverter.convert(String.class, dateTemp).toString()+"')";
        for(Map<String, Object> dicMap : dicList){
        	String symbol = dicMap.get("symbol").toString();
        	String name = dicMap.get("name").toString();
        	String code = dicMap.get("code").toString();
        	String daytable = dicMap.get("daytable").toString();
        	String limitSql = "select * from "+daytable+" where code='"+symbol+"' and period in "+periods+" order by code asc";
        	
        }
		
		return "dataAnalysis/recentlyLimit";
	}
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, -1);
        DateConverter dateConverter = new DateConverter();
        String periods = dateConverter.convert(String.class, calendar.getTime()).toString();
        System.out.println(periods);
	}
}
