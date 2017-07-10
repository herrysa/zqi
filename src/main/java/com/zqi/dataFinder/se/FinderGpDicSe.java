package com.zqi.dataFinder.se;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.dataFinder.IFinderGpDic;
import com.zqi.frame.util.Tools;
import com.zqi.unit.DateUtil;

public class FinderGpDicSe implements IFinderGpDic{

	public String[] gpDicColumnSse = {"COMPANY_CODE","SECURITY_CODE_A","COMPANY_ABBR","SECURITY_ABBR_A","LISTING_DATE","totalShares","totalFlowShares","endDate"};
	
	public String[] gpDicColumnSzse = {"0","5","1","6","7","8","9","endDate"};
	public int codeIndex = 0;
	public int nameIndex = 1;
	
	@Override
	public List<Map<String, Object>> findGpDic() {
		List<Map<String, Object>> gpDiList = getSseGpList();
		gpDiList.addAll(getSzseGpList());
		return gpDiList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSseGpList() {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = getSseGpListByHttp();
		if(result==null||"".equals(result)){
			System.out.println("---------------getSseGpList error---------------");
		}else{
			result = result.substring(19, result.length()-1);
			JSONObject rsObject = JSONObject.fromObject(result);
			JSONObject pageHelp = (JSONObject) rsObject.get("pageHelp");
			JSONArray items = (JSONArray) pageHelp.get("data");
			Iterator<JSONObject> itemIt = items.iterator();
			while (itemIt.hasNext()) {
				JSONObject item = itemIt.next();
				Map<String, Object> data = new HashMap<String, Object>();
				String codeValue = "",daytable = "daytable",nameValue = "";
				for(int k=0;k<gpDicColumn.length;k++){
					String key = gpDicColumn[k];
					if(key.equals("pinyinCode")){
						data.put(key,Tools.getPYIndexStr(nameValue, true));
					}else if(key.equals("daytable")){
						data.put(key,daytable);
					}else if(key.equals("remark")){
						data.put(key,"");
					}else{
						String datakey = gpDicColumnSse[k];
						Object value = item.get(datakey);
						String v = "";
						if(value!=null){
							v = value.toString();
							if(key.equals(name)){
								nameValue = v;
							}else if(key.equals(code)){
								codeValue = v;
								daytable += codeValue.substring(0,1)+"_";
								Long codeNum = Long.parseLong(codeValue.substring(1));
								daytable += ""+(codeNum/50+1);
								v = "sh"+v;
							}
							data.put(key, v);
						}
					}
				}
				data.put("type", "0");
				dataList.add(data);
			}
			System.out.println("---------------上交所A:"+dataList.size()+"---------------");
		}
		return dataList;
	}
	
	private static String getSseGpListByHttp(){
		String result = "";
		BufferedReader in = null;
		try {
			Calendar calendar = Calendar.getInstance();
			Long longTime = calendar.getTimeInMillis();
			long random = new Double(Math.floor(Math.random()*(100000+1))).longValue();
			String url = "http://query.sse.com.cn/security/stock/getStockListData2.do?&jsonCallBack=jsonpCallback"+random+"&isPagination=true&stockCode=&csrcCode=&areaName=&stockType=1&pageHelp.cacheSize=1&pageHelp.beginPage=1&pageHelp.pageSize=2000&pageHelp.pageNo=1&_="+longTime;
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("Host","query.sse.com.cn");
			connection.setRequestProperty("Referer"," http://www.sse.com.cn/assortment/stock/list/share/");
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
			connection.getInputStream(),"utf-8"));
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
		return result;
	}
	
