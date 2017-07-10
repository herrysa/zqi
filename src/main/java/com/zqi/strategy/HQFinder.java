package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.JedisUtils;

/**行情数据辅助查找类
 * @author Administrator
 *
 */
@Component("hQFinder")
public class HQFinder {
	
	/*public enum CACHETYPE {
		PERIOD,CODE
	}*/
	
	private ZqiDao zqiDao;
	private Map<String, String> gpTableMap = new HashMap<String, String>();
	private Gson gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd") .create();
	
	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	public ZqiDao getZqiDao(){
		return zqiDao;
	}
	
	@SuppressWarnings("rawtypes")
	public List findAll(String sql){
		return this.zqiDao.findAll(sql);
	}

	
	/**获取gp字典
	 * @param code
	 * @return
	 */
	public Map<String, Object> getGpDic(String code){
		Map<String, Object> gp = zqiDao.findFirst("select * from d_gpdic where code='"+code+"'");
		return gp;
	}
	
	/**获取gp存储表
	 * @param code
	 * @return
	 */
	public String getGpDataTable(String code){
		String daytable = gpTableMap.get(code);
		if(StringUtils.isNotEmpty(daytable)){
			return daytable;
		}
		Map<String, Object> gp = zqiDao.findFirst("select * from d_gpdic where code='"+code+"'");
		if(gp!=null){
			daytable = gp.get("daytable").toString();
			gpTableMap.put(code, daytable);
			return daytable;
		}else{
			return "daytable_all";
		}
	}
	
	/**获取某一天的历史数据 数据库取
	 * @param code
	 * @param period
	 * @return
	 */
	public Map<String, Object> getGpHq(String year , String code , String period){
		String daytable = getGpDataTable(code);
		String daydataSql = "select * from "+year+"_"+daytable+" where code='"+code+"' and period='"+period+"'";
		Map<String, Object> daydata = zqiDao.findFirst(daydataSql);
		return daydata;
	}
	
	/**获取某一段时间的历史数据 数据库取
	 * @param code
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getGpHq(String year , String code , String start , String end){
		String daytable = getGpDataTable(code);
		String daydataSql = "select * from "+year+"_"+daytable+" where code='"+code+"' and period>='"+start+"'"+"and period<='"+end+"'";
		List<Map<String, Object>> daydataList = zqiDao.findAll(daydataSql);
		return daydataList;
	}
	
	/**多参数获取某一段时间的历史数据 数据库取
	 * @param code
	 * @param optionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getGpHq(String year, String code , String period , Map<String, String> optionMap){
		String start = null, end = null;
		if(period==null){
			if(optionMap.containsKey("start")){
				start = optionMap.get("start");
			}
			if(optionMap.containsKey("end")){
				end = optionMap.get("end");
			}
		}
		
		String findSql = null;
		if("all".equals(code)){
			findSql = "select * from "+year+"_daytable_all where 1=1 and type in (0,1)";
		}else if(code.contains(",")){
			findSql = "select * from "+year+"_daytable_all where code in "+code;
		}else{
			String daytable = getGpDataTable(code);
			findSql = "select * from "+year+"_"+daytable+" where code='"+code+"'";
		}
		if(start!=null&&end!=null){
			findSql += " and period between '"+start+"' and '"+end+"'";
		}else if(start!=null){
			findSql += " and period>='"+start+"'";
		}else if(end!=null){
			findSql += " and period<='"+end+"'";
		}
		if(period!=null){
			findSql += " and period='"+period+"'";
		}
		/*if(!"0".equals(ex_suspended)){
			findSql += " and close<>0";
		}
		if(!"0".equals(ex_new)){
			findSql += " and invalid=0";
		}*/
		findSql += " order by code asc,period asc";
		
		List<Map<String, Object>> daydataList = zqiDao.findAll(findSql);
		return daydataList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getGpPeriodHq(String year, String period){
		Map<String, Object> periodData = zqiDao.findFirst("select * from "+year+"_daytable_period where period='"+period+"'");
		if(periodData.isEmpty()){
			return null;
		}
		String d = periodData.get("D").toString();
		return gson.fromJson(d, List.class);
	}
	
/*	public HQDataBox getHQDataBox(String code,Map<String, String> optionMap){
		HQDataBox hqDataBox = new HQDataBox() ;
		List<Map<String, Object>> daydataList = this.getGpHq(code, null ,optionMap);
		if(!daydataList.isEmpty()){
			Map<String, Object> data = daydataList.get(0);
			String name = data.get("name").toString();
			hqDataBox.setName(name);
		}
		hqDataBox.setCode(code);
		hqDataBox.setDataList(daydataList);
		//hqDataBox.setHqFinder(this);
		return hqDataBox;
	}*/
	
/*	public HQDataBox getHQDataBox(String code,String period , Map<String, String> optionMap){
		HQDataBox hqDataBox = new HQDataBox() ;
		List<Map<String, Object>> daydataList = this.getGpHq(code,period , optionMap);
		if(!daydataList.isEmpty()){
			Map<String, Object> data = daydataList.get(0);
			String name = data.get("name").toString();
			hqDataBox.setName(name);
		}
		hqDataBox.setCode(code);
		hqDataBox.setDataList(daydataList);
		//hqDataBox.setHqFinder(this);
		return hqDataBox;
	}*/
	
/*	public List<HQDataBox> getHQDataBoxs(String codeStr,String period , Map<String, String> optionMap){
		List<Map<String, Object>> dataList = getGpHq(codeStr, period, optionMap);
		List<HQDataBox> hqDataBoxs = new ArrayList<HQDataBox>();
		for(Map<String, Object> data : dataList){
			String code = data.get("code").toString();
			HQDataBox hqDataBox = new HQDataBox() ;
			hqDataBox.setCode(code);
			hqDataBox.setData(data);
			//hqDataBox.setHqFinder(this);
		}
		return hqDataBoxs;
	}*/
	
/*	public void getHQDataBoxMap(String codeStr,String period , Map<String, String> optionMap,Map<String, HQDataBox> hqDataBoxMap){
		List<Map<String, Object>> dataList = this.getGpHq(codeStr, period, optionMap);
		for(Map<String, Object> data : dataList){
			String code = data.get("code").toString();
			HQDataBox hqDataBox = hqDataBoxMap.get(code);
			if(hqDataBox==null){
				hqDataBox = new HQDataBox();
				hqDataBoxMap.put(code,hqDataBox);
			}
			hqDataBox.setData(data);
		}
	}*/
	
