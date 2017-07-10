package com.zqi.dataFinder.wy163;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.zqi.dataFinder.IFinderFh;
import com.zqi.frame.util.Tools;

public class Finder163Fh implements IFinderFh{

	@Override
	public List<Map<String, Object>> findFhInfo(Map<String, Object> fhInfo) {
		List<Map<String,Object>> dataList = getFhInfo(fhInfo,null);
		fhInfo.put("page", "0");
		dataList.addAll(getPgInfo(fhInfo,null));
		return dataList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getFhInfo(Map<String, Object> fhInfo,List<Map<String,Object>> dataList){
		String year = fhInfo.get("year").toString();
		Object pageObj = fhInfo.get("page");
		String page = null;
		if(pageObj!=null){
			page = fhInfo.get("page").toString();
		}
		if(page==null||"".equals(page)){
			page = "0";
		}
		String url = "http://quotes.money.163.com/hs/marketdata/service/fh.php?host=/hs/marketdata/service/fh.php&page="+page+"&query=date:"+year+"&fields=NO,SYMBOL,SNAME,DECLAREDATE,DISHTY1,paixi,DISHTY12,DISHTY13,DISHTY16,SYMBOL&sort=DECLAREDATE&order=desc&count=10000&type=query&initData=[object%20Object]&callback=callback_502566156&req=11113";
		String result = Tools.getByHttpUrl(url);
		if(dataList==null){
			dataList = new ArrayList<Map<String,Object>>();
		}
		if(result!=null&&!"".equals(result)){
			result = result.replace("callback_502566156(", "");
			result = result.substring(0, result.length()-1);
			Gson gson = new Gson();
			Map<String, Object> resultMap = gson.fromJson(result, Map.class);
			List<Map<String, Object>> items = (List<Map<String, Object>>) resultMap.get("list");
			Map<String,Map<String,Object>> dataMap = new HashMap<String, Map<String,Object>>();
			for(Map<String, Object> item : items){
				String code = item.get("SYMBOL").toString();
				String name = item.get("SNAME").toString();
				String ggDate = item.get("DECLAREDATE").toString();
				String fhYear = item.get("DISHTY1").toString();
				String dj = item.get("DISHTY12").toString();
				String cq = item.get("DISHTY13").toString();
				String fh = item.get("DISHTY3").toString();
				String zz = item.get("DISHTY8").toString();
				String sg = item.get("DISHTY7").toString();
				String valueType = item.get("paixi").toString();
				
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
					if(cq==null||cq.equals("--")){
						data.put("cqDate", ggDate);
					}else{
						data.put("cqDate", cq);
					}
					data.put("fh", fh);
				}else if(valueType.contains("转")){
					data.put("zzss", dj);
					data.put("zzdz", cq);
					if(code.equals("600556")){
						System.out.println();
					}
					if(cq==null||cq.equals("--")){
						if(dj==null||dj.equals("--")){
							data.put("zzdz", ggDate);
						}else{
							data.put("zzdz", dj);
						}
					}else{
						data.put("zzdz", cq);
					}
					data.put("zz", zz);
				}else if(valueType.contains("送")){
					data.put("sgss", dj);
					data.put("sgdz", cq);
					data.put("sg", sg);
				}
			}
			
			dataList.addAll(dataMap.values());
			Double pageCount = Double.parseDouble(resultMap.get("pagecount").toString());
			Double paged = Double.parseDouble(page);
			if(pageCount>1&&paged<pageCount-1){
				fhInfo.put("page",paged+1);
				getFhInfo(fhInfo,dataList);
			}
		}
		return dataList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getPgInfo(Map<String, Object> fhInfo,List<Map<String,Object>> dataList){
		String year = fhInfo.get("year").toString();
		Object pageObj = fhInfo.get("page");
		String page = null;
		if(pageObj!=null){
			page = fhInfo.get("page").toString();
		}
		if(page==null||"".equals(page)){
			page = "0";
		}
		String url = "http://quotes.money.163.com/hs/marketdata/service/pg.php?host=/hs/marketdata/service/pg.php&page="+page+"&query=date:"+year+"&fields=NO,SYMBOL,SNAME,DECLAREDATE,DISHTY1,paixi,DISHTY12,DISHTY13,DISHTY16,SYMBOL&sort=DECLAREDATE&order=desc&count=10000&type=query&initData=[object%20Object]&callback=callback_502566156&req=11113";
		String result = Tools.getByHttpUrl(url);
		if(dataList==null){
			dataList = new ArrayList<Map<String,Object>>();
		}
		if(result!=null&&!"".equals(result)){
			result = result.replace("callback_502566156(", "");
			result = result.substring(0, result.length()-1);
			Gson gson = new Gson();
			Map<String, Object> resultMap = gson.fromJson(result, Map.class);
			List<Map<String, Object>> items = (List<Map<String, Object>>) resultMap.get("list");
			Map<String,Map<String,Object>> dataMap = new HashMap<String, Map<String,Object>>();
			for(Map<String, Object> item : items){
				String code = item.get("SYMBOL").toString();
				String name = item.get("SNAME").toString();
				String ggDate = item.get("DECLAREDATE").toString();
				String fhYear = item.get("DISHTY1").toString();
				String dj = item.get("DISHTY12").toString();
				String cq = item.get("DISHTY13").toString();
				String pg = item.get("DISHTY18").toString();
				String pgprice = item.get("DISHTY19").toString();
				
				Map<String, Object> data = dataMap.get(code+ggDate);
				if(data==null){
					data = new HashMap<String, Object>();
					dataMap.put(code+ggDate, data);
				}
				
				data.put("code", code);
				data.put("name", name);
				data.put("ggDate", ggDate);
				data.put("fhYear", fhYear);
				data.put("pgss", dj);
				data.put("pgdz", cq);
				data.put("pg", pg);
				data.put("pgprice", pgprice);
			}
			
			dataList.addAll(dataMap.values());
			Double pageCount = Double.parseDouble(resultMap.get("pagecount").toString());
			Double paged = Double.parseDouble(page);
			if(pageCount>1&&paged<pageCount-1){
				fhInfo.put("page",paged+1);
				getPgInfo(fhInfo,dataList);
			}
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
