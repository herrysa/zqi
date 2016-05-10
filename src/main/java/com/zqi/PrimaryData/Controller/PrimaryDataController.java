package com.zqi.PrimaryData.Controller;

import java.io.BufferedReader;
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
import com.zqi.dataFinder.Finder163RHis;
import com.zqi.dataFinder.Finder163RToday;
import com.zqi.dataFinder.Finder163ZhishuRToday;
import com.zqi.dataFinder.IFinderRToday;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.controller.pagers.SortOrderEnum;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;
import com.zqi.unit.DateConverter;
import com.zqi.unit.DateUtil;
import com.zqi.unit.UUIDGenerator;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController extends BaseController{

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
			String findDayTableSql = "select daytable from d_gpDic where symbol='"+code+"'";
			String tableName = "";
			Map<String, Object> rs0 = zqiDao.findFirst(findDayTableSql);
			if(!rs0.isEmpty()){
				tableName = rs0.get("daytable").toString();
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
				BigDecimal changePercent = new BigDecimal(close).subtract(new BigDecimal(settlement)).divide(new BigDecimal(close),10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);;
				data.put("changepercent", changePercent.toString());
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
		Calendar calendar = Calendar.getInstance();
		if(dateTo!=null&&!"".equals(dateTo)){
			DateConverter dateConverter = new DateConverter();
			Date dateObj = (Date)dateConverter.convert(Date.class, dateTo);
			calendar.setTime(dateObj);
		}
		
		int year = calendar.get(Calendar.YEAR);
		int season = 1;
		int month = calendar.get(Calendar.MONTH);  
        switch (month) {  
        case Calendar.JANUARY:  
        case Calendar.FEBRUARY:  
        case Calendar.MARCH:  
            season = 1;  
            break;  
        case Calendar.APRIL:  
        case Calendar.MAY:  
        case Calendar.JUNE:  
            season = 2;  
            break;  
        case Calendar.JULY:  
        case Calendar.AUGUST:  
        case Calendar.SEPTEMBER:  
            season = 3;  
            break;  
        case Calendar.OCTOBER:  
        case Calendar.NOVEMBER:  
        case Calendar.DECEMBER:  
            season = 4;  
            break;  
        default:  
            break;  
        }  
        if("today".equals(fillType)){
        	findTodayData();
		}else if("jidu".equals(fillType)){
			findHisDayDataByJidu(""+year,""+season,-1);
		}else if("date".equals(fillType)){
			findRHisData(dateFrom,dateTo);
		}else{
			int days = -1;
			try {
				days = Integer.parseInt(fillType);
			} catch (Exception e) {
				days = 3;
			}
			findHisDayDataByJidu(""+year,""+season,days);
		}
		return "导入成功！";
	}
	
	
	public void findTodayData(){
		String code = "",symbol = "",period = "";
		TestTimer importTodayDataTime = new TestTimer("导入今日数据");
		importTodayDataTime.begin();
		try {
			IFinderRToday iFinderRToday = new Finder163RToday();
			List<Map<String, Object>> todayList = iFinderRToday.findRToday();
			IFinderRToday iFinderZhishuRToday = new Finder163ZhishuRToday();
			List<Map<String, Object>> todayZhishuList = iFinderZhishuRToday.findRToday();
			Map<String, Map<String, Object>> gpmap = findAGpDicMap();
			int count = 0;
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
					zqiDao.excute("delete from "+daytable+" where code='"+code+"' and period='"+period+"' and type in ('0','1')");
					dayData.put("type", type);
					zqiDao.add(dayData, daytable);
					count++;
				}
			}
			for(Map<String, Object> dayZhishuData :todayZhishuList){
				code = dayZhishuData.get("code").toString();
				period = dayZhishuData.get("period").toString();
				//String close = dayData.get("close").toString();
				Map<String, Object> gpdic;
				if(code.startsWith("0")){
					symbol = "sh"+code;
				}else{
					symbol = "sz"+code;
				}
				gpdic = gpmap.get(symbol);
				if(gpdic!=null){
					String type = gpdic.get("type").toString();
					String daytable = gpdic.get("daytable").toString();
					zqiDao.excute("delete from "+daytable+" where code='"+code+"' and period='"+period+"' and type in ('2','3')");
					dayZhishuData.put("type", type);
					zqiDao.add(dayZhishuData, daytable);
					count++;
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
	
	public void findRHisData(String dateFrom, String dateTo){
		String code = "";
		TestTimer importRHisDataTime = new TestTimer("导入今日数据");
		importRHisDataTime.begin();
		try {
			List<Map<String, Object>> gpList = findAGpDicList();
			Finder163RHis finder163rHis = new Finder163RHis();
			int count = 0;
			for(Map<String, Object> gp : gpList){
				code = gp.get("code").toString();
				String daytable = gp.get("daytable").toString();
				String type = gp.get("type").toString();
				List<Map<String,Object>> dataList = finder163rHis.findRHis(gp, dateFrom, dateTo);
				String deleteSql = "delete from "+daytable+" where code='"+code+"' and period between '"+dateFrom+"' and '"+dateTo+"' and type='"+type+"'";
				zqiDao.excute(deleteSql);
				zqiDao.addList(dataList, daytable);
				count += dataList.size();
			}
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
			errorLog.put("assistId", code);
			errorLog.put("info", dateFrom+" to "+dateTo+"导入历史日数据错误！");
			errorLog.put("logDate", DateUtil.getDateTimeNow());
			zqiDao.add(errorLog,"_log");
			e.printStackTrace();
		}
	}
	
	public void findTodayDataSina(){
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
	}
	public void findHisDayDataByJidu(String year,String jidu,int days){
		String lastFillSql = "select assistId from _log where mainId='findJiduDataSuccess'";
        Map<String, Object> lastFill = zqiDao.findFirst(lastFillSql);
        String lastSymbol = "";
        String dicSql= "select * from d_gpDic order by symbol";
        if(lastFill!=null&&!lastFill.isEmpty()){
        	lastSymbol = lastFill.get("assistId").toString();
        	dicSql= "select * from d_gpDic where symbol>'"+lastSymbol+"' order by symbol";
        }
        List<Map<String, Object>> dicList = zqiDao.findAll(dicSql);
        for(Map<String, Object> dicMap : dicList){
        	String symbol = dicMap.get("symbol").toString();
        	System.out.println("----------------"+symbol+"-----------------");
        	try {
            	String name = dicMap.get("name").toString();
            	String code = dicMap.get("code").toString();
            	String daytable = dicMap.get("daytable").toString();
            	List<String[]> dataList = findDayData(symbol,code,""+year,""+jidu);
            	if(dataList==null||dataList.size()==0){
            		continue;
            	}
            	List<String> dataSqlList = new ArrayList<String>();
            	List<String> delDataSqlList = new ArrayList<String>();
            	if(days==-1){
            		days = dataList.size()-2;
            	}
            	for(int row=2;row<days+2;row++){
            		String[] rowData = dataList.get(row);
            		String[] rowData2 = null;
            		String settlement = "-1";
            		if(row<dataList.size()-1){
            			rowData2 = dataList.get(row+1);
            			settlement = rowData2[3];
            		}
            		String period = rowData[0];
                    String open = rowData[1];
                    String high = rowData[2];
                    String low = rowData[4];
                    String close = rowData[3];
                    String volume = rowData[5];
                    String amount = rowData[6];
            		String dataSql= "insert into "+daytable+"(period,code,name,settlement,open,high,low,close,volume,amount) values ('"+period+"','"+symbol+"','"+name+"','"+settlement+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+amount+"');";
            		String delDataSql = "delete from "+daytable+" where period='"+period+"' and code='"+symbol+"'";
            		dataSqlList.add(dataSql);
            		delDataSqlList.add(delDataSql);
            	}
            	String[] sqls = dataSqlList.toArray(new String[dataSqlList.size()]);
            	String[] delSqls = delDataSqlList.toArray(new String[delDataSqlList.size()]);
            	zqiDao.bathUpdate(delSqls);
            	zqiDao.bathUpdate(sqls);
            	zqiDao.excute("delete from _log where mainId='findJiduDataSuccess'");
            	Map<String, Object> successLog = new HashMap<String, Object>();
            	successLog.put("id", UUIDGenerator.getInstance().getNextValue());
            	successLog.put("type", "findHisDayData");
            	successLog.put("mainId", "findJiduDataSuccess");
            	successLog.put("assistId", symbol);
		    	successLog.put("info", "导入成功！");
		    	successLog.put("logDate", DateUtil.getDateTimeNow());
		    	zqiDao.add(successLog,"_log");
			} catch (Exception e) {
				Map<String, Object> errorLog = new HashMap<String, Object>();
		    	errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
		    	errorLog.put("type", "findHisDayData");
		    	errorLog.put("mainId", "findJiduDataError");
		    	errorLog.put("assistId", symbol);
		    	errorLog.put("info", year+jidu+"导入日数据错误！");
		    	errorLog.put("logDate", DateUtil.getDateTimeNow());
		    	zqiDao.add(errorLog,"_log");
				e.printStackTrace();
			}
        }
	}
	
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
		String url = "http://quotes.money.163.com/service/chddata.html?code=1300141&start=20160112&end=20160428&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
		String a = Tools.getByHttpUrl(url);
		System.out.println(a);
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
}
