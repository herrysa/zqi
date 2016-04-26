package com.zqi.PrimaryData.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.unit.DBHelper;
import com.zqi.unit.DateConverter;
import com.zqi.unit.UUIDGenerator;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController extends BaseController{

	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> primaryDataGridList(HttpServletRequest request,String gpCode){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		String[] columns = request.getParameterValues("columns");
		Object q = request.getAttribute("columns");
		Map columns1 = request.getParameterMap();
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
		}
		Map<String, Object> r = new HashMap<String, Object>();
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
			/*List<Map<String, String>> result = new ArrayList<Map<String,String>>();
			Map<String, String> row = new HashMap<String, String>();
			row.put("customer_id", "1");
			row.put("lastname", "1");
			row.put("firstname", "1");
			row.put("email", "1");
			result.add(row);*/
			r.put("page", pagedRequests.getPageNumber());
			r.put("records", pagedRequests.getTotalNumberOfRows());
			r.put("rows", pagedRequests.getList());
			r.put("total", pagedRequests.getTotalNumberOfPages());
		}
		return r;
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
	public String findHisDayData(String fillDate,String fillType){
		DateConverter dateConverter = new DateConverter();
		Date dateObj = (Date)dateConverter.convert(Date.class, fillDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateObj);
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
            	List<String[]> dataList = findDayData(symbol,code,""+year,""+season);
            	if(dataList==null||dataList.size()==0){
            		continue;
            	}
            	List<String> dataSqlList = new ArrayList<String>();
            	List<String> delDataSqlList = new ArrayList<String>();
            	for(int row=2;row<dataList.size();row++){
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
		    	zqiDao.add(successLog,"_log");
			} catch (Exception e) {
				Map<String, Object> errorLog = new HashMap<String, Object>();
		    	errorLog.put("id", UUIDGenerator.getInstance().getNextValue());
		    	errorLog.put("type", "findHisDayData");
		    	errorLog.put("mainId", "findJiduDataError");
		    	errorLog.put("assistId", symbol);
		    	errorLog.put("info", year+season+"导入日数据错误！");
		    	zqiDao.add(errorLog,"_log");
				e.printStackTrace();
			}
        	
        	
        }
		return "导入成功！";
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
			HtmlPage page;
			page = webClient.getPage("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/"+code+".phtml?year="+year+"&jidu="+jidu);
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
			DomElement domElement = page.getElementById("FundHoldSharesTable");
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
		
		String[] gnbkKey = {"name","code","number","count","volume","amount","trade","changeprice","changepercent","symbol","sname","strade","schangeprice","schangepercent"};
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
		}
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
