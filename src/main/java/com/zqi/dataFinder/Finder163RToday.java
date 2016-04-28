package com.zqi.dataFinder;

import java.math.BigDecimal;
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

	public static String[] rDayColumn163 = {"period","SYMBOL","SNAME","YESTCLOSE","OPEN","HIGH","LOW","PRICE","VOLUME","TURNOVER","UPDOWN","PERCENT","ZF","HS","FIVE_MINUTE","LB","WB","TCAP","MCAP","PE","MFSUM","MFRATIO.MFRATIO2","MFRATIO.MFRATIO10"};
	
	@Override
	public List<Map<String,Object>> findRToday(){
		String url = "http://quotes.money.163.com/hs/service/diyrank.php?host=http%3A%2F%2Fquotes.money.163.com%2Fhs%2Fservice%2Fdiyrank.php&page=0&query=STYPE%3AEQA&fields=NO%2CSYMBOL%2CNAME%2CPRICE%2CPERCENT%2CUPDOWN%2CFIVE_MINUTE%2COPEN%2CYESTCLOSE%2CHIGH%2CLOW%2CVOLUME%2CTURNOVER%2CHS%2CLB%2CWB%2CZF%2CPE%2CMCAP%2CTCAP%2CMFSUM%2CMFRATIO.MFRATIO2%2CMFRATIO.MFRATIO10%2CSNAME%2CCODE%2CANNOUNMT%2CUVSNEWS&sort=SYMBOL&order=asc&count=3000&type=query";
		String result = Tools.getByHttpUrl(url);
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		if(result!=null&&!"".equals(result)){
			JSONObject rsObject = JSONObject.fromObject(result);
				String day = "";
				Object dayObject = rsObject.get("time");
				if(dayObject!=null){
					day = dayObject.toString().substring(0, 10);
				}
				/*if(titleMap!=null){
					if(dayObject!=null){
						day = dayObject.toString().substring(0, 10);
						titleMap.put("day", day);
					}
					titleMap.put("count", rsObject.getString("total"));
				}*/
				JSONArray items = (JSONArray) rsObject.get("list");
				Iterator<JSONObject> itemIt = items.iterator();
				while (itemIt.hasNext()) {
					JSONObject item = itemIt.next();
					Map<String, Object> data = new HashMap<String, Object>();
					for(int i=0;i<rDayColumn.length;i++){
						String key = rDayColumn[i];
						if(key.equals("period")){
							data.put(key, day);
						}else{
							String dataKey = rDayColumn163[i];
							String dataType = rDayColumnType[i];
							if(dataKey.contains(".")){
								String[] keyArr = dataKey.split("\\.");
								Object value = item.get(keyArr[0]);
								JSONObject itemSub = (JSONObject)value;
								Object value2 = itemSub.get(keyArr[1]);
								String v = "";
								if(value2!=null){
									v = value2.toString();
									if("decimal".equals(dataType)){
										data.put(key, new BigDecimal(v));
									}else{
										data.put(key, v);
									}
									
								}
							}else{
								Object value = item.get(dataKey);
								String v = "";
								if(value!=null){
									v = value.toString();
									if("decimal".equals(dataType)){
										data.put(key, new BigDecimal(v));
									}else{
										data.put(key, v);
									}
									
								}
							}
							
						}
					}
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
		System.out.println("---------------163today:"+dataList.size()+"------------------");
			return dataList;
	   }
}
