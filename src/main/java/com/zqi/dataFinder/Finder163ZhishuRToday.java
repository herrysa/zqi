package com.zqi.dataFinder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.frame.util.Tools;

public class Finder163ZhishuRToday implements IFinderRToday{

	public static String[] rZhishuDayColumn163 = {"period","CODE","NAME","YESTCLOSE","OPEN","HIGH","LOW","PRICE","VOLUME","TURNOVER","UPDOWN","PERCENT","zhenfu"};
	
	@Override
	public List<Map<String,Object>> findRToday(){
		List<Map<String,Object>> dataList = findZhishuShRToday();
		dataList.addAll(findZhishuSzRToday());
		return dataList;
	   }
	
	private List<Map<String,Object>> findZhishuShRToday(){
		String url = "http://quotes.money.163.com/hs/service/hsindexrank.php?host=/hs/service/hsindexrank.php&page=0&query=IS_INDEX:true;EXCHANGE:CNSESH&fields=no,SYMBOL,NAME,PRICE,UPDOWN,PERCENT,zhenfu,VOLUME,TURNOVER,YESTCLOSE,OPEN,HIGH,LOW&sort=SYMBOL&order=asc&count=500&type=query&callback=callback_925897272&req=1228";
		String result = Tools.getByHttpUrl(url);
		result = result.replace("callback_925897272(", "");
		result = result.substring(0,result.length()-1);
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
					String code = "";
					for(int i=0;i<rDayColumn.length;i++){
						if(i==rZhishuDayColumn163.length){
							break;
						}
						String key = rDayColumn[i];
						if(key.equals("period")){
							data.put(key, day);
							//data.put(key, "2016-05-06");
						}else{
							String dataKey = rZhishuDayColumn163[i];
							String dataType = rDayColumnType[i];
							Object value = item.get(dataKey);
							String v = "";
							if(value!=null){
								v = value.toString();
								if(key.equals("code")){
									code = v;
								}
								if("decimal".equals(dataType)){
									if(!"".equals(v)&&!"null".equals(v)){
										try {
											BigDecimal vNum = new BigDecimal(v);
											data.put(key, vNum);
										} catch (Exception e) {
											System.out.println("---------error:"+code+":{"+dataKey+":"+v+"}--------------------");
											data.put(key, new BigDecimal(-9999));
										}
									}
								}else{
									data.put(key, v);
								}
								
							}
							
						}
					}
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
		System.out.println("---------------163沪市指数today:"+dataList.size()+"------------------");
			return dataList;
	   }
	
	private List<Map<String,Object>> findZhishuSzRToday(){
		String url = "http://quotes.money.163.com/hs/service/hsindexrank.php?host=/hs/service/hsindexrank.php&page=0&query=IS_INDEX:true;EXCHANGE:CNSESZ&fields=no,SYMBOL,NAME,PRICE,UPDOWN,PERCENT,zhenfu,VOLUME,TURNOVER,YESTCLOSE,OPEN,HIGH,LOW&sort=SYMBOL&order=asc&count=500&type=query&callback=callback_925897272&req=1228";
		String result = Tools.getByHttpUrl(url);
		result = result.replace("callback_925897272(", "");
		result = result.substring(0,result.length()-1);
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
					String code = "";
					for(int i=0;i<rDayColumn.length;i++){
						if(i==rZhishuDayColumn163.length){
							break;
						}
						String key = rDayColumn[i];
						if(key.equals("period")){
							data.put(key, day);
							//data.put(key, "2016-05-06");
						}else{
							String dataKey = rZhishuDayColumn163[i];
							String dataType = rDayColumnType[i];
							Object value = item.get(dataKey);
							String v = "";
							if(value!=null){
								v = value.toString();
								if("decimal".equals(dataType)){
									if(!"".equals(v)&&!"null".equals(v)){
										try {
											BigDecimal vNum = new BigDecimal(v);
											data.put(key, vNum);
										} catch (Exception e) {
											System.out.println("---------error:"+code+":{"+dataKey+":"+v+"}--------------------");
											data.put(key, new BigDecimal(-9999));
										}
									}
								}else{
									data.put(key, v);
								}
								
							}
							
						}
					}
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
		System.out.println("---------------163深市指数today:"+dataList.size()+"------------------");
			return dataList;
	   }
}
