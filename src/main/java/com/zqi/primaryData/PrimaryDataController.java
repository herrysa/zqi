package com.zqi.primaryData;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zqi.dataFinder.IFinderFh;
import com.zqi.dataFinder.IFinderRToday;
import com.zqi.dataFinder.wy163.Finder163Fh;
import com.zqi.dataFinder.wy163.Finder163RToday;
import com.zqi.dataFinder.wy163.Finder163ZhishuRToday;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.controller.pagers.SortOrderEnum;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;
import com.zqi.primaryData.fileDataBase.IFileDataBase;
import com.zqi.primaryData.fileDataBase.RHisFileDataBase;
import com.zqi.unit.DateConverter;
import com.zqi.unit.DateUtil;
import com.zqi.unit.FileUtil;
import com.zqi.unit.UUIDGenerator;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController extends BaseController{

	@RequestMapping("/primaryDataMain")
	public String primaryMain(){
		
		return "primaryData/primaryDataMain";
	}
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
		/*Map columns1 = request.getParameterMap();
		Set<Entry<String, Object>> pp = columns1.entrySet();
		Set<String> keys = columns1.keySet();
		for(Entry<String, Object> p : pp){
			Object v= p.getValue();
			if(v instanceof String){
				System.out.println(p.getKey()+":"+p.getValue());
			}else{
				String[] vArr = (String[])v;
				System.out.println(p.getKey()+":"+vArr[0]);
			}
		}*/
		if(code!=null&&!"".equals(code)){
			String findDayTableSql = "select type,daytable from d_gpDic where symbol='"+code+"'";
			String tableName = "",type = "";
			Map<String, Object> rs0 = zqiDao.findFirst(findDayTableSql);
			if(!rs0.isEmpty()){
				tableName = rs0.get("daytable").toString();
				type = rs0.get("type").toString();
			}
			if("2".equals(type)||"3".equals(type)){
				code = code.replace("sh", "0");
				code = code.replace("sz", "1");
			}else{
				code = code.replace("sh", "");
				code = code.replace("sz", "");
			}
			
			String dayDataSql = "select * from "+tableName+" where code='"+code+"'";
			if(period!=null&&!"".equals(period)){
				dayDataSql += " and period='"+period+"'";
			}
			List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
			JQueryPager pagedRequests = null;
			pagedRequests = (JQueryPager) pagerFactory.getPager(
					PagerFactory.JQUERYTYPE, request);
			pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
			List<Map<String, Object>> dayData = pagedRequests.getList();
			for(Map<String, Object> data : dayData){
				String settlement = data.get("settlement").toString();
				String close = data.get("close").toString();
				BigDecimal closeNum = new BigDecimal(close);
				if(closeNum.compareTo(new BigDecimal(0))==0){
					data.put("changepercent", "0");
				}else{
					BigDecimal changePercent = new BigDecimal(close).subtract(new BigDecimal(settlement)).divide(new BigDecimal(close),10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);;
					data.put("changepercent", changePercent.toString());
				}
			}
			/*List<Map<String, String>> result = new ArrayList<Map<String,String>>();
			Map<String, String> row = new HashMap<String, String>();
			row.put("customer_id", "1");
			row.put("lastname", "1");
			row.put("firstname", "1");
			row.put("email", "1");
			result.add(row);*/
			resultMap.put("page", pagedRequests.getPageNumber());
			resultMap.put("records", pagedRequests.getTotalNumberOfRows());
			resultMap.put("rows", pagedRequests.getList());
			resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		}else{
			List<Map<String, Object>> rsList = new ArrayList<Map<String,Object>>();
			String dicSql = "select * from d_gpdic order by symbol asc";
			List<Map<String, Object>> gpList = zqiDao.findAll(dicSql);
			List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
			JQueryPager pagedRequests = null;
			pagedRequests = (JQueryPager) pagerFactory.getPager(
					PagerFactory.JQUERYTYPE, request);
			int start = pagedRequests.getStart() , end = pagedRequests.getEnd();
			int gpIndex=0;
			String dayDataSql = "";
			Set<String> daytableSet = new HashSet<String>();
			for(;gpIndex<=gpList.size()-1;gpIndex++){
				Map<String, Object> gp = gpList.get(gpIndex);
				String tableName = "" , symbol = "";
				tableName = gp.get("daytable").toString();
				symbol = gp.get("symbol").toString();
				//dayDataSql += "select * from "+tableName+" where code='"+symbol+"' and period='"+period+"' union ";
				daytableSet.add(tableName);
				/*Map<String, Object> rs0 = zqiDao.findFirst(dayDataSql);
				if(!rs0.isEmpty()){
					rsList.add(rs0);
				}*/
			}
			String tableStr1 = "" , tableStr2 = "" ;
			int i = 0;
			for(String table : daytableSet){
				if(i<daytableSet.size()/2){
					tableStr1 += table+",";
				}else{
					tableStr2 += table+",";
				}
				i++;
			}
			if(!"".equals(tableStr1)){
				tableStr1 = tableStr1.substring(0, tableStr1.length()-1);
				tableStr2 = tableStr2.substring(0, tableStr2.length()-1);
				dayDataSql = "select * from (select * from "+tableStr1;
				dayDataSql += " union select * from "+tableStr2+") u "+" where u.period='"+period+"'";
			}
			dayDataSql = "select * from daytable_all where period='"+period+"'";
			String orderName = pagedRequests.getSortCriterion();
			if(orderName==null){
				pagedRequests.setSortCriterion("changepercent");
				pagedRequests.setSortDirection(SortOrderEnum.DESCENDING);
			}else{
				pagedRequests.setSortCriterion(""+orderName);
			}
			pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
//			for(Map<String, Object> data : rsList){
//				String settlement = data.get("settlement").toString();
//				String close = data.get("close").toString();
//				BigDecimal changePercent = new BigDecimal(close).subtract(new BigDecimal(settlement)).divide(new BigDecimal(close),10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);;
//				data.put("changepercent", changePercent.toString());
//			}
			//pagedRequests.setTotalNumberOfRows(rsList.size());
			resultMap.put("page", pagedRequests.getPageNumber());
			resultMap.put("records", pagedRequests.getTotalNumberOfRows());
			resultMap.put("rows", pagedRequests.getList());
			resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		}
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
	public String findHisDayData(String dateFrom,String dateTo,String fillType){

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
		return "导入成功！";
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
		calendar.set(Calendar.DAY_OF_YEAR, -1);
		dateTo = DateUtil.convertDateToString(calendar.getTime());
		try {
			List<Map<String, Object>> gpList = findAGpDicList(null);
			String delSql = "delete from daytable_all where period between '"+dateFrom+"' and '"+dateTo+"'";
			zqiDao.excute(delSql);
			
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
		calendar.set(Calendar.DAY_OF_YEAR, -1);
		dateTo = DateUtil.convertDateToString(calendar.getTime());
		try {
			List<Map<String, Object>> gpList = findAGpDicList(null);
			String delSql = "delete from daytable_all where period between '"+dateFrom+"' and '"+dateTo+"'";
			zqiDao.excute(delSql);
			
			RHisFileDataBase yearDb = new RHisFileDataBase(year);
			RHisFileDataBase tempDb = new RHisFileDataBase("temp");
			tempDb.deleteDataBase();
			List<String> daytableList = new ArrayList<String>();
			for(Map<String, Object> gp : gpList){
				String code = gp.get("code").toString();
				String daytable = gp.get("daytable").toString();
				if(!daytableList.contains(daytable)){
					daytableList.add(daytable);
				}
				String content  = yearDb.readStr(code);
				tempDb.writeStr(daytable, content);
			}
			List<String> loadList = new ArrayList<String>();
	        for(String daytable : daytableList){
	        	String loadDataSql = tempDb.getLoadFileSql(daytable);
	        	loadList.add(loadDataSql);
	        }
	        String[] loadSqls = loadList.toArray(new String[loadList.size()]);
	        zqiDao.bathUpdate(loadSqls);
	        setMessage("导入"+year+"年度日数据成功！");
		} catch (Exception e) {
			setMessage("导入"+year+"年度日数据失败！");
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
			Map<String, Map<String, Object>> gpmap = findAGpDicMap();
			int count = 0;
			Map<String, List<Map<String, Object>>> dayTableData = new HashMap<String, List<Map<String,Object>>>();
			String delSql = "delete from daytable_all where period='"+period+"'";
			zqiDao.excute(delSql);
			for(Map<String, Object> dayData :todayList){
				code = dayData.get("code").toString();
				period = dayData.get("period").toString();
				//String close = dayData.get("close").toString();
				Map<String, Object> gpdic;
				if(code.startsWith("6")){
					symbol = "sh"+code;
				}else{
					symbol = "sz"+code;
				}
				gpdic = gpmap.get(symbol);
				if(gpdic!=null){
					String daytable = gpdic.get("daytable").toString();
					String type = gpdic.get("type").toString();
					dayData.put("type", type);
					List<Map<String, Object>> dayTableList = dayTableData.get(daytable);
					if(dayTableList==null){
						dayTableList = new ArrayList<Map<String,Object>>();
						dayTableData.put(daytable,dayTableList);
					}
					dayTableList.add(dayData);
					//zqiDao.add(dayData, daytable);
					count++;
				}
			}
			for(Map<String, Object> dayZhishuData :todayZhishuList){
				code = dayZhishuData.get("code").toString();
				period = dayZhishuData.get("period").toString();
				//String close = dayData.get("close").toString();
				Map<String, Object> gpdic;
				if(code.startsWith("0")){
					symbol = "sh"+code.substring(1);
				}else{
					symbol = "sz"+code.substring(1);
				}
				gpdic = gpmap.get(symbol);
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
					count++;
				}
			}
			
			Set<String> daytableSet = dayTableData.keySet();
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
			}
			
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            e.printStackTrace();
		}
        return dayList;
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
		String url = "https://xueqiu.com/stock/f10/bonus.json?symbol=SZ000426&page=1&size=50";
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
		String dataCol = "code,name,fhYear,ggDate,djDate,cqDate,fh,sg,zz,sgss,zzss,sgdz,zzdz,zzdz";
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
		FileUtil.writeFile(stringBuilder.toString(), info+"i_gpFh.txt");
		String loadDataSql = "load data infile '"+info+"i_gpFh.txt' into table i_gpFh("+dataCol+",txt"+");";
		zqiDao.excute(loadDataSql);
		System.out.println("------"+year+"分红信息导入成功--------");
		return resultMap;
	}
	
}
