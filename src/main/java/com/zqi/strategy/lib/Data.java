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
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;
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
		Gson aa = new Gson();
		String aaa = aa.toJson(dataMap);
		JSONObject dataJsonObject = JSONObject.fromObject(dataMap);
		String dataStr = dataJsonObject.toString();
		return dataStr;
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object> getGPData(String option){
		Map<String,String> optionMap = null;
		if(option!=null){
			try {
				option = option.replaceAll("'", "");
				Gson gson = new Gson();
				optionMap = gson.fromJson(option, Map.class);
			} catch (Exception e) {
			}
		}else{
			optionMap = new HashMap<String, String>();
		}
		String code = null,col = null,gpNum = null,random = null;
		code = optionMap.get("code");
		List<Map<String,Object>> gpListTemp = null;
		boolean multiCode = false;
		if(code!=null){
			if(",".equals(code)){
				String[] codeArr = code.split(",");
				String codeSql = "(";
				for(String c : codeArr){
					codeSql += "'"+c+"',";
				}
				if("(".equals(codeSql)){
					codeSql += ")";
				}else{
					codeSql = codeSql.substring(0,codeSql.length()-1);
					codeSql += ")";
				}
				multiCode = true;
				gpListTemp = zqiDao.findAll("select * from d_gpdic where code in "+codeSql);
			}else{
				if(!code.startsWith("'")){
					code = "'"+code+"'";
				}
				gpListTemp = zqiDao.findAll("select * from d_gpdic where code="+code);
			}
		}else{
			multiCode = true;
			gpListTemp = zqiDao.findAll("select * from d_gpdic where type in ('0','1')");	
		}
		if(optionMap.containsKey("col")){
			col = optionMap.get("col");
		}else{
			col = "close";
		}
		List<String> extendCol = new ArrayList<String>();
		String dbCol = initCol(col,extendCol);
		optionMap.put("dbCol", dbCol);
		
		if(optionMap.containsKey("gpNum")){
			gpNum = optionMap.get("gpNum");
		}else{
			gpNum = "100";
		}
		if(optionMap.containsKey("random")){
			random = optionMap.get("random");
		}
		List<Map<String,Object>> gpList = new ArrayList<Map<String,Object>>();
		if("1".equals(random)){
			if("-1".equals(gpNum)){
				gpList = gpListTemp;
			}else{
				int gpNumi = Integer.parseInt(gpNum);
				for(int i=0;i<gpNumi;i++){
					int randomi =(int) (Math.random()*gpListTemp.size()+1);
					gpList.add(gpListTemp.get(randomi-1));
				}
			}
		}else{
			if("-1".equals(gpNum)){
				gpList = gpListTemp;
			}else{
				int gpNumi = Integer.parseInt(gpNum);
				if(gpNumi<gpListTemp.size()){
					gpList = gpListTemp.subList(0, gpNumi-1);
				}else{
					gpList = gpListTemp;
				}
			}
		}
		
		//Map<String, Map<String, Map<String, Object>>> codeDataMap = new HashMap<String, Map<String,Map<String,Object>>>();
		Map<String, Object> codeDataMap = new TreeMap<String, Object>();
		//Map<String, Map<String, Object>> dataMap = null;
		String hasData = null;
		if(optionMap.containsKey("hasData")){
			hasData = optionMap.get("hasData");
		}
		String key = null;
		if(optionMap.containsKey("key")){
			key = optionMap.get("key");
		}
		for(Map<String,Object> gp : gpList){
			if("0".equals(hasData)){
				String gpCode = gp.get("code").toString();
				codeDataMap.put(gpCode, gp);
				continue;
			}
			List<Map<String,Object>> codeList = gpDataFactory(gp,optionMap);
			getExendColData(codeList,extendCol);
			//dataMap = new HashMap<String, Map<String,Object>>();
			for(Map<String,Object> data : codeList){
				if("code".equals(key)){
					String gpCode = gp.get("code").toString();
					codeDataMap.put(gpCode, codeList);
				}else{
					String period = data.get("period").toString();
					Object codeDataObj= codeDataMap.get(period);
					if(codeDataObj==null){
						if(multiCode){
							List<Map<String, Object>> codeDataList = new ArrayList<Map<String, Object>>();
							codeDataList.add(data);
							codeDataMap.put(period,codeDataList);
						}else{
							codeDataMap.put(period, data);
						}
					}else{
						if(multiCode){
							List<Map<String, Object>> codeDataList = (List<Map<String, Object>>)codeDataObj;
							codeDataList.add(data);
						}
					}
					//dataMap.put(period, data);
				}
				}
				
			//codeDataMap.put(gpCode, dataMap);
		}
		return codeDataMap;
		
	}
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> gpDataFactory(String code,Map<String, String> optionMap){
		String start = null, end = null, ex_suspended =null,ex_new =null;
		List<Map<String,Object>> codeList = null;
		if(optionMap.containsKey("start")){
			start = optionMap.get("start");
		}
		if(optionMap.containsKey("end")){
			end = optionMap.get("end");
		}
		if(optionMap.containsKey("ex_suspended")){
			ex_suspended = optionMap.get("ex_suspended");
		}
		if(optionMap.containsKey("ex_new")){
			ex_new = optionMap.get("ex_new");
		}
		String dbCol = optionMap.get("dbCol");
		
		String findSql = null;
		if("all".equals(code)){
			findSql = "select "+dbCol+" from daytable_all where 1=1 ";
		}else if(",".equals(code)){
			findSql = "select "+dbCol+" from daytable_all where code in "+code;
		}else{
			findSql = "select "+dbCol+" from daytable_all where code="+code;
		}
		if(start!=null&&end!=null){
			findSql += " and period between '"+start+"' and '"+end+"'";
		}else if(start!=null){
			findSql += " and period>='"+start+"'";
		}else if(end!=null){
			findSql += " and period<='"+end+"'";
		}
		if(!"0".equals(ex_suspended)){
			findSql += " and close<>0";
		}
		if(!"0".equals(ex_new)){
			findSql += " and invalid=0";
		}
		findSql += " order by period asc,code asc";
		codeList = zqiDao.findAll(findSql);
		if(codeList==null){
			codeList = new ArrayList<Map<String,Object>>();
		}
		return codeList;
	}
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> gpDataFactory(Map<String,Object> gp,Map<String, String> optionMap){
		String start = null, end = null, ex_suspended =null,ex_new =null;
		String code = gp.get("code").toString();
		String daytable = gp.get("daytable").toString();
		List<Map<String,Object>> codeList = null;
		if(optionMap.containsKey("start")){
			start = optionMap.get("start");
			if(start.contains("'")){
				start = start.replaceAll("'", "");
			}
		}
		if(optionMap.containsKey("end")){
			end = optionMap.get("end");
			if(end.contains("'")){
				end = end.replaceAll("'", "");
			}
		}
		if(optionMap.containsKey("ex_suspended")){
			ex_suspended = optionMap.get("ex_suspended");
		}
		if(optionMap.containsKey("ex_new")){
			ex_new = optionMap.get("ex_new");
		}
		String dbCol = optionMap.get("dbCol");
		
/*		String listDate = gp.get("listDate").toString();
		Calendar calendar = Calendar.getInstance();
		try {
			if(listDate!=null&&!"null".equals(listDate)){
				calendar.setTime(DateUtil.convertStringToDate(listDate));
				int dayOfyear = calendar.get(Calendar.DAY_OF_YEAR);
				calendar.set(Calendar.DAY_OF_YEAR,dayOfyear+90);
				if(start==null){
					start = DateUtil.convertDateToString(calendar.getTime());
				}else{
					Calendar cdStart = Calendar.getInstance();
					cdStart.setTime(DateUtil.convertStringToDate(start));
					int d = calendar.compareTo(cdStart);
					if(d>0){
						start = DateUtil.convertDateToString(calendar.getTime());
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		
		String findSql = "select "+dbCol+" from "+daytable+" where code='"+code+"'";
		if(start!=null&&end!=null){
			findSql += " and period between '"+start+"' and '"+end+"'";
		}else if(start!=null){
			findSql += " and period>='"+start+"'";
		}else if(end!=null){
			findSql += " and period<='"+end+"'";
		}
		if(!"0".equals(ex_suspended)){
			findSql += " and close<>0";
		}
		
		if(!"0".equals(ex_new)){
			findSql += " and invalid=0";
		}
		findSql += " order by period asc";
		
		codeList = zqiDao.findAll(findSql);
		if(codeList==null){
			codeList = new ArrayList<Map<String,Object>>();
		}
		/*Map<String, Map<String, Object>> dataMap = new HashMap<String, Map<String,Object>>();;
		for(Map<String,Object> data : codeList){
			String period = data.get("period").toString();
			dataMap.put(period, data);
		}
		String findFhSql = "select * from i_gpfh where code='"+code+"' and fhYear not like '%(预*)'";
		List<Map<String,Object>> fhList = zqiDao.findAll(findFhSql);
		for(Map<String,Object> fh : fhList){
			Object cqDateObj = fh.get("cqDate");
			Object sgDateObj = fh.get("sgdz");
			Object zzDateObj = fh.get("zzdz");
			if(cqDateObj!=null){
				String period = cqDateObj.toString();
				Map<String,Object> data = dataMap.get(period);
				if(data!=null){
					data.put("cq", "1");
				}
			}
			if(sgDateObj!=null){
				String period = sgDateObj.toString();
				Map<String,Object> data = dataMap.get(period);
				if(data!=null){
					data.put("sg", "1");
				}
			}
			if(zzDateObj!=null){
				String period = zzDateObj.toString();
				Map<String,Object> data = dataMap.get(period);
				if(data!=null){
					data.put("zz", "1");
				}
			}
		}*/
		System.out.println("get "+code+" data over!");
		return codeList;
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
		//System.out.println();
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
