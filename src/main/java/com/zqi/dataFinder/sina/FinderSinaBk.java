package com.zqi.dataFinder.sina;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.dataFinder.IFinderBk;
import com.zqi.unit.DBHelper;

public class FinderSinaBk implements IFinderBk{

	@Override
	public List<Map<String, String>> findBkInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String findBkInfoStr() {
		String bkData = "";
		bkData += findBkGp();
		bkData += findZxBkGp();
		bkData += findZsBkGp();
		return bkData;
	}
	
	public String findBkGp(){
		String[] bkCodeArr = {"gainianbankuai","diyu","bkshy"};
		String bkData = "";
		for(String bkCode : bkCodeArr){
			String[] gnbkKey = {"name","code","number","count","volume","amount","trade","changeprice","changepercent","symbol","sname","strade","schangeprice","schangepercent"};
			String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bknode%22,%22"+bkCode+"%22,%22%22,0]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  gnbkDataList = getHttpUrlMap(gnbkKey,url,titleMap);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date nowDate=new Date();
			String period = dateFormat.format(nowDate);
			//String period = titleMap.get("day");
			for(Map<String, String> gnbkData :gnbkDataList){
				String code = gnbkData.get("code");
				String bkName = gnbkData.get("name");
				String[] gnbkgpKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
				String gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bkshy_node%22,%22"+code+"%22,%22%22,0,1,40]]&callback=FDC_DC.theTableData";
				List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl,null);
				for(Map<String, String> gnbkgpData : gnbkgpDataList){
					String symbol = gnbkgpData.get("code");
					String gpName = gnbkgpData.get("name");
					bkData += period+"\t"+symbol+"\t"+gpName+"\t"+bkCode+"\t"+bkName+"\n";
				}
			}
			System.out.println("------"+bkCode+"--------");
		}
		return bkData;
	}
	
	public String findZxBkGp(){
		String[] zxBkCodeArr = {"cyb","zxqy"};
		String[] zxBkNameArr = {"创业板","中小板"};
		String bkData = "";
		int i = 0;
		for(String bkCode : zxBkCodeArr){
			String bkName = zxBkNameArr[i];
			String[] gnbkgpKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
			String gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22hq%22,%22"+bkCode+"%22,%22%22,0,1,2000]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl,titleMap);
			//String period = titleMap.get("day");
			SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
			Date nowDate=new Date();
			String period = myFmt2.format(nowDate);
			for(Map<String, String> gnbkgpData : gnbkgpDataList){
				String symbol = gnbkgpData.get("code");
				String gpName = gnbkgpData.get("name");
				bkData += period+"\t"+symbol+"\t"+gpName+"\t"+bkCode+"\t"+bkName+"\n";
			}
			System.out.println("------"+bkName+"--------");
			i++;
		}
		return bkData;
	}
	
	public String findZsBkGp(){
		String[] zsBkCodeArr = {"zhishu_000001","zhishu_399001","hs300"};
		String[] zsBkNameArr = {"上证","深证","沪深300"};
		String bkData = "";
		int i = 0 ;
		for(String bkCode : zsBkCodeArr){
			String bkName = zsBkNameArr[i];
			String[] gnbkgpKey = {"symbol","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","code","ticktime","focus","fund"};
			String gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22jjhq%22,1,2000,%22%22,0,%22"+bkCode+"%22]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl,titleMap);
			//String period = titleMap.get("day");
			SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
			Date nowDate=new Date();
			String period = myFmt2.format(nowDate);
			for(Map<String, String> gnbkgpData : gnbkgpDataList){
				String symbol = gnbkgpData.get("code");
				String gpName = gnbkgpData.get("name");
				bkData += period+"\t"+symbol+"\t"+gpName+"\t"+bkCode+"\t"+bkName+"\n";
			}
			System.out.println("------"+bkName+"--------");
			i++;
		}
		return bkData;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,String>> getHttpUrlMap(String[] keys,String url,Map<String, String> titleMap){
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
		if(result.contains("theTableData")){
			result = result.substring(result.indexOf("theTableData")+13, result.length()-1);
			JSONArray rsArr = JSONArray.fromObject(result);
			JSONObject rsObject = (JSONObject) rsArr.get(0);
			if(titleMap!=null){
				Object dayObject = rsObject.get("day");
				if(dayObject!=null){
					titleMap.put("day", dayObject.toString());
				}
				//titleMap.put("day", rsObject.getString("day"));
				titleMap.put("count", rsObject.getString("count"));
			}
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
		}else{
			System.out.println(url);
		}
		return dataList;
	}

}
