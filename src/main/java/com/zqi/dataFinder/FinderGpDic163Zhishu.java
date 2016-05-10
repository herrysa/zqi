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

public class FinderGpDic163Zhishu implements IFinderGpDic{

	public String[] gpDicColumn163Zhishu = {"CODE","CODE","NAME","NAME","wu","wu","wu","wu","pinyinCode","daytable","remark"};
	
	@Override
	public List<Map<String, Object>> findGpDic() {
		List<Map<String, Object>> gpDicList = findZhishuSh();
		gpDicList.addAll(findZhishuSz());
		return gpDicList;
	}
	
	private List<Map<String,Object>> findZhishuSh(){
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
					String codeValue = "",daytable = "daytable",nameValue = "";
					Map<String, Object> data = new HashMap<String, Object>();
					for(int i=0;i<gpDicColumn.length;i++){
						String key = gpDicColumn[i];
						if(key.equals("pinyinCode")){
							data.put(key,Tools.getPYIndexStr(nameValue, true));
						}else if(key.equals("daytable")){
							data.put(key,daytable);
						}else if(key.equals("remark")){
							data.put(key,"");
						}else{
							String datakey = gpDicColumn163Zhishu[i];
							if("wu".equals(datakey)){
								continue;
							}
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
									v = "sh"+v.substring(1);
								}
								data.put(key, v);
							}
						}
					}
					data.put("type", "2");
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
		System.out.println("---------------163沪市指数字典:"+dataList.size()+"------------------");
			return dataList;
	   }
	
	private List<Map<String,Object>> findZhishuSz(){
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
					String codeValue = "",daytable = "daytable",nameValue = "";
					Map<String, Object> data = new HashMap<String, Object>();
					for(int i=0;i<gpDicColumn.length;i++){
						String key = gpDicColumn[i];
						if(key.equals("pinyinCode")){
							data.put(key,Tools.getPYIndexStr(nameValue, true));
						}else if(key.equals("daytable")){
							data.put(key,daytable);
						}else if(key.equals("remark")){
							data.put(key,"");
						}else{
							String datakey = gpDicColumn163Zhishu[i];
							if("wu".equals(datakey)){
								continue;
							}
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
									v = "sz"+v.substring(1);
								}
								data.put(key, v);
							}
						}
					}
					data.put("type", "3");
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
		System.out.println("---------------163深市指数字典:"+dataList.size()+"------------------");
			return dataList;
	   }
}
