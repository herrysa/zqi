package com.zqi.dataFinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.frame.util.Tools;
import com.zqi.unit.DBHelper;
import com.zqi.unit.DateUtil;

public class FinderGpDicSse implements IFinderGpDic{

	public String[] gpDicColumnSse = {"COMPANY_CODE","SECURITY_CODE_A","COMPANY_ABBR","SECURITY_ABBR_A","LISTING_DATE","totalShares","totalFlowShares","endDate"};
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findGpDic() {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = getSseGpList();
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
							}
							data.put(key, v);
						}
					}
				}
				dataList.add(data);
			}
			System.out.println("---------------上交所A:"+dataList.size()+"---------------");
		}
		return dataList;
	}
	
	private static String getSseGpList(){
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
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getDateTimeNow());
		FinderGpDicSse finderGpDicSse = new FinderGpDicSse();
		finderGpDicSse.findGpDic();
		System.out.println(DateUtil.getDateTimeNow());
	}

}
