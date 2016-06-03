package com.zqi.dataFinder.xq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import com.zqi.dataFinder.IFinderFh;
import com.zqi.unit.DateUtil;

public class FinderXqFh implements IFinderFh{

	String[] fhKey = {"symbol","bonusyear","bonusimpdate","recorddate","exrightdate","bonusskratio","tranaddskraio","taxcdividend","bonussklistdate","tranaddsklistdate","bonusskaccday","tranaddskaccday","summarize"};
	String[] fhType = {"string","string","date","date","date","decimal","decimal","decimal","date","date","date","date","string"};
	String url = "https://xueqiu.com/stock/f10/bonus.json?symbol=%code%&page=1&size=50";
	
	public List<Map<String, Object>> findFhInfo(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String name = gp.get("name").toString();
		String type = gp.get("type").toString();
		String urlTemp = "";
		if("0".equals(type)){
			urlTemp = url.replace("%code%", "SH"+code);
		}else if("1".equals(type)){
			urlTemp = url.replace("%code%", "SZ"+code);
		}
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = getByHttpUrl(urlTemp);
		if(result!=null&&!"".equals(result)){
			JSONObject rsObj = JSONObject.fromObject(result);
			Object listObj = rsObj.get("list");
			if(listObj==null||(listObj instanceof JSONNull)){
				return dataList;
			}
			JSONArray list = (JSONArray)listObj;
			Iterator<JSONObject> itemIt = list.iterator();
			while (itemIt.hasNext()) {
				JSONObject item = itemIt.next();
				int i=0;
				Map<String, Object> data = new HashMap<String, Object>();
				for(String col : fhColumn){
					String dateKey = fhKey[i];
					String dateType = fhType[i];
					Object value = item.get(dateKey);
					String v = "";
					if(value!=null){
						v = value.toString();
						if("null".equals(v)){
							v = "";
							data.put(col, v);
							i++;
							continue;
						}
						if("date".equals(dateType)){
							Date date = null;
							try {
								date = DateUtil.convertStringToDate("yyyyMMdd",v);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							v = DateUtil.convertDateToString(date);
							data.put(col, v);
						}else if("decimal".equals(dateType)){
							data.put(col, new BigDecimal(v));
						}else{
							data.put(col, v);
						}
					}
					i++;
				}
				data.put("code", code);
				data.put("name", name);
				dataList.add(data);
			}
		}
		return dataList;
	}
	
	public static String getByHttpUrl(String url){
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
				connection.setRequestProperty("Cookie", "s=z7j12bwtsx; bid=939f56eae8ec07c84d7fa292766da868_iifbmm51; webp=0; xq_a_token=edb97b470d1e9f7d513f3bffbefbb218caaabd77; xq_r_token=1e8b07a62f4eb2a5e7ffb11b607aad96c8d15b1a; u=5427987089; xq_token_expire=Sun%20Jun%2019%202016%2013%3A25%3A19%20GMT%2B0800%20(CST); xq_is_login=1; __utma=1.671511342.1450418473.1464684363.1464765151.52; __utmz=1.1464765151.52.5.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; Hm_lvt_1db88642e346389874251b5a1eded6e3=1464243703,1464684335,1464765151,1464926077; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1464928066");
				connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				connection.setRequestProperty("Host", "xueqiu.com");
				//connection.setConnectTimeout(1000);
				//connection.setReadTimeout(1000);
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
}
