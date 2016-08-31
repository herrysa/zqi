package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.zqi.unit.SpringContextHelper;

@Component("strategyQuarz")
public class StrategyQuarz {

	private StrategyFactoy strategyFactoy;
	private Strategy strategy;
	private Map<String , StrategyOut> strategyOutMap;
	private Map<String,Map<String,Map<String, Object>>> wholeDataMap = null;//对应dataType为1是的数据形式，但每次run的都是dataMap类型的数据
	//private String dataType;
	private String wholeOut;
	private int step = 1;
	private int dataRangeFrom = 0;
	private int dataRangeTo = 1;
	
	public void init(String fileName){
		try {
			strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
			strategy = strategyFactoy.getStrategy(fileName);
			wholeOut = strategy.getInitParam("wholeOut");
			String stepStr = strategy.getInitParam("step");
			if(stepStr!=null&&!"".equals(stepStr)){
				step = Integer.parseInt(stepStr);
			}
			String dataRangeFromStr = strategy.getInitParam("dataRangeFrom");
			if(dataRangeFromStr!=null&&!"".equals(dataRangeFromStr)){
				dataRangeFrom = Integer.parseInt(dataRangeFromStr);
			}
			String dataRangeToStr = strategy.getInitParam("dataRangeTo");
			if(dataRangeToStr!=null&&!"".equals(dataRangeToStr)){
				dataRangeTo = Integer.parseInt(dataRangeToStr);
			}
			wholeDataMap = strategy.getCodeData();
			
			strategyOutMap = new HashMap<String, StrategyOut>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			Set<String> wholeKeySet = wholeDataMap.keySet();
			for(String wholeKey : wholeKeySet){
				Map<String,Map<String, Object>> dataMap = wholeDataMap.get(wholeKey);
				TreeMap<String,Map<String, Object>> treeDataMap = new TreeMap<String, Map<String,Object>>();
				treeDataMap.putAll(dataMap);
				Set<String> dataKeySet = treeDataMap.keySet();
				List<String> xDataList = new ArrayList<String>();
				xDataList.addAll(dataKeySet);
				int time = xDataList.size()/step+1;
				for(int i=0;i<time;i++){
					String period = xDataList.get(i);
					int f = i-dataRangeFrom;
					int t = i+dataRangeTo;
					if(f<0){
						f = 0;
					}
					if(t>xDataList.size()){
						t = xDataList.size();
					}
					List<String> xData = xDataList.subList(f, t);
					Map<String,Map<String, Object>> dataMapTemp = treeDataMap.subMap(xData.get(0), true,xData.get(xData.size()-1),true);
					JSONArray xJson = JSONArray.fromObject(xData);
					String xJsonStr = xJson.toString();
					strategy.setInitParam("xData", xJsonStr);
					//JSONObject dataJson = JSONObject.fromObject(dataMap);
					Gson dataJson = new Gson();
					String dataJsonStr = dataJson.toJson(dataMapTemp);
					strategy.setInitParam("codeData", dataJsonStr);
					strategy.setInitParam("current_date", "'"+period+"'");
					strategy.eval();
					List<StrategyOut> outList = strategy.getOutList();
					for(StrategyOut strategyOut :outList){
						String outNname = strategyOut.getName();
						String outType = strategyOut.getType();
						List<Object> values = strategyOut.getValues();
						
						StrategyOut wholeOut = strategyOutMap.get(outNname);
						if(wholeOut==null){
							strategyOutMap.put(outNname, strategyOut);
						}else{
							if("accu".equals(outType)){
								//categoryData = values;
								List<Object> wholeVlues = wholeOut.getValues();
								if(wholeVlues==null){
									wholeOut.addValues(values);
								}else{
									Object value = values.get(0);
									JSONObject valueJson = JSONObject.fromObject(value);
									Object wholeValue = wholeVlues.get(0);
									JSONObject wholeValueJson = JSONObject.fromObject(wholeValue);
									Set<String> valueKeySet = valueJson.keySet();
									for(String valueKey : valueKeySet){
										Object v = valueJson.getString(valueKey);
										Object wholeV = wholeValueJson.getString(valueKey);
										BigDecimal vDecimal = new BigDecimal(v.toString());
										BigDecimal wholevDecimal = new BigDecimal(wholeV.toString());
										wholevDecimal = wholevDecimal.add(vDecimal);
										wholeValueJson.put(valueKey, wholevDecimal.toString());
									}
									wholeVlues.clear();
									wholeVlues.add(wholeValueJson.toString());
									wholeOut.setValues(wholeVlues);
								}
							}else{
								wholeOut.addValues(values);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(3/2);
	}
}
