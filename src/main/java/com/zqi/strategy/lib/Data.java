package com.zqi.strategy.lib;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;

public class Data extends BaseLib{

	
	public Data(ZqiDao zqiDao) {
		super(zqiDao);
	}

	public String getGPData(String code ,String col,String start,String end){
		if(!code.startsWith("'")){
			code = "'"+code+"'";
		}
		List<String> extendCol = new ArrayList<String>();
		String dbCol = initCol(col,extendCol);
		String findSql = "select "+dbCol+" from daytable_all where code="+code;
		if(start!=null&&end!=null){
			findSql += " and period between "+start+" and "+end;
		}else if(start!=null){
			findSql += " and period>='"+start+"'";
		}else if(end!=null){
			findSql += " and period<='"+end+"'";
		}
		findSql += " order by period asc";
		List<Map<String,Object>> codeList = zqiDao.findAll(findSql);
		Map<String, Map<String, Object>> dataMap = new HashMap<String, Map<String,Object>>();
		getExendColData(codeList,extendCol);
		for(Map<String,Object> data : codeList){
			String period = data.get("period").toString();
			dataMap.put(period, data);
		}
		JSONObject dataJsonObject = JSONObject.fromObject(dataMap);
		String dataStr = dataJsonObject.toString();
		return dataStr;
	}
	
	public static void avg(List<Map<String,Object>> codeList,String param){
		List<String> colList = new ArrayList<String>();
		Map<String,BigDecimal> avgValueMap = new HashMap<String,BigDecimal>();
		Map<String,Queue<BigDecimal>> valueQueueMap = new HashMap<String,Queue<BigDecimal>>();
		int maxAvgNum = 0;
		Set<Integer> avgNum = new TreeSet<Integer>();
		JSONObject jsonObject = JSONObject.fromObject(param);
		Iterator<String> keyIt = jsonObject.keys();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			Object value = jsonObject.get(key);
			colList.add(key);
			if(value instanceof JSONArray){
				JSONArray values = (JSONArray)value;
				//values.iterator();
				for(Object v : values){
					String n = v.toString();
					int num = Integer.parseInt(n);
					avgNum.add(num);
					/*if(num>maxAvgNum){
						maxAvgNum = num;
					}*/
					//avgValueMap.put(key+num, new BigDecimal(0));
					Queue<BigDecimal> valueQueue = new LinkedList<BigDecimal>();
					valueQueueMap.put(key+num, valueQueue);
				}
			}else{
				String n = value.toString();
				int num = Integer.parseInt(n);
				avgNum.add(num);
				//avgValueMap.put(key+num, new BigDecimal(0));
				Queue<BigDecimal> valueQueue = new LinkedList<BigDecimal>();
				valueQueueMap.put(key+num, valueQueue);
			}
			
		}
		int i=0;
		for(Map<String,Object> dataMap : codeList){
			for(int num : avgNum){
				if(i<num-1){
					for(String col : colList){
						BigDecimal data = (BigDecimal)dataMap.get(col);
						//BigDecimal d = new BigDecimal(data);
						BigDecimal avgValue = avgValueMap.get(col+num);
						Queue<BigDecimal> valueQueue = valueQueueMap.get(col+num);
						if(avgValue==null){
							avgValue = data;
							avgValueMap.put(col+num, avgValue);
							valueQueue.offer(avgValue);
						}else{
							avgValueMap.put(col+num, avgValue.add(data));
							valueQueue.offer(data);
						}
					}
				}else{
					for(String col : colList){
						BigDecimal data = (BigDecimal)dataMap.get(col);
						BigDecimal avgValue = avgValueMap.get(col+num);
						Queue<BigDecimal> valueQueue = valueQueueMap.get(col+num);
						if(avgValue==null){
							avgValue = data;
							avgValueMap.put(col+num, avgValue);
							valueQueue.offer(avgValue);
						}else{
							BigDecimal numSumValue = avgValue.add(data);
							dataMap.put(col+num,numSumValue.divide(new BigDecimal(num),10,BigDecimal.ROUND_HALF_DOWN).setScale(3,BigDecimal.ROUND_HALF_UP));
							avgValueMap.put(col+num, numSumValue.subtract(valueQueue.poll()));
							valueQueue.offer(data);
						}
					}
				}
			}
			i++;
		}
		System.out.println();
	}
	
	private String initCol(String col,List<String> extendCol){
		String dbCol = "";
		col = col.replaceAll("'", "");
		List<String> matcherList = new ArrayList<String>();
		String methodParam = Tools.findMethod(col, matcherList);
		String[] paramArr = methodParam.split(",");
		for(String param:paramArr){
			if(param.startsWith("@")){
				String pIndex = param.replace("@_", "");
				int index = Integer.parseInt(pIndex);
				String eCol = matcherList.get(index);
				extendCol.add(eCol);
			}else{
				dbCol += param+",";
			}
		}
		if(dbCol.contains("period")){
			dbCol = dbCol.substring(0, dbCol.length()-1);
		}else{
			dbCol += "period";
		}
		return dbCol;
	}
	
	public static void main(String[] args) {
		String aa = "{a:1}";
		JSONObject jsonObject = JSONObject.fromObject(aa);
		System.out.println(jsonObject.toString());
	}
	

}
