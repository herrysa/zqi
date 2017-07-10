package com.zqi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Pipeline;

import com.zqi.frame.util.JedisUtils;

public class HQDataBox implements Runnable{

	private String name;
	private String code;
	private String period;
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

	private Pipeline pipeline;

	public Pipeline getPipeline() {
		return pipeline;
	}
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	//private Map<String,List<Map<String, Object>>> dataListMap = new HashMap<String, List<Map<String,Object>>>();
	//private Map<String,String> periodMap = new HashMap<String, String>();
	//private Map<String, Object> mapDatas;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
	
	//private Map<String, String> optionMap;
	
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	//private int cacheCapacity = 50;
	//private int cacheNum = 2;
	//private int nextCache = 1;
	private String listStart;
	private String listEnd;
	private int nextIndex = 0;
	/*private boolean findOver = false;
	private String start;
	private String end;
	private String listStart;
	private String listEnd;*/
	/*
	private String ex_suspended;
	private String ex_new;*/
	//private HQFinder hqFinder;
	
	/*private ConcurrentHashMap<String, Future> futureMap;  
	
	public ConcurrentHashMap<String, Future> getFutureMap() {
		return futureMap;
	}
	public void setFutureMap(ConcurrentHashMap<String, Future> futureMap) {
		this.futureMap = futureMap;
	}*/
	/*public String getEx_suspended() {
		return ex_suspended;
	}
	public void setEx_suspended(String ex_suspended) {
		this.ex_suspended = ex_suspended;
	}
	public String getEx_new() {
		return ex_new;
	}
	public void setEx_new(String ex_new) {
		this.ex_new = ex_new;
	}*/
	
