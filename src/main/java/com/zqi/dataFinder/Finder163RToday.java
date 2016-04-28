package com.zqi.dataFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zqi.frame.util.Tools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Finder163RToday implements IFinderRToday{

	public static String[] rDayColumn163 = {"SYMBOL","SNAME","YESTCLOSE","OPEN","HIGH","LOW","PRICE","VOLUME","TURNOVER","UPDOWN","PERCENT","ZF","HS","FIVE_MINUTE","LB","WB","TCAP","MCAP","PE","MFSUM","MFRATIO.MFRATIO2","MFRATIO.MFRATIO10"};
	@Override
	public List<Map<String, Object>> findRToday() {
		
		return null;
	}
	public static List<Map<String,String>> getHttpUrlMap163(Map<String, String> titleMap){
		String url = "http://quotes.money.163.com/hs/service/diyrank.php?host=http%3A%2F%2Fquotes.money.163.com%2Fhs%2Fservice%2Fdiyrank.php&page=0&query=STYPE%3AEQA&fields=NO%2CSYMBOL%2CNAME%2CPRICE%2CPERCENT%2CUPDOWN%2CFIVE_MINUTE%2COPEN%2CYESTCLOSE%2CHIGH%2CLOW%2CVOLUME%2CTURNOVER%2CHS%2CLB%2CWB%2CZF%2CPE%2CMCAP%2CTCAP%2CMFSUM%2CMFRATIO.MFRATIO2%2CMFRATIO.MFRATIO10%2CSNAME%2CCODE%2CANNOUNMT%2CUVSNEWS&sort=SYMBOL&order=asc&count=3000&type=query";
		String result = Tools.getByHttpUrl(url);
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		if(result!=null&&!"".equals(result)){
				JSONArray rsArr = JSONArray.fromObject(result);
				JSONObject rsObject = (JSONObject) rsArr.get(0);
				if(titleMap!=null){
					Object dayObject = rsObject.get("time");
					if(dayObject!=null){
						String day = dayObject.toString().substring(0, 10);
						titleMap.put("day", day);
					}
					titleMap.put("count", rsObject.getString("total"));
				}
				JSONArray items = (JSONArray) rsObject.get("list");
				Iterator<JSONObject> itemIt = items.iterator();
				while (itemIt.hasNext()) {
					JSONObject item = itemIt.next();
					Set<String> keySet = item.keySet();
					int i=0;
					Map<String, String> data = new HashMap<String, String>();
					for(String key : keySet){
						Object value = item.get(key);
						String v = "";
						if(value!=null){
							if(value instanceof JSONObject){
								JSONObject subValue = (JSONObject)value;
								Set<String> keySet2 = subValue.keySet();
								for(String key2 : keySet2){
									Object value2 = subValue.get(key2);
									String v2 = "";
									if(value2!=null){
										v2 = value2.toString();
										data.put(key+"."+key2, v2);
									}
								}
							}else{
								v = value.toString();
								data.put(key, v);
							}
						}
					}
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
			return dataList;
	   }
}