	private List<Map<String, Object>> getSzseGpList() {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = getSzseGpListByHttp();
		if(result==null||"".equals(result)){
			System.out.println("---------------getSzseGpList error---------------");
		}else{
			String period = DateUtil.getDateNow();
			String[] rowArr = result.split("\t\n");
 			for(String row : rowArr){
				String codeValue = "",daytable = "daytable",nameValue = "";
				Map<String, Object> data = new HashMap<String, Object>();
				String[] cellArr = row.split("\t");
				boolean addFlag = true;
				for(int k=0;k<gpDicColumn.length;k++){
					String key = gpDicColumn[k];
					if(key.equals("pinyinCode")){
						data.put(key,Tools.getPYIndexStr(nameValue, true));
					}else if(key.equals("daytable")){
						data.put(key,daytable);
					}else if(key.equals("remark")){
						data.put(key,"");
					}else{
						String datakey = gpDicColumnSzse[k];
						if("endDate".equals(datakey)){
							data.put(key, period);
						}else{
							int vIndex = Integer.parseInt(datakey);
							String v = cellArr[vIndex];
							if(key.equals(name)){
								nameValue = v;
							}else if(key.equals(code)){
								codeValue = v;
								if(codeValue.equals("")){
									addFlag =false;
									break;
								}
								//System.out.println(codeValue);
								daytable += codeValue.substring(0,1)+"_";
								Long codeNum = Long.parseLong(codeValue.substring(1));
								daytable += ""+(codeNum/50+1);
								v = "sz"+v;
							}
							data.put(key, v);
						}
					}
				}
				if(addFlag){
					data.put("type", "1");
					dataList.add(data);
				}
			}
			System.out.println("---------------深交所A:"+dataList.size()+"---------------");
		}
		return dataList;
	}
	
	private String getSzseGpListByHttp(){
		String result = "";
		BufferedReader in = null;
		try {
			String url = "http://www.szse.cn/szseWeb/ShowReport.szse?SHOWTYPE=EXCEL&CATALOGID=1110&tab1PAGENUM=6&tab1PAGECOUNT=177&tab1RECORDCOUNT=1765&ENCODE=1&TABKEY=tab1";
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			//connection.setRequestProperty("Host","query.sse.com.cn");
			//connection.setRequestProperty("Referer"," http://www.sse.com.cn/assortment/stock/list/share/");
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
			/*in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(),"gbk"));
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}*/
			/*Workbook workbook = null;
			workbook = new HSSFWorkbook(connection.getInputStream()); 
			Sheet sheet = workbook.getSheetAt(0);
			int begin = sheet.getFirstRowNum(); 
			int end = sheet.getLastRowNum(); 
			for (int i = begin; i <= end; i++) {  
				Row row = sheet.getRow(i);
		        if (null == sheet.getRow(i)) {  
		          continue;  
		        }
		        int cellBegin = row.getFirstCellNum();
		        int cellEnd = row.getFirstCellNum();
		        for (int j = cellBegin; j <= cellEnd; j++) {  
		        	Cell cell = row.getCell(j);
		        	System.out.println(cell.getStringCellValue());
		        }
		      } */
			in = new BufferedReader(new InputStreamReader(
			connection.getInputStream(),"gbk"));
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				result += line;
			}
			result = result.replaceAll("<tr bgcolor='#ffffff' >", "\t\n");
			result = result.replaceAll("<tr bgcolor='#F8F8F8' >", "\t\n");
			result = result.replaceAll("<tr  class='cls-data-tr' bgcolor='#F8F8F8'>", "\t\n");
			result = result.replaceAll("</tr>", "");
			result = result.replaceAll("<td  align='center'  >", "");
			result = result.replaceAll("<td  align='left'  >", "");
			result = result.replaceAll("<td  class='cls-data-td'  align='left' >", "");
			result = result.replaceAll("<td  class='cls-data-td' null align='left' >", "");
			result = result.replaceAll("<td  class='cls-data-td' style='mso-number-format:\\\\@' align='center' >", "");
			result = result.replaceAll("</td>", "\t");
			result = result.replaceAll("</th>", "");
			result = result.substring(result.lastIndexOf("公司网址")+6);
			//System.out.println(result);
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getDateTimeNow());
		FinderGpDicSe finderGpDicSse = new FinderGpDicSe();
		finderGpDicSse.findGpDic();
		System.out.println(DateUtil.getDateTimeNow());
	}

}
