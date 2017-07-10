package com.zqi.primaryData;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zqi.dataFinder.IFinderFh;
import com.zqi.dataFinder.IFinderRToday;
import com.zqi.dataFinder.wy163.Finder163Fh;
import com.zqi.dataFinder.wy163.Finder163RToday;
import com.zqi.dataFinder.wy163.Finder163ZhishuRToday;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.DecimalUtil;
import com.zqi.frame.util.JedisUtils;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;
import com.zqi.init.InItTool;
import com.zqi.primaryData.fileDataBase.FileDataBase;
import com.zqi.primaryData.fileDataBase.IFileDataBase;
import com.zqi.primaryData.fileDataBase.RHisFileDataBase;
import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.HQFinder;
import com.zqi.strategy.lib.DataMethod;
import com.zqi.strategy.period.PeriodFinder;
import com.zqi.unit.DateConverter;
import com.zqi.unit.DateUtil;
import com.zqi.unit.FileUtil;
import com.zqi.unit.SpringContextHelper;
import com.zqi.unit.UUIDGenerator;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(PrimaryDataController.class);
	
	HQFinder hqFinder;
	
	public HQFinder getHqFinder() {
		return hqFinder;
	}
	
	@Autowired
	public void setHqFinder(HQFinder hqFinder) {
		this.hqFinder = hqFinder;
	}
	@RequestMapping("/primaryDataMain")
	public String primaryMain(){
		
		return "primaryData/primaryDataMain";
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> primaryDataGridList(HttpServletRequest request){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		if(code==null||"".equals(code)){
			if(period==null||"".equals(period)){
				period = DateUtil.getDateNow();
			}
		}
		//Calendar nowCalendar = Calendar.getInstance();
		String nowYear = period.split("-")[0];
		Map<String, Object> monthMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = 'daytable_lastmonth' and TABLE_SCHEMA = 'zqi'");
		Long monthCount = (Long)monthMap.get("count");
		Map<String, Object> allMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = '"+nowYear+"_daytable_all' and TABLE_SCHEMA = 'zqi'");
		Long allCount = (Long)allMap.get("count");
		String dayDataSql = null;
		List<Map<String, Object>> dayDataList = null;
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		if(monthCount>0){
			dayDataSql = "select * from daytable_lastmonth where 1=1";
			if(code!=null&&!"".equals(code)){
				dayDataSql += " and code='"+code+"'";
			}
			if(period!=null&&!"".equals(period)){
				dayDataSql += " and period='"+period+"'";
			}
			pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
			dayDataList = pagedRequests.getList();
		}
		if(allCount>0){
			if(dayDataList==null||dayDataList.size()==0){
				dayDataSql = "select * from "+nowYear+"_daytable_all where 1=1";
				if(code!=null&&!"".equals(code)){
					dayDataSql += " and code='"+code+"'";
				}
				if(period!=null&&!"".equals(period)){
					dayDataSql += " and period='"+period+"'";
				}
				pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
			}
		}
		
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		
		return resultMap;
	}
	
	@RequestMapping("/primaryDataList")
	public String primaryDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "primaryData/primaryDataList";
	}
	
	public String findBlockInfo(){
		
		return "";
	}
	
	@ResponseBody
	@RequestMapping("/fillPrimaryData")
	public Map<String, Object> findHisDayData(String dateFrom,String dateTo,String fillType){

        if("today".equals(fillType)){
        	findTodayData();
		}else if("year".equals(fillType)){
			Calendar calendar = Calendar.getInstance();
			if(dateFrom!=null&&!"".equals(dateFrom)){
				DateConverter dateConverter = new DateConverter();
				Date dateObj = (Date)dateConverter.convert(Date.class, dateFrom);
				calendar.setTime(dateObj);
			}
			int year = calendar.get(Calendar.YEAR);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			dateFrom = DateUtil.convertDateToString(calendar.getTime());
			calendar.set(Calendar.YEAR, year+1);
			calendar.set(Calendar.DAY_OF_YEAR, -1);
			dateTo = DateUtil.convertDateToString(calendar.getTime());
			findRHisData(dateFrom,dateTo,""+year);
		}else if("date".equals(fillType)){
			findRHisData(dateFrom,dateTo,null);
		}
        setMessage("导入成功！");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/downLoadRHisData")
	public Map<String, Object> downLoadRHisData(HttpServletRequest request){
		String dateFrom = request.getParameter("dateFrom");
		String dateTo;
		String year = "";
		Calendar calendar = Calendar.getInstance();
		if(dateFrom!=null&&!"".equals(dateFrom)){
			DateConverter dateConverter = new DateConverter();
			Date dateObj = (Date)dateConverter.convert(Date.class, dateFrom);
			calendar.setTime(dateObj);
		}
		int y = calendar.get(Calendar.YEAR);
		year = ""+y;
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		dateFrom = DateUtil.convertDateToString(calendar.getTime());
		calendar.set(Calendar.YEAR, y+1);
		calendar.set(Calendar.DAY_OF_YEAR, 0);
		dateTo = DateUtil.convertDateToString(calendar.getTime());
		try {
			List<Map<String, Object>> gpList = findAGpDicList(null);
			//String delSql = "delete from daytable_all where period between '"+dateFrom+"' and '"+dateTo+"'";
			//zqiDao.excute(delSql);
			
			String dataCol = "period,code,name,type,settlement,open,high,low,close,volume,amount,changeprice,changepercent";
			HisContext hisContext = new HisContext();
			hisContext.setDateFrom(dateFrom);
			hisContext.setDateTo(dateTo);
			hisContext.setColArr(dataCol.split(","));
			hisContext.setYear(year);
			RHisFileDataBase yearDb = new RHisFileDataBase(year);
			yearDb.deleteDataBase();
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5); 
			for(Map<String, Object> gp : gpList){
				HisDataFindThread hisDataAddThread = new HisDataFindThread(gp, hisContext);
				fixedThreadPool.execute(hisDataAddThread);
			}
			fixedThreadPool.shutdown();
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
			setMessage("下载"+year+"年度日数据成功！");
		} catch (Exception e) {
			setMessage("下载"+year+"年度日数据失败！");
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/importRHisData")
	public Map<String, Object> importRHisData(HttpServletRequest request){
		String dateFrom = request.getParameter("dateFrom");
		String dateTo;
		String importYear = "";
		Calendar imporCalendar = Calendar.getInstance();
		if(dateFrom!=null&&!"".equals(dateFrom)){
			DateConverter dateConverter = new DateConverter();
			Date dateObj = (Date)dateConverter.convert(Date.class, dateFrom);
			imporCalendar.setTime(dateObj);
		}
		int importY = imporCalendar.get(Calendar.YEAR);
		importYear = ""+importY;
		imporCalendar.set(Calendar.DAY_OF_YEAR, 1);
		dateFrom = DateUtil.convertDateToString(imporCalendar.getTime());
		imporCalendar.set(Calendar.YEAR, importY+1);
		imporCalendar.set(Calendar.DAY_OF_YEAR, -1);
		dateTo = DateUtil.convertDateToString(imporCalendar.getTime());
		
		Calendar nowCalendar = Calendar.getInstance();
		int nowYear = nowCalendar.get(Calendar.YEAR);
		List<String> daytableList = new ArrayList<String>();
		String lastMontDateStr= null;
		try {
			List<Map<String, Object>> gpList = findAGpDicList(null);
			
			Map<String, Object> allMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = '"+importYear+"_daytable_all' and TABLE_SCHEMA = 'zqi'");
			Long count = (Long)allMap.get("count");
			if(count<1){
				String[] sqls = InItTool.createYearDayTable(gpList, importYear);
				zqiDao.bathUpdate(sqls);
			}else{
				String delSql = "delete from "+importYear+"_daytable_all where period between '"+dateFrom+"' and '"+dateTo+"'";
				zqiDao.excute(delSql);
				//当年的才导入daytable_lastmonth
				if(nowYear==importY){
					int nowMmonth = nowCalendar.get(Calendar.MONTH);
					nowCalendar.set(Calendar.MONTH,nowMmonth-1);
					Date lastMontDate = nowCalendar.getTime();
					lastMontDateStr = DateUtil.convertDateToString(lastMontDate);
					delSql = "delete from daytable_lastmonth where period between '"+dateFrom+"' and '"+dateTo+"'";
					zqiDao.excute(delSql);
					daytableList.add("daytable_lastmonth");
				}
			}
			
			
			RHisFileDataBase yearDb = new RHisFileDataBase(importYear);
			RHisFileDataBase tempDb = new RHisFileDataBase("temp");
			tempDb.deleteDataBase();
			
			for(Map<String, Object> gp : gpList){
				String code = gp.get("code").toString();
				String daytable = gp.get("daytable").toString();
				daytable = importYear+"_"+daytable;
				if(!daytableList.contains(daytable)){
					daytableList.add(daytable);
				}
				String content  = yearDb.readStr(code);
				if(lastMontDateStr!=null){
					String[] rows = content.split("\n");
					String lastMonthContent = "";
					for(String row : rows){
						String[] rowArr = row.split("\t");
						String period = rowArr[0];
						if(period.compareTo(lastMontDateStr)<=0){
							break;
						}else{
							lastMonthContent += row+"\n";
						}
					}
					tempDb.writeStr("daytable_lastmonth", lastMonthContent,1);
				}
				
				tempDb.writeStr(daytable, content,1);
			}
			List<String> loadList = new ArrayList<String>();
	        for(String daytable : daytableList){
	        	String loadDataSql = tempDb.getLoadFileSql(daytable);
	        	loadList.add(loadDataSql);
	        }
	        String[] loadSqls = loadList.toArray(new String[loadList.size()]);
	        zqiDao.bathUpdate(loadSqls);
	        
	        setMessage("导入"+importYear+"年度日数据成功！");
		} catch (Exception e) {
			setMessage("导入"+importYear+"年度日数据失败！");
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public void findTodayData(){
		String code = "",symbol = "",period = DateUtil.getDateNow();
		TestTimer importTodayDataTime = new TestTimer("导入今日数据");
		importTodayDataTime.begin();
		try {
			IFinderRToday iFinderRToday = new Finder163RToday();
			List<Map<String, Object>> todayList = iFinderRToday.findRToday();
			IFinderRToday iFinderZhishuRToday = new Finder163ZhishuRToday();
			List<Map<String, Object>> todayZhishuList = iFinderZhishuRToday.findRToday();
			//Map<String, Map<String, Object>> gpmap = findAGpDicMap();
			int count = 0;
			//Map<String, List<Map<String, Object>>> dayTableData = new HashMap<String, List<Map<String,Object>>>();
			String delSql = "delete from daytable_all where period='"+period+"'";
			zqiDao.excute(delSql);
			
			for(Map<String, Object> dayData :todayList){
				code = dayData.get("code").toString();
				period = dayData.get("period").toString();
				String type = "";
				//String close = dayData.get("close").toString();
				//Map<String, Object> gpdic;
				if(code.startsWith("6")){
					type = "0";
				}else{
					type = "1";
				}
				dayData.put("type", type);
				zqiDao.add(dayData, "daytable_lastmonth");
				/*gpdic = gpmap.get(symbol);
				if(gpdic!=null){
					String daytable = gpdic.get("daytable").toString();
					dayData.put("type", type);
					List<Map<String, Object>> dayTableList = dayTableData.get(daytable);
					if(dayTableList==null){
						dayTableList = new ArrayList<Map<String,Object>>();
						dayTableData.put(daytable,dayTableList);
					}
					dayTableList.add(dayData);
					//zqiDao.add(dayData, daytable);
				}*/
				count++;
			}
			for(Map<String, Object> dayZhishuData :todayZhishuList){
				code = dayZhishuData.get("code").toString();
				period = dayZhishuData.get("period").toString();
				String type = "";
				//String close = dayData.get("close").toString();
				Map<String, Object> gpdic;
				if(code.startsWith("0")){
					type = "2";
				}else{
					type = "3";
				}
				dayZhishuData.put("type", type);
				zqiDao.add(dayZhishuData, "daytable_lastmonth");
				/*gpdic = gpmap.get(symbol);
				if(gpdic!=null){
					String type = gpdic.get("type").toString();
					String daytable = gpdic.get("daytable").toString();
					dayZhishuData.put("type", type);
					List<Map<String, Object>> dayTableList = dayTableData.get(daytable);
					if(dayTableList==null){
						dayTableList = new ArrayList<Map<String,Object>>();
						dayTableData.put(daytable,dayTableList);
					}
					dayTableList.add(dayZhishuData);
					//zqiDao.add(dayZhishuData, daytable);
				}*/
				count++;
			}
			
			/*Set<String> daytableSet = dayTableData.keySet();
			List<Thread> threads = new ArrayList<Thread>();
			for(String daytable : daytableSet){
				List<Map<String, Object>> dayTableList = dayTableData.get(daytable);
				//DataAddThread dataAddThread = new DataAddThread(dayTableList, daytable);
				//Thread thread = new Thread(dataAddThread);
				//thread.start();
				//threads.add(thread);
			}
			for(Thread thread : threads){
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			
			long msTime = importTodayDataTime.doner();
			Map<String, Object> errorLog = new HashMap<String, Object>();
			errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
			errorLog.put("type", "findTodayData");
			errorLog.put("mainId", "findTodayDataSuccess");
			errorLog.put("assistId", count+":"+Math.floor(msTime/1000));
			errorLog.put("info", period+"导入今日数据成功！");
			errorLog.put("logDate", DateUtil.getDateTimeNow());
			zqiDao.add(errorLog,"_log");
			System.out.println("---------------"+period+":"+count+"------------------");
		} catch (Exception e) {
			Map<String, Object> errorLog = new HashMap<String, Object>();
			errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
			errorLog.put("type", "findTodayData");
			errorLog.put("mainId", "findTodayDataError");
			errorLog.put("assistId", symbol);
			errorLog.put("info", period+"导入今日数据错误！");
			errorLog.put("logDate", DateUtil.getDateTimeNow());
			zqiDao.add(errorLog,"_log");
			e.printStackTrace();
		}
		
	}
	
	public void findRHisData(String dateFrom, String dateTo ,String year){
		//String code = "";
		TestTimer importRHisDataTime = new TestTimer("导入今日数据");
		importRHisDataTime.begin();
		try {
			List<Map<String, Object>> gpList = findAGpDicList(null);
			Map<String, List<Map<String, Object>>> gpListMap = findAGpDicListMap();
			int count = 0;
			String delSql = "delete from daytable_all where period between '"+dateFrom+"' and '"+dateTo+"'";
			zqiDao.excute(delSql);
			
			String dataCol = "period,code,name,type,settlement,open,high,low,close,volume,amount,changeprice,changepercent";
			Set<String> daytableSet = gpListMap.keySet();
			HisContext hisContext = new HisContext();
			hisContext.setDateFrom(dateFrom);
			hisContext.setDateTo(dateTo);
			hisContext.setColArr(dataCol.split(","));
			if (year == null) {
				year = "temp";
			}
			hisContext.setYear(year);
			IFileDataBase yearDb = new RHisFileDataBase(year);
			yearDb.deleteDataBase();
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5); 
			for(Map<String, Object> gp : gpList){
				HisDataFindThread hisDataAddThread = new HisDataFindThread(gp, hisContext);
				fixedThreadPool.execute(hisDataAddThread);
			}
			/*for(String daytable : daytableSet){
				List<Map<String, Object>> gpList = gpListMap.get(daytable);
				HisDataFindThread hisDataAddThread = new HisDataFindThread(gpList, daytable, hisContext);
				fixedThreadPool.execute(hisDataAddThread);
			}*/
			fixedThreadPool.shutdown();
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
			String basePath = Tools.getResource("baseDir");
			/*List<String> daytableList = new ArrayList<String>();
			for(Map<String, Object> gp : gpList){
				String code = gp.get("code").toString();
				String daytable = gp.get("daytable").toString();
				IFileDataBase rhisFileDb = new RHisFileDataBase(year);
				String content = rhisFileDb.readStr(code);
				rhisFileDb.writeStr(daytable, content);
				daytableList.add(daytable);
			}
			
			String basePath = Tools.getResource("baseDir");
			String rHisDataDir = basePath+Tools.getResource("rhisDir");
			List<String> loadList = new ArrayList<String>();
	        for(String daytable : daytableList){
	        	String loadDataSql = "load data infile '"+rHisDataDir+daytable+".txt' into table "+daytable+"("+dataCol+");";
	        	loadList.add(loadDataSql);
	        }
	        String[] loadSqls = loadList.toArray(new String[loadList.size()]);
	        zqiDao.bathUpdate(loadSqls);*/
			
			StringBuilder stringBuilder = new StringBuilder();
	       /* Map<String, Integer> recordMap = hisContext.getRecordMap();
	        Set<String> recordSet = recordMap.keySet();
	        stringBuilder.append("------daytable记录数--------\n");
			for(String record : recordSet){
				count += recordMap.get(record);
				stringBuilder.append("[count]:"+record+":"+count+"\n");
			}
			stringBuilder.append("\n");*/
			Map<String,Map<String, String>> logMap = hisContext.getLog();
			
			Set<String> codeSet = logMap.keySet();
			int gpCount = 0;
			for(String gpcode : codeSet){
				stringBuilder.append("------"+gpcode+"--------\n");
				Map<String, String> gpLog = logMap.get(gpcode);
				String httpcount = gpLog.get("count");
				String timeoutcount = gpLog.get("timeoutcount");
				stringBuilder.append("[http次数]:"+httpcount+"\n");
				stringBuilder.append("[http超时次数]:"+timeoutcount+"\n");
				gpCount++;
			}
			stringBuilder.append("------total--------\n");
			stringBuilder.append("[更新股票数量]:"+gpCount+"\n");
			stringBuilder.append("[更新股票总记录数量]:"+count+"\n");
			String logDir = basePath+Tools.getResource("logDir");
			FileUtil.writeFile(stringBuilder.toString(), logDir+dateFrom+"-"+dateTo+"_log.txt");
			long msTime = importRHisDataTime.doner();
			Map<String, Object> errorLog = new HashMap<String, Object>();
			errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
			errorLog.put("type", "findRHisData");
			errorLog.put("mainId", "findRHisDataSuccess");
			errorLog.put("assistId", count+":"+Math.floor(msTime/1000));
			errorLog.put("info", dateFrom+" to "+dateTo+"导入历史日数据成功！");
			errorLog.put("logDate", DateUtil.getDateTimeNow());
			zqiDao.add(errorLog,"_log");
			System.out.println("---------------"+dateFrom+" to "+dateTo+":"+count+"------------------");
		} catch (Exception e) {
			Map<String, Object> errorLog = new HashMap<String, Object>();
			errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
			errorLog.put("type", "findRHisData");
			errorLog.put("mainId", "findRHisDataError");
			//errorLog.put("assistId", code);
			errorLog.put("info", dateFrom+" to "+dateTo+"导入历史日数据错误！");
			errorLog.put("logDate", DateUtil.getDateTimeNow());
			zqiDao.add(errorLog,"_log");
			e.printStackTrace();
		}
	}
	
/*	public void findTodayDataSina(){
		String[] hs_aKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
		int findNum=0;
		for(int page=1;page<=50;page++){
			String hs_aKeyUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22hq%22,%22hs_a%22,%22%22,0,"+page+",60]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  hs_aDataList = Tools.getHttpUrlMapSina(hs_aKey,hs_aKeyUrl,titleMap);
			findNum += hs_aDataList.size();
			String period = DateUtil.getDateNow();
			//String total = titleMap.get("count");
			for(Map<String, String> gpData : hs_aDataList){
				String symbol = gpData.get("symbol");
				try {
					String code = gpData.get("code");
					String name = gpData.get("name");
					String settlement = gpData.get("settlement");
					String open = gpData.get("open");
                    String high = gpData.get("high");
                    String low = gpData.get("low");
                    String close = gpData.get("trade");
                    String volume = gpData.get("volume");
                    String amount = gpData.get("amount");
                    Map<String, Object> todayMap = new HashMap<String, Object>();
                    todayMap.put("period", period);
                    todayMap.put("code", symbol);
                    todayMap.put("name", name);
                    todayMap.put("settlement", new BigDecimal(settlement));
                    todayMap.put("open", new BigDecimal(open));
                    todayMap.put("high", new BigDecimal(high));
                    todayMap.put("low", new BigDecimal(low));
                    todayMap.put("close", new BigDecimal(close));
                    todayMap.put("volume", new BigDecimal(volume));
                    todayMap.put("amount", new BigDecimal(amount));
                    String dicSql = "select daytable from d_gpdic where symbol='"+symbol+"'";
                    Map<String, Object> gpDic = zqiDao.findFirst(dicSql);
                    String daytable = "";
                    if(gpDic!=null){
                    	daytable = gpDic.get("daytable").toString();
                    	String delSql = "delete from "+daytable+" where code='"+symbol+"'";
                    	zqiDao.excute(delSql);
                    	zqiDao.add(todayMap, daytable);
                    }
				} catch (Exception e) {
					Map<String, Object> errorLog = new HashMap<String, Object>();
					errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
					errorLog.put("type", "findHisDayData");
					errorLog.put("mainId", "findTodayDataError");
					errorLog.put("assistId", symbol);
					errorLog.put("info", period+"导入日数据错误！");
					errorLog.put("logDate", DateUtil.getDateTimeNow());
					zqiDao.add(errorLog,"_log");
					e.printStackTrace();
				}
			}
			System.out.println(findNum);
		}
	}*/
	
	public List<String[]> findDayData(String symbol,String code,String year,String jidu){
		String str = "";
		List<String[]> dayList = new ArrayList<String[]>();
		Map<String, Object> errorLog = new HashMap<String, Object>();
    	errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
    	errorLog.put("type", "findHisDayData");
    	errorLog.put("mainId", "findJiduDataError");
    	errorLog.put("assistId", symbol);
    	errorLog.put("info", year+jidu+"导入日数据错误！");
    	errorLog.put("logDate", DateUtil.getDateTimeNow());
		//创建一个webclient
		try {
			WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
			java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF); 
			//htmlunit 对css和javascript的支持不好，所以请关闭之
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			//webClient.waitForBackgroundJavaScript(600*1000);  
			//webClient.setAjaxController(new NicelyResynchronizingAjaxController()); 
			//获取页面
			String url = Tools.getResource("dayHisDataUrl");
			url = url.replace("%code%", code);
			url = url.replace("%year%", year);
			url = url.replace("%jidu%", jidu);
			HtmlPage page;
			page = webClient.getPage(url);
			//webClient.waitForBackgroundJavaScript(1000*5); 
			//webClient.setJavaScriptTimeout(5000);  
//        //获取页面的TITLE
//        str = page.getTitleText();
//        System.out.println(str);
//        //获取页面的XML代码
//        str = page.asXml();
//        System.out.println(str);
//        //获取页面的文本
//        str = page.asText();
//        System.out.println(str);
//        str = page.asXml();
//        System.out.println(str);
			DomElement domElement = null;
			String elementId = Tools.getResource("dayHisTableId");
			if(elementId==null||"".equals(elementId)){
				String elementClass = Tools.getResource("dayHisTableClass");
				DomNodeList<DomElement> domList= page.getElementsByTagName("table");
				for(DomElement dom : domList){
					String domHtml = dom.asXml();
					if(domHtml.contains(elementClass)){
						domElement = dom;
						break;
					}
				}
				
			}else{
				domElement = page.getElementById("FundHoldSharesTable");
			}
			
			if(domElement!=null){
				str = domElement.asText();
				str = str.replaceAll("\r\n\t\r\n", " ").replaceAll("\r\n", "\t");
				String[] rowArr = str.split("\t");
				for(String row : rowArr){
				String[] colArr = row.split(" ");
					dayList.add(colArr);
				}
			}
			//System.out.println(domElement.asText());
			//关闭webclient
			webClient.closeAllWindows();
		} catch (Exception e) {
        	zqiDao.add(errorLog,"_log");
        	try {
        		System.out.println("wait to log");
				wait(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
            e.printStackTrace();
		}
        return dayList;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cacheFhData")
	public Map<String, Object> cacheFhData(HttpServletRequest request){
		Jedis jedis = JedisUtils.getResource();
		Pipeline pipeline = jedis.pipelined();
		
		List<Map<String, Object>> fhList = zqiDao.findAll("SELECT * from i_gpfh where fhYear not like '%(预*)'");
		for(Map<String, Object> fhMap : fhList){
			String code = fhMap.get("code").toString();
			Object fhObj = fhMap.get("fh");
			Object zzObj = fhMap.get("zz");
			Object sgObj = fhMap.get("sg");
			Object pgObj = fhMap.get("pg");
			Object pgPriceObj = fhMap.get("pgPrice");
			
			if(fhObj!=null){
				String cqDate = fhMap.get("cqDate").toString();
				BigDecimal fh = (BigDecimal)fhObj;
				JedisUtils.setObject(pipeline,"fh:"+code+":"+cqDate, fh, 0);
			}
			
			if(zzObj!=null){
				String cqDate = fhMap.get("zzdz").toString();
				BigDecimal zz = (BigDecimal)zzObj;
				JedisUtils.setObject(pipeline,"zz:"+code+":"+cqDate, zz, 0);
			}

			if(sgObj!=null){
				String cqDate = fhMap.get("sgdz").toString();
				BigDecimal sg = (BigDecimal)sgObj;
				JedisUtils.setObject(pipeline,"sg:"+code+":"+cqDate, sg, 0);
			}
			
			if(pgObj!=null){
				String cqDate = fhMap.get("pgdz").toString();
				BigDecimal pg = (BigDecimal)pgObj;
				JedisUtils.setObject(pipeline,"pg:"+code+":"+cqDate, pg, 0);
			}
			
			if(pgPriceObj!=null){
				String cqDate = fhMap.get("pgPrice").toString();
				BigDecimal pgPrice = (BigDecimal)pgPriceObj;
				JedisUtils.setObject(pipeline,"pgPrice:"+code+":"+cqDate, pgPrice, 0);
			}
		}
		pipeline.sync();
		JedisUtils.returnResource(jedis);
		this.setMessage("缓存分红数据成功！");
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/cachePeriodHQData")
	public Map<String, Object> cachePeriodHQDatas(HttpServletRequest request){
		String year = request.getParameter("year");
		String PeriodHQLog = JedisUtils.get("PeriodHQLog");
		if(StringUtils.isEmpty(PeriodHQLog)){
			PeriodHQLog = year;
		}else{
			if(!PeriodHQLog.contains(year)){
				PeriodHQLog += ","+year;
			}
		}
		//JedisUtils.set("PeriodHQLog", PeriodHQLog, 0);
		//Jedis jedis = JedisUtils.getResource();
		//Pipeline pipeline = jedis.pipelined();
		String start = year+"-01-01";
		String end = year+"-12-31";
		List<String> periodList;
		try {
			FileDataBase periodDataDb = new FileDataBase("temp/periodHQ");
			periodDataDb.deleteDataBase();
			Gson gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd") .create();
			periodList = PeriodFinder.getDayPeriod(start, end);
			for(String period : periodList){
				//JedisUtils.delObject(period);
				List<Map<String, Object>> dataList = hqFinder.getGpHq(year , "all" , period, new HashMap());
				if(dataList!=null&&dataList.size()>0){
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> dataTemp : dataList){
						HQDataHandler.dealRHQData(dataTemp);
						dataTemp.remove("d");
						list.add(dataTemp);
					}
					list.addAll(dataList);
					periodDataDb.writeStr("periodHQ", period+"	"+gson.toJson(list)+"\n", 1);
					//JedisUtils.setObjectList(period, list, 0);
				}
				System.out.println(period);
			}
			Map<String, Object> allMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = '"+year+"_daytable_period' and TABLE_SCHEMA = 'zqi'");
			Long count = (Long)allMap.get("count");
			if(count<1){
				String createSql = "create table "+year+"_daytable_period(`period` date NOT NULL,`D` longtext,PRIMARY KEY (`period`),KEY `"+year+"_daytable_period` (`period`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
				zqiDao.excute(createSql);
			}else{
				this.hqFinder.getZqiDao().excute("delete from "+year+"_daytable_period");
			}
			String periodDataCols = "period,D";
			String periodDataSql = "load data infile '"+periodDataDb.getFilePath("periodHQ")+"' into table "+year+"_daytable_period("+periodDataCols+");";
			this.hqFinder.getZqiDao().excute(periodDataSql);
			//System.out.println(11);
			/*TestTimer ttt = new TestTimer("get");
			ttt.begin();
			
			Map<String, Object> aa = zqiDao.findFirst("select * from periodHQ where period='2016-01-05'");
			String aaa = aa.get("D").toString();
			List<Map<String, Object>> ms = gson.fromJson(aaa, List.class);
			for(Map<String, Object> m :ms){
				String code = m.get("code").toString();
			}
			ttt.done();*/
			//pipeline.sync();
			//JedisUtils.returnResource(jedis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("{} 年度缓存数据",year);
		this.setMessage("缓存"+year+"年度期间数据成功！");
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cachePeriodHQData1")
	public Map<String, Object> cachePeriodHQDatas1(HttpServletRequest request){
		String year = request.getParameter("year");
		String PeriodHQLog = JedisUtils.get("PeriodHQLog");
		if(StringUtils.isEmpty(PeriodHQLog)){
			PeriodHQLog = year;
		}else{
			if(!PeriodHQLog.contains(year)){
				PeriodHQLog += ","+year;
			}
		}
		JedisUtils.set("PeriodHQLog", PeriodHQLog, 0);
		Jedis jedis = JedisUtils.getResource();
		Pipeline pipeline = jedis.pipelined();
		String start = year+"-01-01";
		String end = year+"-12-31";
		List<String> periodList;
		try {
			periodList = PeriodFinder.getDayPeriod(start, end);
			for(String period : periodList){
				List<Map<String, Object>> dataList = hqFinder.getGpHq(year , "all" , period, new HashMap());
				if(dataList!=null&&dataList.size()>0){
					List<Object> list = new ArrayList<Object>();
					list.addAll(dataList);
					JedisUtils.setObjectList(period, list, 0);
				}
			}
			pipeline.sync();
			JedisUtils.returnResource(jedis);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("{} 年度缓存数据",year);
		this.setMessage("缓存"+year+"年度期间数据成功！");
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cacheRHQData")
	public Map<String, Object> cacheRHQData(HttpServletRequest request){
		String year = request.getParameter("year");
		int yearInt = Integer.parseInt(year);
		int nextYear = yearInt+1;
		String RHQLog = JedisUtils.get("RHQLog");
		if(StringUtils.isEmpty(RHQLog)){
			RHQLog = year;
		}else{
			if(!RHQLog.contains(year)){
				RHQLog += ","+year;
			}
		}
		JedisUtils.set("RHQLog", RHQLog, 0);
		Jedis jedis = JedisUtils.getResource();
		Pipeline pipeline = jedis.pipelined();
		
		Map<String, Object> nextYearMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = '"+nextYear+"_daytable0_1' and TABLE_SCHEMA = 'zqi'");
		Long count = (Long)nextYearMap.get("count");
		
		List<String> periodList = new ArrayList<String>();
		String szSql = null;
		String codeSql =null;
		if(count<1){
			szSql = "SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_daytable0_1 where code='0000001' order by period asc";
			codeSql = "SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_%daytable% where code='%code%' order by period asc";
		}else{
			szSql = "SELECT * from (SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_daytable0_1 where code='0000001'"
					+"UNION SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+nextYear+"_daytable0_1 where code='0000001') t order by t.period asc";
			codeSql = "SELECT * from (SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_%daytable% where code='%code%'"
					+"UNION SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+nextYear+"_%daytable% where code='%code%') t order by t.period asc";
		}
		List<Map<String, Object>> szlists = zqiDao.findAll(szSql);
		for(Map<String, Object> data : szlists){
			//String code = gp.get("code").toString();
			String period = data.get("period").toString();
			JedisUtils.set("period:"+period, "1", 0);
			periodList.add(period);
		}
		
		List<Object> dics = hqFinder.getGpDicData();
		if(dics==null){
			dics = zqiDao.findAll("SELECT * from d_gpdic where type in (0,1) order by code asc");
			JedisUtils.setObjectList("gpDic", dics, 0);
		}
		
		dics.addAll(zqiDao.findAll("SELECT * from d_gpdic where code in ('0000001','0000300','1399001','1399005','1399006') order by code asc"));
		
		//String[] indexArr = {"{name:emaData,param:'\\{col:[\\'close\\'],value:[5,10,20,30,60,250]\\}'}"};
		int cc = 0;
		for(Object gpObj : dics){
			Map<String, Object> gp = (Map<String, Object>)gpObj;
			String code = gp.get("code").toString();
			String daytable = gp.get("daytable").toString();
			String codeSqlTemp = codeSql.replace("%code%", code);
			codeSqlTemp = codeSqlTemp.replace("%daytable%", daytable);
			List<Map<String, Object>> lists = zqiDao.findAll(codeSqlTemp);
			cc += lists.size();
			if(lists.isEmpty()){
				logger.info("{} RHQ数据为零", code);
				continue;
			}
			Map<String, Object> perData = null;
			String perPeriod = null;
			Map<String, Object> dataTemp = null ;
			String periodTemp = null;
			int dataIndex = 0;
			
			for(int i=0;i< periodList.size();i++){
				String date = periodList.get(i);
				String periodYear = date.split("-")[0];
				if((""+nextYear).equals(periodYear)){
					Map<String, Object> rData = hqFinder.getRHQData(code, date);
					if(rData!=null){
						break;
					}
				}
				if(dataTemp==null){
					if(dataIndex<lists.size()){
						dataTemp = lists.get(dataIndex);
						periodTemp = dataTemp.get("period").toString();
					}
					
				}
				
				if(date.equals(periodTemp)){
					BigDecimal open =(BigDecimal) dataTemp.get("open");
					BigDecimal close =(BigDecimal) dataTemp.get("close");
					if(perData!=null){
						dataTemp.put("prePeriod", perPeriod);
					}
					JedisUtils.setObject(pipeline, code+":"+date, dataTemp, 0);
					if(open.doubleValue()!=0&&close.doubleValue()!=0){
						perData = dataTemp;
						perPeriod = perData.get("period").toString();
					}
					dataTemp = null;
					dataIndex++;
				}else{//date只能比periodTemp小
					if(perData!=null){
						Map<String, Object> zeroData = new HashMap<String, Object>();
						BigDecimal settlement = (BigDecimal)perData.get("close");
						zeroData.put("prePeriod", perPeriod);
						zeroData.put("settlement",settlement);
						zeroData.put("period",date);
						zeroData.put("open",new BigDecimal(0));
						zeroData.put("high",new BigDecimal(0));
						zeroData.put("low",new BigDecimal(0));
						zeroData.put("close",new BigDecimal(0));
						zeroData.put("volume",new BigDecimal(0));
						zeroData.put("amount",new BigDecimal(0));
						zeroData.put("changeprice",new BigDecimal(0));
						zeroData.put("changepercent",new BigDecimal(0));
						zeroData.put("isNew","0");
						Map<String, Object> fhMap = zqiDao.findFirst("SELECT * FROM i_gpfh where code='"+code+"' and (cqdate='"+date+"' or zzdz='"+date+"' or sgdz='"+date+"')");
						if(fhMap.isEmpty()){
							zeroData.put("isFh","0");
						}else{
							zeroData.put("isFh","1");
							logger.info("停牌分红：{}-{}",code , date);
						}
						JedisUtils.setObject(pipeline, code+":"+date, zeroData, 0);
						logger.info("补充数据：{}-{}",code , date);
					}
				}
			}
		}
		pipeline.sync();
		JedisUtils.returnResource(jedis);
		System.out.println(cc);
		logger.info("{} 年度缓存数据 {}",year,cc);
		this.setMessage("缓存"+year+"年度日数据成功！");
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/perRHQ")
	public Map<String, Object> perRHQ(HttpServletRequest request){
		String rightCol = request.getParameter("rightCol");
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			String code = gp.get("code").toString();
			System.out.println(code);
			
			List<Map<String, Object>> gpFhList = zqiDao.findAll("select * from i_gpFh where code='"+code+"' and fhYear not like '%(预*)' order by fhYear desc");
			int i = 0 ;
			for(Map<String, Object> fhMap : gpFhList){
				eachFh(gp,fhMap,rightCol,i);
				i++;
			}
		}
		return resultMap;
	}
	
	public void eachFh(Map<String, Object> gp,Map<String, Object> fhBean,String col , int fhTime){
		String code = gp.get("code").toString();
		String daytable = gp.get("daytable").toString();
		
		//Map<String, String> fhMap = new HashMap<String, String>();
		//String fhcode = fhBean.get("code").toString();
		//String ggDate = fhBean.get("ggDate").toString();
		Object fhObj = fhBean.get("fh");
		Object zzObj = fhBean.get("zz");
		Object sgObj = fhBean.get("sg");
		Object pgObj = fhBean.get("pg");
		Object pgpriceObj = fhBean.get("pgprice");
		String cqDate = null;
		
		BigDecimal fh = new BigDecimal(0);
		BigDecimal zz = new BigDecimal(0);
		BigDecimal sg = new BigDecimal(0);
		BigDecimal pg = new BigDecimal(0);
		BigDecimal pgprice = new BigDecimal(0);
		
		if(fhObj!=null){
			cqDate = fhBean.get("cqDate").toString();
			fh = (BigDecimal)fhObj;
			//String cqDateObj = fhBean.get("cqDate").toString();
			//fhMap.put(fhcode+"_fh_"+cqDateObj,fhObj.toString());
		}
		
		if(zzObj!=null){
			cqDate = fhBean.get("zzdz").toString();
			zz = (BigDecimal)zzObj;
			/*Object cqDateObj = fhBean.get("zzdz");
			String cqStr = null;
			if(cqDateObj != null){
				cqStr = cqDateObj.toString();
			}else{
				cqDateObj = fhBean.get("zzss");
				if(cqDateObj!=null){
					cqStr = cqDateObj.toString();
				}
			}
			if(cqStr!=null){
				fhMap.put(fhcode+"_zz_"+cqStr,zzObj.toString());
			}*/
		}

		if(sgObj!=null){
			cqDate = fhBean.get("sgdz").toString();
			sg = (BigDecimal)sgObj;
			/*Object cqDateObj = fhBean.get("sgdz");
			String cqStr = null;
			if(cqDateObj != null){
				cqStr = cqDateObj.toString();
			}else{
				cqDateObj = fhBean.get("sgss");
				if(cqDateObj!=null){
					cqStr = cqDateObj.toString();
				}
			}
			if(cqStr!=null){
				fhMap.put(fhcode+"_ss_"+cqStr,sgObj.toString());
			}*/
		}
		if(pgObj!=null){
			cqDate = fhBean.get("pgdz").toString();
			pg = (BigDecimal)pgObj;
			pgprice = (BigDecimal)pgpriceObj;
			/*String cqDateObj = fhBean.get("pgdz").toString();
			String cqStr = null;
			if(cqDateObj != null){
				cqStr = cqDateObj.toString();
			}else{
				pgpriceObj = fhBean.get("pgss");
				if(pgpriceObj!=null){
					cqStr = cqDateObj.toString();
				}
			}
			if(cqStr!=null){
				fhMap.put(fhcode+"_pg_"+cqStr,pgObj.toString()+"_"+pgpriceObj.toString());
			}*/
		}
		/*Date cqDate1 = null;
		try {
			cqDate1 = DateUtil.convertStringToDate(cqDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		String yeasSql = "select info from _log where id='RHQYears'";
		Map<String, Object> yMap = zqiDao.findFirst(yeasSql);
		String ys = yMap.get("info").toString();
		String[] yArr = ys.split(",");
		
		String fhYeasSql = "select info from _log where id='FHYears'";
		Map<String, Object> fhyMap = zqiDao.findFirst(fhYeasSql);
		String fhys = fhyMap.get("info").toString();
		//String[] fhyArr = fhys.split(",");
		
		List<String> updateList = new ArrayList<String>();
		for(int yi=yArr.length-1;yi>=0;yi--){
			String y = yArr[yi];
			String dataSql = null;
			if(fhys==null){
				fhys = y;
			}else if(fhys.contains(y)){
				continue;
			}else{
				fhys = y + "," + fhys;
			}
			if(fhTime>0){
				dataSql = "select * from "+y+"_"+daytable+" where code='"+code+"' and period<"+cqDate+" order by period desc";
			}else{
				dataSql = "select * from "+y+"_"+daytable+" where code='"+code+"' order by period desc";
			}
			List<Map<String, Object>> dayDataList = zqiDao.findAll(dataSql);
			Gson gson = new Gson();
			for(int i=0;i<dayDataList.size();i++){
				Map<String, Object> dayData = dayDataList.get(i);
				String period = dayData.get("period").toString();
				BigDecimal colData = null;
				String d = null;
				Object dObj = dayData.get("d");
				if(dObj!=null){
					d = dObj.toString();
				}
				
				Map<String, Object> dMap = null;
				if(!StringUtils.isEmpty(d)){
					dMap = gson.fromJson(d, Map.class);
				}else{
					dMap = new HashMap<String, Object>();
				}
				
				if(fhTime>0){
					colData = (BigDecimal)dayData.get(col);
				}else{
					colData = (BigDecimal)dayData.get(col);
				}
				
				//[(复权前价格-现金红利)＋配(新)股价格×流通股份变动比例]÷(1＋流通股份变动比例)
			/*	Date dataPeriod = null;
				try {
					dataPeriod = DateUtil.convertStringToDate(period);
				} catch (ParseException e) {
					e.printStackTrace();
				}*/
				//int cqBefore = dataPeriod.compareTo(cqDate1);
				if(period.compareTo(cqDate)<0){
					if(colData.compareTo(new BigDecimal(0))!=0){
						colData = (colData.subtract(fh.divide(new BigDecimal(10))).add((pgprice.multiply(pg.divide(new BigDecimal(10)))))).divide(zz.add(sg).divide(new BigDecimal(10)).add(new BigDecimal(1)),10,BigDecimal.ROUND_HALF_DOWN).setScale(3, BigDecimal.ROUND_HALF_UP);
					}
				}
				dMap.put(col+"_p",colData.doubleValue());
				String dd = gson.toJson(dMap);
				String updateSql = "update "+y+"_"+daytable+" set d='"+dd+"' where code='"+code+"' and period='"+period+"'";
				updateList.add(updateSql);
			}
			
		}
		
		String[] updateArr = updateList.toArray(new String[updateList.size()]);
		if(updateArr.length>0){
			zqiDao.bathUpdate(updateArr);
		}
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/computeIndexData")
	public Map<String, Object> computeIndexData(HttpServletRequest request){
		String year = request.getParameter("year");
		String index = request.getParameter("index");
		int yearInt = Integer.parseInt(year);
		int nextYear = yearInt+1;
		
		/*String szSql = "SELECT period from "+year+"_daytable0_1 where code='0000001' order by period asc";
		List<Map<String, Object>> periodList = zqiDao.findAll(szSql);
		String szSql2 = szSql = "SELECT period from "+nextYear+"_daytable0_1 where code='0000001' order by period asc";
		List<Map<String, Object>> periodList2 = zqiDao.findAll(szSql);*/
		
		Gson gson = new Gson();
		Map<String, Object> indexMap = gson.fromJson(index, Map.class);
		String indexName = indexMap.get("name").toString();
		Map<String, Object> indexParam = (Map<String, Object>)indexMap.get("param");
		DataMethod dataMethod = (DataMethod)SpringContextHelper.getBean(indexName+"Data");
		dataMethod.setParam(indexParam);
		
		List<Map<String, Object>> dics = zqiDao.findAll("select * from d_gpdic where type in (0,1) order by code asc");
		for(Map<String, Object> gp : dics){
			List<String> updateList = new ArrayList<String>();
			String code = gp.get("code").toString();
			String daytable = gp.get("daytable").toString();
			//String daytable = gp.get("daytable").toString();
			//String codeSqlTemp = codeSql.replace("%code%", code);
			//codeSqlTemp = codeSqlTemp.replace("%daytable%", daytable);
			//List<Map<String, Object>> lists = zqiDao.findAll(codeSqlTemp);
			String dataSql = "SELECT * FROM (SELECT * from "+year+"_"+daytable+" where code='"+code+"' and close<>0 union SELECT * from "+nextYear+"_"+daytable+" where code='"+code+"' and close<>0 ) c order by c.period asc";
			List<Map<String, Object>> dataList = zqiDao.findAll(dataSql);
			//dataMethod.setBeginIndex(methodBeginIndex);
			dataMethod.execute(dataList);
			String[] colArr = dataMethod.getColArr();
			Double[] valueArr = dataMethod.getValueArr();
			for(Map<String, Object> data : dataList){
				String period = data.get("period").toString();
				String yy = period.substring(0, 4);
				String d = null;
				Object dObj = data.get("d");
				if(dObj!=null){
					d = dObj.toString();
				}
				
				Map<String, Object> dMap = null;
				if(!StringUtils.isEmpty(d)){
					dMap = gson.fromJson(d, Map.class);
				}else{
					dMap = new HashMap<String, Object>();
				}
				for(String col : colArr){
					for(Double v : valueArr){
						String c = col + "_" + indexName + "_" +v.intValue();
						Double a = (Double)data.get(c);
						dMap.put(c,a);
					}
				}
				String dd = gson.toJson(dMap);
				String updateSql = "update "+yy+"_"+daytable+" set d='"+dd+"' where code='"+code+"' and period='"+period+"'";
				updateList.add(updateSql);
			}
			
			String[] updateArr = updateList.toArray(new String[updateList.size()]);
			if(updateArr.length>0){
				zqiDao.bathUpdate(updateArr);
			}
		}
		this.setMessage("计算"+year+"年度"+indexName+"指标成功！");
		
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cacheIndexData1")
	public Map<String, Object> cacheIndexData1(HttpServletRequest request){
		String year = request.getParameter("year");
		String index = request.getParameter("index");
		int yearInt = Integer.parseInt(year);
		int nextYear = yearInt+1;
		
		Jedis jedis = JedisUtils.getResource();
		Pipeline pipeline = jedis.pipelined();
		
		Set<String> periodSet = new TreeSet<String>();
		Set<String> yearperiod = jedis.keys("period:"+year+"*");
		periodSet.addAll(yearperiod);
		Set<String> nextYearperiod = jedis.keys("period:"+nextYear+"*");
		periodSet.addAll(nextYearperiod);
		
		/*Map<String, Object> nextYearMap = zqiDao.findFirst("select count(*) count from information_schema.TABLES where table_name = '"+nextYear+"_daytable0_1' and TABLE_SCHEMA = 'zqi'");
		Long count = (Long)nextYearMap.get("count");
		
		String codeSql =null;
		if(count<1){
			codeSql = "SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_%daytable% where code='%code%' order by period asc";
		}else{
			codeSql = "SELECT * from (SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_%daytable% where code='%code%'"
					+"UNION SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+nextYear+"_%daytable% where code='%code%') t order by t.period asc";
		}*/
		
		Gson gson = new Gson();
		Map<String, Object> indexMap = gson.fromJson(index, Map.class);
		String indexName = indexMap.get("name").toString();
		String RIndexLog = JedisUtils.get("RIndexLog_"+indexName);
		if(StringUtils.isEmpty(RIndexLog)){
			RIndexLog = year;
		}else{
			if(!RIndexLog.contains(year)){
				RIndexLog += ","+year;
			}
		}
		JedisUtils.set("RIndexLog_"+indexName, RIndexLog, 0);
		Map<String, Object> indexParam = (Map<String, Object>)indexMap.get("param");
		DataMethod dataMethod = (DataMethod)SpringContextHelper.getBean(indexName+"Data");
		dataMethod.setParam(indexParam);
		
		List<Object> dics = hqFinder.getGpDicData();
		//List<Object> dics = new ArrayList<Object>();
		//dics.add(hqFinder.getGpDic("603818"));
		
		
		for(Object gpObj : dics){
			Map<String, Object> gp = (Map<String, Object>)gpObj;
			String code = gp.get("code").toString();
			//String daytable = gp.get("daytable").toString();
			//String codeSqlTemp = codeSql.replace("%code%", code);
			//codeSqlTemp = codeSqlTemp.replace("%daytable%", daytable);
			//List<Map<String, Object>> lists = zqiDao.findAll(codeSqlTemp);
			List<Map<String, Object>> listsTemp = new ArrayList<Map<String,Object>>();
			int methodBeginIndex = 0 , i=0;
			for(String period : periodSet){
				period = period.replace("period:","");
				Map<String, Object> data = hqFinder.getRHQData(code, period);
				if(data==null){
					continue;
				}
				//String period = data.get("period").toString();
				BigDecimal open = (BigDecimal)data.get("open");
				BigDecimal close = (BigDecimal)data.get("close");
				String isFh = data.get("isFh").toString();
				if("1".equals(isFh)){
					if("603818".equals(code)){
						System.out.println();
					}
					FileUtil.writeFile(code+"\n", "D://zqi/logs/qq.txt");
					dataMethod.setBeginIndex(methodBeginIndex);
					dataMethod.execute(listsTemp);
					methodBeginIndex = i;
					BigDecimal fh = hqFinder.getFhData(code, period)==null?new BigDecimal(0):hqFinder.getFhData(code, period);
					BigDecimal zz = hqFinder.getZzData(code, period)==null?new BigDecimal(0):hqFinder.getZzData(code, period);
					BigDecimal sg = hqFinder.getSgData(code, period)==null?new BigDecimal(0):hqFinder.getSgData(code, period);
					BigDecimal pg = hqFinder.getPgData(code, period)==null?new BigDecimal(0):hqFinder.getPgData(code, period);
					BigDecimal pgprice = hqFinder.getPgPriceData(code, period)==null?new BigDecimal(0):hqFinder.getPgPriceData(code, period);
					for(Map<String, Object> d : listsTemp){
						BigDecimal d_close = (BigDecimal)d.get("close_p");
						double prePrice = (d_close.doubleValue()-(fh.doubleValue()/10)+pgprice.doubleValue()*(pg.doubleValue()/10))/((zz.doubleValue()+sg.doubleValue())/10+1);
						prePrice = DecimalUtil.scale(prePrice);
						d.put("close_p", new BigDecimal(prePrice));
					}
				}
				
				if(open.compareTo(new BigDecimal(0))==0||close.compareTo(new BigDecimal(0))==0){
					continue;
				}
				data.put("close_p", close);
				listsTemp.add(data);
				i++;
			}
			if(methodBeginIndex<i){
				if("600684".equals(code)){
					System.out.println();
				}
				FileUtil.writeFile(code+"\n", "D://zqi/logs/qq.txt");
				dataMethod.setBeginIndex(methodBeginIndex);
				dataMethod.execute(listsTemp);
			}
			for(Map<String, Object> data : listsTemp){
				String date = data.get("period").toString();
				JedisUtils.setObject(pipeline, code+":"+date, data, 0);
			}
		}
		
		pipeline.sync();
		JedisUtils.returnResource(jedis);
		this.setMessage("缓存"+year+"年度"+indexName+"指标成功！");
		return resultMap;
	}
	
	public static void main(String[] args) {
		
		/*String[] gnbkKey = {"name","code","number","count","volume","amount","trade","changeprice","changepercent","symbol","sname","strade","schangeprice","schangepercent"};
		String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bknode%22,%22gainianbankuai%22,%22%22,0]]&callback=FDC_DC.theTableData";
		List<Map<String, String>>  gnbkDataList = getHttpUrlMap(gnbkKey,url);
		DBHelper dicDb = new DBHelper();
		List<String> updateSqlList = new ArrayList<String>();
		for(Map<String, String> gnbkData :gnbkDataList){
			String code = gnbkData.get("code");
			String bkName = gnbkData.get("name");
			String[] gnbkgpKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
			String gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bkshy_node%22,%22"+code+"%22,%22%22,0,1,40]]&callback=FDC_DC.theTableData";
			List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl);
			for(Map<String, String> gnbkgpData : gnbkgpDataList){
				String symbol = gnbkgpData.get("symbol");
				//String name = gnbkgpData.get("name");
				String updateGnbkSql = "update d_gpdic set b_gn='"+bkName+"' where code='"+symbol.toUpperCase()+"'";
				updateSqlList.add(updateGnbkSql);
				dicDb.addBatchSql(updateGnbkSql);
			}
		}
		try {
			dicDb.st.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*PrimaryDataController primaryDataController = new PrimaryDataController();
		String sql = "http://quotes.money.163.com/trade/lsjysj_600000.html#06f01";
		primaryDataController.findDayData("600000","600000","2016","01");*/
		//String url = "https://xueqiu.com/stock/f10/bonus.json?symbol=SZ000426&page=1&size=50";
		
		/*Calendar cd = Calendar.getInstance();
		int month = cd.get(Calendar.MONTH);
		cd.set(Calendar.MONTH,month-10);
		Date date = cd.getTime();
		System.out.println("2015-08-12".compareTo("2015-09-12"));*/
		//Gson gson = new Gson();
		//gson.fromJson("{name:'limitData',param:{col:'close',value:[5,10,20,60,250]}}", Map.class);
		
		double prePrice = (8.57-(0/10)+0*(0/10))/((0+0)/10+1);
		System.err.println(prePrice);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Map<String,String>> getHttpUrlMap(String[] keys,String url){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			/*Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
			System.out.println(key + "--->" + map.get(key));
			}*/
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
			connection.getInputStream(),"GBK"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
			    if (in != null) {
			        in.close();
			    }
			} catch (Exception e2) {
			    e2.printStackTrace();
			}
		}
		result = result.substring(result.indexOf("theTableData")+13, result.length()-1);
		JSONArray rsArr = JSONArray.fromObject(result);
		JSONObject rsObject = (JSONObject) rsArr.get(0);
		JSONArray items = (JSONArray) rsObject.get("items");
		Iterator<JSONArray> itemIt = items.iterator();
		while (itemIt.hasNext()) {
			JSONArray item = itemIt.next();
			Iterator<JSONArray> propertyIt = item.iterator();
			int i=0;
			Map<String, String> data = new HashMap<String, String>();
			while (propertyIt.hasNext()) {
				Object property = propertyIt.next();
				data.put(keys[i], property.toString());
				i++;
			}
			dataList.add(data);
		}
			return dataList;
	}
	
	@RequestMapping("fhDataList")
	public String fhDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "primaryData/fhDataList";
	}
	
	@ResponseBody
	@RequestMapping("/fhDataGridList")
	public Map<String, Object> fhDataGridList(HttpServletRequest request){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		String dayDataSql = "select * from i_gpFh where 1=1 ";
		if(code!=null&&!"".equals(code)){
			dayDataSql += " code='"+code+"'";
		}
		
		if(period!=null&&!"".equals(period)){
			dayDataSql += " and cqDate='"+period+"'";
		}
		pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@RequestMapping("redisDataList")
	public String redisDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "primaryData/redisDataList";
	}
	
	@ResponseBody
	@RequestMapping("/showRedisData")
	public Map<String, Object> showRedisData(HttpServletRequest request){
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String code = request.getParameter("gpCode");
		String redisContent = "";
		Gson gson = new Gson();
		if(StringUtils.isEmpty(endDate)){
			Map<String, Object> data = (Map<String, Object>)JedisUtils.getObject(code+":"+beginDate);
			redisContent += gson.toJson(data);
		}else{
			//String
			List<String> periodList = null; 
			try {
				periodList = PeriodFinder.getDayPeriod(beginDate, endDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(String period : periodList){
				Map<String, Object> data = (Map<String, Object>)JedisUtils.getObject(code+":"+period);
				if(data!=null){
					redisContent += gson.toJson(data);
				}
			}
		}
		resultMap.put("rs", redisContent);
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/importFhData")
	public Map<String, Object> importFhData(HttpServletRequest request){
		String year = request.getParameter("year");
		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("year", year);
		IFinderFh iFinderFh = new Finder163Fh();
		List<Map<String, Object>> fhDataList = iFinderFh.findFhInfo(infoMap);
		String basePath = Tools.getResource("baseDir");
		String info = basePath+Tools.getResource("info");
		File file = new File(info+"i_gpFh.txt");
		file.deleteOnExit();
		String deleteSql = "delete from i_gpFh where fhYear like '"+year+"%'";
		zqiDao.excute(deleteSql);
		StringBuilder stringBuilder = new StringBuilder();
		String dataCol = "code,name,fhYear,ggDate,djDate,cqDate,fh,sg,zz,sgss,zzss,sgdz,zzdz,zzdz,pgss,pgdz,pg,pgprice";
		String[] colArr = dataCol.split(",");
		for(Map<String, Object> fhData : fhDataList){
			String dataLine = "";
			String txt = "";
			for(String col : colArr){
				Object value = fhData.get(col);
				String v = "\\N";
				if(value!=null){
					v = value.toString();
					if("--".equals(v)){
						v = "\\N";
					}
				}
				if("fh".equals(col)&&!"\\N".equals(v)){
					txt += "派"+v;
				}else if("sg".equals(col)&&!"\\N".equals(v)){
					txt += "送"+v;
				}else if("zz".equals(col)&&!"\\N".equals(v)){
					txt += "转"+v;
				}
				dataLine += v+"\t";
			}
			dataLine += txt;
			stringBuilder.append(dataLine+"\n");
		}
		File fhTxt = new File(info+"i_gpFh.txt");
		if(fhTxt.exists()){
			fhTxt.delete();
		}
		FileUtil.writeFile(stringBuilder.toString(), info+"i_gpFh.txt");
		String loadDataSql = "load data infile '"+info+"i_gpFh.txt' into table i_gpFh("+dataCol+",txt"+");";
		zqiDao.excute(loadDataSql);
		System.out.println("------"+year+"分红信息导入成功--------");
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/markFhToRHQ")
	public Map<String, Object> markFhToRHQ(HttpServletRequest request){
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
		for(Map<String, Object> gp : gpDicList){
			dayDataFq(gp);
		}
		setMessage("更新日数据分红状态成功！");
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	public void dayDataFq(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String daytable = gp.get("daytable").toString();
		//System.out.println(code);
		
		List<Map<String, Object>> gpFhList = zqiDao.findAll("select * from i_gpFh where code='"+code+"' and fhYear not like '%(预*)' order by fhYear desc");
		//int i = 0 ;
		for(Map<String, Object> fhMap : gpFhList){
			//eachFh(gp,fhMap,i);
			String cqDate = null;
			String year = null;
			if(fhMap.get("cqDate")!=null){
				cqDate = fhMap.get("cqDate").toString();
			}else if(fhMap.get("zzdz")!=null){
				cqDate = fhMap.get("zzdz").toString();
			}else if(fhMap.get("sgdz")!=null){
				cqDate = fhMap.get("sgdz").toString();
			}else if(fhMap.get("pgdz")!=null){
				cqDate = fhMap.get("pgdz").toString();
			}
			if(cqDate!=null){
				year = cqDate.split("-")[0];
				try {
					zqiDao.update("update "+year+"_"+daytable+" set isFh='1' where code='"+code+"' and period='"+cqDate+"'");
				} catch (Exception e) {
					logger.error("{}表不存在",year+"_"+daytable);
				}
			}
			//i++;
		}
		
		
	}
	
	private void loadPeriod(){
		String lastMonthPeriod = "select DISTINCT(period) from daytable_lastmonth UNION select period from daytable_all order by period desc";
		List<Map<String, Object>> lastMonthPeriodList = (List<Map<String, Object>>)zqiDao.findAll(lastMonthPeriod);
		String deleteSql = "delete from d_period";
		zqiDao.excute(deleteSql);
		try {
		for(Map<String, Object> period : lastMonthPeriodList){
			String periodStr = period.get("period").toString();
			Calendar cd = Calendar.getInstance();
			Date date = date = DateUtil.convertStringToDate(periodStr);
			cd.setTime(date);
			int dayofweek = cd.get(Calendar.DAY_OF_WEEK);
			//int month = 
		}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