	/*public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}*/
	/*public Map<String, String> getOptionMap() {
		return optionMap;
	}
	public void setOptionMap(Map<String, String> optionMap) {
		this.optionMap = optionMap;
		this.start = optionMap.get("start");
		this.end = optionMap.get("end");
	}*/
	/*public HQFinder getHqFinder() {
		return hqFinder;
	}
	public void setHqFinder(HQFinder hqFinder) {
		this.hqFinder = hqFinder;
	}*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
		Map<String, Object> data = this.dataList.get(0);
		this.listStart = data.get("period").toString();
		/*List<Map<String, Object>> dataListTemp = dataList;
		Map<String, Object> mapDatasTemp = new HashMap<String, Object>();
		Set<String> periodSet = new TreeSet<String>();
		for(Map<String, Object> dataTemp : dataListTemp){
			String period = dataTemp.get("period").toString();
			periodSet.add(period);
			mapDatasTemp.put(period,dataTemp);
		}
		int i = 0;
		List<Map<String, Object>> cache = this.dataListMap.get(nextCache);
		if(cache==null){
			cache = new ArrayList<Map<String,Object>>();
			this.dataListMap.put(""+nextCache,cache);
		}else{
			cache.clear();
		}
		for(String period : periodSet){
			periodMap.put(period,nextCache+"_"+i);
			Map<String, Object> dataTemp = (Map<String, Object>)mapDatasTemp.get(period);
			cache.add(dataTemp);
			//this.dataList.indexOf(dataList)
			i++;
		}
		nextCache++;*/
	}
	
	public void setData(Map<String, Object> data){
		
		dataList.add(data);
		/*if(listStart==null){
			this.listStart = data.get("period").toString();
		}*/
		if(dataList.size()>10){
			dataList.remove(0);
			//this.listStart = dataList.get(0).get("period").toString();
			//dataList = dataList.subList(dataList.size()-10, dataList.size());
		}
	}
	
	public Map<String,Object> getRHQData(){
		
		if(!dataList.isEmpty()){
			return dataList.get(dataList.size()-1);
		}else{
			return null;
		}
	}
	
	/*public Map<String,Object> getRHQData(String period){
		
		return null;
	}*/
	
	/*public Map<String,Object> getRHQData(String period){
		if(listStart==null){
			listStart = dataList.get(0).get("period").toString();
		}
		Integer index = PeriodFinder.findPeriodIndex(listStart, period);
		if(index>=0){
			return getListData(period,index);
		}else{
			return null;
		}
		
	}*/
	public Map<String,Object> getRHQData(String period){
		return (Map<String,Object>)JedisUtils.getObject(code+":"+period);
		//return (Map<String,Object>)JedisUtils.toObject(pipeline.get(JedisUtils.getBytesKey(code+":"+period)));
	}
	
	
	/*public Map<String,Object> getRHqData(String period){
		//Map<String,Object> data = getListData(period,nextIndex);
		//nextIndex++;
		
		return hqFinder.getGpHq(code, period);
	}*/
	
	public Map<String,Object> getListData(String period,int index){
		if(index>=dataList.size()){
			return null;
		}
		Map<String,Object> data = dataList.get(index);
		String periodTemp = data.get("period").toString();
		int c = period.compareTo(periodTemp);
		if(c==0){
			return data;
		}else if(c<0){
			List<Map<String,Object>> datListTemp = dataList.subList(0, index);
			for(int i=datListTemp.size()-1;i>=0;i--){
				Map<String,Object> dataTemp = datListTemp.get(i);
				String pLeft = dataTemp.get("period").toString();
				int c2 = period.compareTo(pLeft);
				if(c2==0){
					return dataTemp;
				}else if(c2<0){
					continue;
				}else{
					return null;
				}
			}
		}else{
			List<Map<String,Object>> datListTemp = dataList.subList(index, dataList.size());
			for(int i=0;i<datListTemp.size();i++){
				Map<String,Object> dataTemp = datListTemp.get(i);
				String pRight = dataTemp.get("period").toString();
				int c3 = period.compareTo(pRight);
				if(c3==0){
					return dataTemp;
				}else if(c3>0){
					continue;
				}else{
					return null;
				}
			}
		}
		return null;
	}
	
	/*public Map<String,Object> getHqData(String period){
		if(period.compareTo(listEnd)>0){
			
		}
		String cachePath = periodMap.get(period);
		if(cachePath==null){
			return null;
		}else{
			String[] pathArr = cachePath.split("_");
			int index = Integer.parseInt(pathArr[1]);
			Map<String,Object> data = dataListMap.get(pathArr[0]).get(index);
			return data;
		}
	}*/
	
	/*public List<Map<String,Object>> getHqData(String start,String end){
		String startPath = periodMap.get(start);
		String endPath = periodMap.get(end);
		if(startPath==null||endPath==null){
			return null;
		}else{
			String[] spathArr = startPath.split("_");
			String[] epathArr = endPath.split("_");
			int startIndex = Integer.parseInt(spathArr[1]);
			int etartIndex = Integer.parseInt(epathArr[1]);
			if(spathArr[0].equals(epathArr[0])){
				return dataListMap.get(spathArr[0]).subList(startIndex, etartIndex);
			}else{
				List<Map<String, Object>> sList = dataListMap.get(spathArr[0]);
				List<Map<String, Object>> eList = dataListMap.get(epathArr[0]);
				List<Map<String, Object>> subDataList = sList.subList(startIndex, sList.size());
				subDataList.addAll( eList.subList(0, etartIndex));
				return subDataList;
			}
		}
	}*/
	
	
	
	/*public Map<String,Object> getHqData(String period,int index){
		
		if(){
			
		}
		Map<String,Object> data = dataList.get(index);
		String dataPeriod = data.get("period").toString();
		if(period.equals(dataPeriod)){
			return data;
		}else{
			
		}
		return data;
	}*/
	
	/*public Map<String,Object> getDataMap() {
		mapDatas = new HashMap<String, Object>();
		Set<String> periodSet = periodMap.keySet();
		for(String period : periodSet){
			String cachePath = periodMap.get(period);
			String[] pathArr = cachePath.split("_");
			int index = Integer.parseInt(pathArr[1]);
			Map<String,Object> data = dataListMap.get(pathArr[0]).get(index);
			mapDatas.put(period, data);
		}
		return mapDatas;
	}*/
	
	/*public void removeOldCache(){
		int cacheNum = nextCache-1;
		List<Map<String, Object>> dataList = dataListMap.get(""+cacheNum);
		dataList.clear();
		
	}*/
	
	@Override
	public void run() {
		Map<String,Object> data = (Map<String,Object>)JedisUtils.getObject(code+":"+period);
		this.setData(data);
	}
	
	/*@Override
	public void run() {
		if(dataListMap.size()<cacheNum){
			String startTemp = null;
			String endTemp = null;
			Map<String, String> optionMapTemp = new HashMap<String, String>();
			optionMapTemp.putAll(optionMap);
			if(dataListMap.size()==0){
				startTemp = start;
				listStart = start;
				endTemp = PeriodFinder.findEndPeriod(startTemp,end,cacheCapacity);
			}else{
				startTemp = PeriodFinder.findEndPeriod(listEnd,end,1);
				endTemp = PeriodFinder.findEndPeriod(listEnd,end,cacheCapacity);
			}
			listEnd = endTemp;
			optionMapTemp.put("start", startTemp);
			optionMapTemp.put("end", endTemp);
			List<Map<String, Object>> dataListTemp = hqFinder.getGpHq(dataName, optionMapTemp);
			setDataList(dataListTemp);
			if(endTemp.compareTo(end)>=0){
				findOver = true;
			}
		}else{
			findOver = true;
		}
		if(findOver){
			Future future = futureMap.remove(dataName);
			future.cancel(true);
		}
		
	}*/
	
	
}
