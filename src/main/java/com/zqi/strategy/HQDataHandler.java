package com.zqi.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.zqi.frame.util.JedisUtils;
import com.zqi.strategy.lib.DataMethod;
import com.zqi.unit.SpringContextHelper;

public class HQDataHandler {

	private String year;
	private HQFinder hqFinder;
	private GpPool gpPool;
	private Map<String, String> optionMap;
	//private List<HQDataBox> hqDataBoxs;
	private Map<String,DataMethod> dataMethodMap = new HashMap<String, DataMethod>();
	/*private int outDateNum = 0;

	public int getOutDateNum() {
		return outDateNum;
	}

	public void setOutDateNum(int outDateNum) {
		this.outDateNum = outDateNum;
	}*/

	//private TreeMap<String, HQDataBox> hqDataBoxMap;
	public HQDataHandler(){
		
	}
	
	public HQDataHandler(GpPool gpPool,Map<String, String> optionMap,HQFinder hqFinder){
		this.gpPool = gpPool;
		this.optionMap = optionMap;
		this.hqFinder = hqFinder;
	}
	
	public HQFinder getHqFinder(){
		return hqFinder;
	}
	
	public void setHqFinder(HQFinder hqFinder) {
		this.hqFinder = hqFinder;
	}
	
	public Map<String, String> getOptionMap() {
		return optionMap;
	}

	public void setOptionMap(Map<String, String> optionMap) {
		this.optionMap = optionMap;
	}

	public void setGpPool(GpPool gpPool) {
		this.gpPool = gpPool;
	}
	
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<HQDataBox> getHqDataBoxs(String period){
		if(hqDataBoxMap==null){
			hqDataBoxMap = new TreeMap<String, HQDataBox>();
		}
		hqFinder.getHQDataBoxMap(gpPool.getCodeStr(), period, optionMap,hqDataBoxMap);
		Collection<HQDataBox> hqDataBoxs = hqDataBoxMap.values();
		for(DataMethod dataMethod : dataMethods){
			for(HQDataBox hqDataBox : hqDataBoxs){
				List<Map<String, Object>> dataList = hqDataBox.getDataList();
				//dataList = dealOutDateData(dataList);
				//hqDataBox.setDataList(dataList);
				dataMethod.execute(dataList);
			}
		}
		return hqDataBoxs;
	}*/
	
	public Collection<HQDataBox> getHqDataBoxs(String period){
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10); 
		//TODO
		Collection<HQDataBox> hqDataBoxs = null;//hqDataBoxMap.values();
		Iterator<HQDataBox> hqDataBoxIt= hqDataBoxs.iterator();
		int i = 0;
		while(hqDataBoxIt.hasNext()){
			HQDataBox hqDataBox = hqDataBoxIt.next();
			hqDataBox.setPeriod(period);
			fixedThreadPool.execute(hqDataBox);
		}
		fixedThreadPool.shutdown();
		try {
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hqDataBoxs;
	}
	
	/*public Collection<HQDataBox> getHqDataBoxs(){
		return hqDataBoxMap.values();
	}
	
	public void initHqDataBoxs(){
		if(hqDataBoxMap==null){
			hqDataBoxMap = new TreeMap<String, HQDataBox>();
		}
		for(String code : gpPool.getCodeArr()){
			HQDataBox dataBox = new HQDataBox();
			dataBox.setCode(code);
			//dataBox.setHqFinder(hqFinder);
			hqDataBoxMap.put(code,dataBox);
		}
	}*/
	
	public Map<String,Object> getRHQData(String code , String period){
		return hqFinder.getRHQData(code, period);
		//return (Map<String,Object>)JedisUtils.toObject(pipeline.get(JedisUtils.getBytesKey(code+":"+period)));
	}
	
	public HQDataHandler addDataMethod(String name,String param){
		DataMethod dataMethod = (DataMethod)SpringContextHelper.getBean(name+"Data");
		dataMethod.setParam(param);
		dataMethodMap.put(name, dataMethod);
		return this;
	}
	
	public Map<String,Object> getIndexData(String name , String code , String period){
		DataMethod dataMethod = dataMethodMap.get(name);
		if(dataMethod==null){
			return null;
		}
		int dataLength = dataMethod.getDataLength();
		if(dataLength>0){
			//TestTimer t = new TestTimer("before");
			//t.begin();
			List<Map<String, Object>> dataList = beforeDatas(code,period,dataLength);
			if(dataList.size()>0){
				dataMethod.execute(dataList);
				return dataList.get(dataList.size()-1);
			}else{
				return null;
			}
			//t.done();
			//TestTimer t2 = new TestTimer("exe");
		///	t2.begin();
			
		}else{
			return null;
		}
	}
	
	public List<Map<String, Object>> beforeDatas(String code , String period , int days){
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		String prePeriod = period;
		while(true){
			if(prePeriod==null||dataList.size()==days){
				break;
			}else{
				Map<String, Object> data = getRHQData(code,prePeriod);
				if(data==null){
					return dataList;
				}
				dataList.add(data);
				Object pPeriod = data.get("prePeriod");
				if(pPeriod!=null){
					prePeriod = pPeriod.toString();
				}else{
					prePeriod = null;
				}
			}
		}
		Collections.reverse(dataList);
		return dataList;
	}
	
	/*public List<Map<String, Object>> dealOutDateData(List<Map<String, Object>> dataList){
		if(outDateNum>0){
			int start = dataList.size()-outDateNum;
			if(start>0){
				return dataList.subList(start,dataList.size());
			}else{
				return dataList;
			}
		}else{
			return dataList;
		}
	}*/
	
	public List<Map<String, Object>> getHqDataList(String period){
		return hqFinder.getGpHq(year , gpPool.getCodeStr() , period, optionMap);
	}
	
	public List<Object> getAllHqDataListByRedis(String period){
		return (List<Object>)JedisUtils.getObjectList(period);
	}
	
	public List<Map<String, Object>> getAllHqDataList(String period){
		return hqFinder.getGpPeriodHq(year, period);
	}
	
	@SuppressWarnings("unchecked")
	public static void dealRHQData(Map<String,Object> data){
		String d = null;
		Gson gson = new Gson();
		Object dObj = data.get("d");
		if(dObj!=null){
			d = dObj.toString();
		}
		
		Map<String, Object> dMap = null;
		if(!StringUtils.isEmpty(d)){
			dMap = gson.fromJson(d, Map.class);
			data.putAll(dMap);
		}
	}
	
	/*public List<HQDataBox> getHqDataBoxs() {
		return hqFinder.getHQDataBoxs(gpPool.getCodeArr(), optionMap);
	}*/
	
	/*public List<Map<String, Object>> getHqDataList(){
		return hqFinder.getGpHq(gpPool.getCodeStr(), null, optionMap);
	}*/
	
	public GpPool getGpPool(){
		return gpPool;
	}
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