	public List<HQDataBox> getHQDataBoxs(String[] codeArr,Map<String, String> optionMap){
		List<HQDataBox> hqDataBoxs = new ArrayList<HQDataBox>();
		for(String code : codeArr){
			//hqDataBoxs.add(this.getHQDataBox(code, optionMap));
			System.out.println(code);
		}
		/*Map<String, List<String>> dayTableMap = new HashMap<String, List<String>>();
		for(String code : codeArr){
			String dayTable = getGpDataTable(code);
			List<String> codeList = dayTableMap.get(dayTable);
			if(codeList==null){
				codeList = new ArrayList<String>();
				dayTableMap.put(dayTable, codeList);
			}
			codeList.add(code);
		}
		Set<String> dayTableSet = dayTableMap.keySet();
		for(String dayTable : dayTableSet){
			List<String> codeList = dayTableMap.get(dayTable);
			String codeStr = "";
			for(String code : codeList){
				codeStr += "'"+code+"',";
			}
			if(!"".equals(codeStr)){
				codeStr = codeStr.substring(0, codeStr.length()-1);
			}
			codeStr += "("+codeStr+")";
			
			List<Map<String, Object>> daydataList = this.getGpHq(codeStr, dayTable, optionMap);
			String code = "";
			HQDataBox hqDataBox = null ;
			for(Map<String, Object> data : daydataList){
				String codeTemp = data.get("code").toString();
				if(!codeTemp.equals(code)){
					code = codeTemp;
					hqDataBox = new HQDataBox();
					hqDataBox.setDataName(code);
					hqDataBox.setOptionMap(optionMap);
					hqDataBox.setHqFinder(this);
					hqDataBoxs.add(hqDataBox);
				}
				hqDataBox.setData(data);
			}
		}
		
		*/
		return hqDataBoxs;
	}
	
	/*public List<HQDataBox> getHQDataBoxs(String[] codeArr,Map<String, String> optionMap){
		List<HQDataBox> hqDataBoxs = new ArrayList<HQDataBox>();
		ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<String, Future>();  
		ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(10);
		for(String code : codeArr){
			HQDataBox hqDataBox = new HQDataBox();
			hqDataBox.setDataName(code);
			hqDataBox.setOptionMap(optionMap);
			hqDataBox.setFutureMap(futureMap);
			hqDataBox.setHqFinder(this);
			Future future = scheduler.scheduleWithFixedDelay(hqDataBox, 0, 100, TimeUnit.MILLISECONDS);
			futureMap.put(code, future);
			hqDataBoxs.add(hqDataBox);
		}
		scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		scheduler.shutdown();
		try {
			while(!scheduler.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return hqDataBoxs;
	}*/
	
	/*
	 * 以下为redis数据
	 * 
	 * 
	 */
	

	public List<Object> getGpDicData(){
		return (List<Object>)JedisUtils.getObjectList("gpDic");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getRHQData(String code , String period){
		return (Map<String,Object>)JedisUtils.getObject(code+":"+period);
	}
	
	public BigDecimal getFhData(String code , String period){
		return (BigDecimal)JedisUtils.getObject("fh:"+code+":"+period);
	}
	
	public BigDecimal getSgData(String code , String period){
		return (BigDecimal)JedisUtils.getObject("sg:"+code+":"+period);
	}
	
	public BigDecimal getZzData(String code , String period){
		return (BigDecimal)JedisUtils.getObject("zz:"+code+":"+period);
	}
	
	public BigDecimal getPgData(String code , String period){
		return (BigDecimal)JedisUtils.getObject("pg:"+code+":"+period);
	}
	
	public BigDecimal getPgPriceData(String code , String period){
		return (BigDecimal)JedisUtils.getObject("pgPrice:"+code+":"+period);
	}
	
	/*public void fillData(String code,Map<String, String> optionMap){
		List<Map<String, Object>> daydataList = getGpHq(code,optionMap);
		for(Map<String, Object> dataMap : daydataList){
			String codeTemp = dataMap.get("code").toString();
			String period = dataMap.get("period").toString();
			cache.put(codeTemp+"_"+period, dataMap);
		}
	}*/
//	public List<Map<String, Object>> getLocalData(String period){
//		return localDataMap.get(period);
//	}
	
	public static void main(String[] args) {
		ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<String, Future>();  
		ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(5);
		for(int i=0;i<5;i++){
			HQDataBox command = new HQDataBox();
			command.setCode("qq"+i);
			//command.setFutureMap(futureMap);
			//Future future = scheduler.scheduleWithFixedDelay(command, 0, 1000, TimeUnit.MILLISECONDS);
			//futureMap.put("qq"+i, future);
		}
		scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		scheduler.shutdown();
		try {
			while(!scheduler.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(111111);
	}
}
