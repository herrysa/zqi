package com.zqi.dataAnalysis.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
		//String dicSql= "select * from d_gpDic order by symbol";
       // List<Map<String, Object>> dicList = this.zqiDao.findAll(dicSql);
        
        /*for(Map<String, Object> dicMap : dicList){
        	String symbol = dicMap.get("symbol").toString();
        	String name = dicMap.get("name").toString();
        	String code = dicMap.get("code").toString();
        	String daytable = dicMap.get("daytable").toString();
        	String limitSql = "select * from "+daytable+" where code in (select code from "+daytable+" where  and period in "+periods+" order by code asc) and period in "+periods+" order by code asc";
        	
        }*/
		
		return "dataAnalysis/recentlyLimit";
	}
	
	@ResponseBody
	@RequestMapping("/recentlyLimitUpGridList")
	public Map<String, Object> recentlyLimitUpGridList(){
		Calendar calendar = Calendar.getInstance();
        Date dateTemp = calendar.getTime();
        DateConverter dateConverter = new DateConverter();
        int findDays = 3,backDays = 0;
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        String[]periods = null;
        List<String> periodList = new ArrayList<String>();
        while(findDays>0){
        	calendar.set(Calendar.DAY_OF_MONTH, today-backDays);
        	if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
        		backDays++;
        		continue;
        	}
            dateTemp = calendar.getTime();
            periodList.add(dateConverter.convert(String.class, dateTemp).toString());
            findDays--;
        }
        String findLimitUpSql = "";
        for(int t=1;t<59;t++){
        	String daytable = "daytable"+t;
        	findLimitUpSql += "SELECT period,code,name,ROUND((close-settlement)/settlement*100,2) increasePercent FROM "+daytable+" WHERE CODE IN (SELECT CODE FROM "+daytable+" WHERE ROUND(settlement * 1.1, 2) = `close` AND period IN ('2016-04-25','2016-04-22','2016-04-21') ORDER BY period DESC) AND period IN ('2016-04-25','2016-04-22','2016-04-21')";
        	if(t==58){
        		
        	}else{
        		findLimitUpSql += " UNION ";
        	}
        }
        List limitUpList = this.zqiDao.findAll(findLimitUpSql);
        this.resultMap.put("page", "1");
        this.resultMap.put("rows", limitUpList);
        this.resultMap.put("total",limitUpList.size());
        return this.resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/recentlyLimitDownGridList")
	public Map<String, Object> recentlyLimitDownGridList(){
		Calendar calendar = Calendar.getInstance();
        Date dateTemp = calendar.getTime();
        DateConverter dateConverter = new DateConverter();
        int findDays = 3,backDays = 0;
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        String[]periods = null;
        List<String> periodList = new ArrayList<String>();
        while(findDays>0){
        	calendar.set(Calendar.DAY_OF_MONTH, today-backDays);
        	if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
        		backDays++;
        		continue;
        	}
            dateTemp = calendar.getTime();
            periodList.add(dateConverter.convert(String.class, dateTemp).toString());
            findDays--;
        }
        String findLimitDownSql = "";
        for(int t=0;t<58;t++){
        	String daytable = "daytable"+t;
        	findLimitDownSql = "SELECT period,code,name,ROUND((close-settlement)/settlement*100,2) increasePercent FROM "+daytable+" WHERE CODE IN (SELECT CODE FROM "+daytable+" WHERE ROUND(settlement * 0.9, 2) = `close` AND period IN ('2016-04-25','2016-04-22','2016-04-21') ORDER BY period DESC) AND period IN ('2016-04-25','2016-04-22','2016-04-21')";
        	if(t==58){
        		
        	}else{
        		findLimitDownSql += " UNION ";
        	}
        }
        List limitDownList = this.zqiDao.findAll(findLimitDownSql);
        this.resultMap.put("page", "1");
        this.resultMap.put("rows", limitDownList);
        this.resultMap.put("total",limitDownList.size());
        return this.resultMap;
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
