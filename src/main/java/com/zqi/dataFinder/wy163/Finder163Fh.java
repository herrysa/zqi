package com.zqi.dataFinder.wy163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.dataFinder.IFinderFh;
import com.zqi.frame.util.Tools;

public class Finder163Fh implements IFinderFh{

	@Override
	public List<Map<String, Object>> findFhInfo(Map<String, Object> fhInfo) {
		String year = fhInfo.get("year").toString();
		String url = "http://quotes.money.163.com/hs/marketdata/service/fh.php?host=/hs/marketdata/service/fh.php&page=0&query=date:"+year+"&fields=NO,SYMBOL,SNAME,DECLAREDATE,DISHTY1,paixi,DISHTY12,DISHTY13,DISHTY16,SYMBOL&sort=DECLAREDATE&order=desc&count=10000&type=query&initData=[object%20Object]&callback=callback_502566156&req=11113";
		String result = Tools.getByHttpUrl(url);
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		if(result!=null&&!"".equals(result)){
			result = result.replace("callback_502566156(", "");
			result = result.substring(0, result.length()-1);
			JSONObject rsObject = JSONObject.fromObject(result);
			JSONArray items = (JSONArray) rsObject.get("list");
			Iterator<JSONObject> itemIt = items.iterator();
			Map<String,Map<String,Object>> dataMap = new HashMap<String, Map<String,Object>>();
			while (itemIt.hasNext()) {
				JSONObject item = itemIt.next();
				
				String code = item.getString("SYMBOL");
				String name = item.getString("SNAME");
				String ggDate = item.getString("DECLAREDATE");
				String fhYear = item.getString("DISHTY1");
				String dj = item.getString("DISHTY12");
				String cq = item.getString("DISHTY13");
				String fh = item.getString("DISHTY3");
				String zz = item.getString("DISHTY8");
				String sg = item.getString("DISHTY7");
				String valueType = item.getString("paixi");
				
				Map<String, Object> data = dataMap.get(code+ggDate);
				if(data==null){
					data = new HashMap<String, Object>();
					dataMap.put(code+ggDate, data);
				}
				
				data.put("code", code);
				data.put("name", name);
				data.put("ggDate", ggDate);
				data.put("fhYear", fhYear);
				if(valueType.contains("派")){
					data.put("djDate", dj);
					data.put("cqDate", cq);
					data.put("fh", fh);
				}else if(valueType.contains("转")){
					data.put("zzss", dj);
					data.put("zzdz", cq);
					data.put("zz", zz);
				}else if(valueType.contains("送")){
					data.put("sgss", dj);
					data.put("sgdz", cq);
					data.put("sg", sg);
				}
			}
			
			dataList.addAll(dataMap.values());
		}
		return dataList;
	}

	public static void main(String[] args) {
		Finder163Fh fh = new Finder163Fh();
		Map<String, Object> fhInfo = new HashMap<String, Object>();
		fhInfo.put("year", "2015");
		fh.findFhInfo(fhInfo);
	}
}
