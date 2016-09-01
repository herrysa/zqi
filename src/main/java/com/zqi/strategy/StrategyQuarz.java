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
	private Map<String,Map<String,Map<String, Object>>> wholeDataMap;
	//private String dataType;
	private Map<String, String> wholeOut;
	private int step = 1;
	private int dataRangeFrom = 0;
	private int dataRangeTo = 1;
	//private boolean needAccount = false;
	//private Map<String, Account> accountMap;
	private Account mainAccount;
	private Account markAccount;
	private Map<String,Map<String,Map<String, Object>>> markDataMap;
	
	@SuppressWarnings("unchecked")
	public void init(String fileName){
		try {
			strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
			strategy = strategyFactoy.getStrategy(fileName);
			String wholeOutStr = strategy.getInitParam("wholeOut");
			Gson gson = new Gson();
			wholeOutStr = strategy.getInitParam("out");
			wholeOut = gson.fromJson(wholeOutStr, Map.class);
			String order = wholeOut.get("order");
			if(order!=null&&!"".equals(order)){
				//needAccount = true;
				//accountMap = new HashMap<String, Account>();
				//Map<String, String> title = strategy.getTitle();
				//String mainCode = title.get("code");
				String capital_base = strategy.getInitParam("capital_base");
				Long capitalBase = Long.parseLong(capital_base);
				mainAccount = new Account(capitalBase);
				
				//String benchmark = strategy.getInitParam("benchmark");
				markAccount = new Account();
				markDataMap = strategy.getDataMap("benchmarkData");
				
			}
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
			wholeDataMap = strategy.getDataMap("codeData");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Strategy run(){
		Map<String , StrategyOut> strategyOutMap = new HashMap<String, StrategyOut>();
		try {
			Set<String> wholeKeySet = wholeDataMap.keySet();
			for(String wholeKey : wholeKeySet){
				Map<String,Map<String, Object>> dataMap = wholeDataMap.get(wholeKey);
				TreeMap<String,Map<String, Object>> treeDataMap = new TreeMap<String, Map<String,Object>>();
				treeDataMap.putAll(dataMap);
				Set<String> dataKeySet = treeDataMap.keySet();
				List<String> xDataList = new ArrayList<String>();
				xDataList.addAll(dataKeySet);
				double time = Math.ceil(xDataList.size()/step);
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
						
						StrategyOut wholeStrategyOut = strategyOutMap.get(outNname);
						if(wholeStrategyOut==null){
							wholeStrategyOut = strategyOut.clone();
							String wholeType = wholeOut.get(outNname);
							wholeStrategyOut.setType(wholeType);
							strategyOutMap.put(outNname, wholeStrategyOut);
						}
						
						if("order".equals(outNname)){
							Map<String,Object> value = (Map)values.get(0);
							String code = value.get("code").toString();
							String otype = value.get("otype").toString();
							BigDecimal amount = (BigDecimal)value.get("amount");
							BigDecimal price = (BigDecimal)value.get("price");
							mainAccount.order(code, amount, price, otype);
						}else if("accu".equals(outType)){
							//categoryData = values;
							List<Object> wholeVlues = wholeStrategyOut.getValues();
							if(wholeVlues==null){
								wholeStrategyOut.addValues(values);
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
								wholeStrategyOut.setValues(wholeVlues);
								strategyOutMap.put(outNname,wholeStrategyOut);
							}
						}else{
							wholeStrategyOut.addValues(values);
						}
					}
				}
				Set<String> outKeySet =  strategyOutMap.keySet();
				List<StrategyOut> outList = new ArrayList<StrategyOut>();
				for(String outKey :outKeySet){
					StrategyOut strategyOut = strategyOutMap.get(outKey);
					outList.add(strategyOut);
				}
				strategy.setOutList(outList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strategy;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(3/2);
	}
}
