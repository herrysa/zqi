package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.strategy.StrategyOut.OUTTYPE;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.account.AccountPosition;
import com.zqi.unit.SpringContextHelper;

@Component("strategyQuarzJs")
@Scope("prototype")
public class StrategyQuarzJs implements IStrategyQuarz{

	private ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	private StrategyJsFactoy strategyFactoy;
	private StrategyJs strategy;
	private Map<String,Object> wholeDataMap;
	//private String dataType;
	private Map<String, String> wholeOut;
	private int step = 1;
	private int dataRangeFrom = 0;
	private int dataRangeTo = 1;
	private boolean needAccount = false;
	//private Map<String, Account> accountMap;
	private Account mainAccount;
	
	@SuppressWarnings("unchecked")
	public void init(String fileName){
		try {
			strategyFactoy = (StrategyJsFactoy)SpringContextHelper.getBean("strategyFactoy");
			strategy = strategyFactoy.getStrategy(fileName);
			String wholeOutStr = strategy.getInitParam("wholeOut");
			Gson gson = new Gson();
			wholeOut = gson.fromJson(wholeOutStr, Map.class);
			if(wholeOut==null){
				wholeOut = new HashMap<String, String>();
			}
			String account = strategy.getInitParam("account");
			//Map<String,String>  out = gson.fromJson(outStr, Map.class);
			//String order = out.get("order");
			if(account!=null&&"1".equals(account)){
				needAccount = true;
				//accountMap = new HashMap<String, Account>();
				//Map<String, String> title = strategy.getTitle();
				//String mainCode = title.get("code");
				String capital_base = strategy.getInitParam("capital_base");
				Long capitalBase = 1000000L;
				if(capital_base!=null){
					capitalBase = Long.parseLong(capital_base);
				}
				//TODO
				//mainAccount = new Account(capitalBase,zqiDao);
				
				//String benchmark = strategy.getInitParam("benchmark");
				Map<String,Object> markDataMap = strategy.getDataMap("benchmarkData");
				//Map<String, Map<String, Object>> markData = markDataMap.get(benchmark);
				//TODO account dataMap
				//mainAccount.setMarkData(markDataMap);
				strategy.setJsParam("benchmarkData", "''");
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
			if(wholeDataMap==null){
				wholeDataMap = new HashMap<String, Object>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public StrategyJs run(){
		Map<String , StrategyOut> strategyOutMap = new HashMap<String, StrategyOut>();
		try {
			Set<String> wholeKeySet = wholeOut.keySet();
			for(String wholeKey : wholeKeySet){
				StrategyOut wholeStrategyOut = new StrategyOut();
				wholeStrategyOut.setName(wholeKey);
				Object paramObj = wholeOut.get(wholeKey);
				String wholeType = null;
				if(paramObj instanceof Map){
					Map<String, Object> paramMap = (Map<String, Object>)paramObj;
					wholeType = paramMap.get("type").toString();
					Boolean accu = (Boolean)paramMap.get("accu");
					wholeStrategyOut.setParamMap(paramMap);
					if(accu!=null){
						wholeStrategyOut.setAccu(accu);
					}
				}else{
					wholeType = paramObj.toString();
				}
				wholeStrategyOut.setType(OUTTYPE.valueOf(wholeType));
				strategyOutMap.put(wholeKey, wholeStrategyOut);
			}
			Set<String> periodKeySet = wholeDataMap.keySet();
			//wholeDataMap.values();
			TreeMap<String,Object> treeDataMap = new TreeMap<String,Object>();
			//int i = 0;
			for(String periodKey : periodKeySet){
				Object dataObj = wholeDataMap.get(periodKey);
				/*if(dataObj instanceof List){
					treeDataMap.put(periodKey,(List<Map<String, Object>>)dataObj);
				}else{
					treeDataMap.put(periodKey,(Map<String, Object>)dataObj);
				}*/
				treeDataMap.put(periodKey,dataObj);
			}
			periodKeySet = treeDataMap.keySet();
			List<String> xDataList = new ArrayList<String>();
			xDataList.addAll(periodKeySet);
			//for(String periodKey : periodKeySet){
				//Object dataObj = treeDataMap.get(periodKey);
				//treeDataMap.putAll(dataMap);
				//Set<String> dataKeySet = treeDataMap.keySet();
			String contextStr = "{}";
			double time = Math.ceil(xDataList.size()/step);
			for(int i=0;i<time;i=i+step){
				String period = xDataList.get(i);
				int f = i-dataRangeFrom;
				int t = i+dataRangeTo;
				if(f<0){
					continue;
				}
				if(t>xDataList.size()){
					t = xDataList.size();
				}
				List<String> xData = xDataList.subList(f, t);
				String start = xData.get(0);
				String end = xData.get(xData.size()-1);
				Map<String,Object> dataMapTemp = treeDataMap.subMap(start, true,end,true);
				Gson gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd").create();
				
				String xJsonStr = null;
				if(xData.size()==1){
					xJsonStr = xData.get(0);
				}else{
					xJsonStr = gson.toJson(xData);
				}
				//String xJsonStr = xJson.toString();
				if(needAccount){
					mainAccount.setCurrent_date(period);
					strategy.setJavaParam("position", gson.toJson(mainAccount.getPosition()),2);
				}
				strategy.setJavaParam("start", start);
				strategy.setJavaParam("end", end);
				strategy.setJavaParam("runNum", ""+i);
				strategy.setJavaParam("xData", xJsonStr,2);
				strategy.setJavaParam("accountContext", contextStr,2);
				//JSONObject dataJson = JSONObject.fromObject(dataMap);
				//Gson dataJson = new Gson();
				String dataJsonStr = gson.toJson(dataMapTemp);
				strategy.setJavaParam("codeData", dataJsonStr,2);
				strategy.setJavaParam("current_date", period);
				strategy.eval();
				List<StrategyOut> outList = strategy.getOutList();
				for(StrategyOut strategyOut :outList){
					String outNname = strategyOut.getName();
					//String outType = strategyOut.getType();
					List<Object> values = strategyOut.getValues();
					StrategyOut wholeStrategyOut = strategyOutMap.get(outNname);
					if("order".equals(outNname)){
						for(Object value : values){
							Map<String,Object> valueMap = (Map<String,Object>)value;
							String code = valueMap.get("code").toString();
							String otype = valueMap.get("otype").toString();
							BigDecimal amount = new BigDecimal(valueMap.get("amount").toString());
							BigDecimal price = null;
							Object priceObj = valueMap.get("price");
							if(priceObj!=null&&!"undefined".equals(priceObj.toString())){
								price = new BigDecimal(priceObj.toString());
								mainAccount.order(period,code, amount.doubleValue(), price.doubleValue(), otype);
							}
						}
						
					}else if("accountContext".equals(outNname)){
						if(values.size()>0){
							contextStr = gson.toJson(values.get(0));
						}
					}else{
						if(wholeStrategyOut==null){
							wholeStrategyOut = strategyOut.clone();
							strategyOutMap.put(outNname, wholeStrategyOut);
						}else{
							wholeStrategyOut.addValues(values);
						}
					}
				}
			}
			//}
			if(needAccount){
				//mainAccount.balance();
				//List<Map<String, Object>> transList = mainAccount.getTransactionList();
				List<AccountPosition> posiList = mainAccount.getPositionList();
				List<String> xData = mainAccount.getxData();
				//List<BigDecimal> yieldRates = mainAccount.getYieldRates();
				//List<BigDecimal> markYieldRates = mainAccount.getMarkYieldRates();
				Set<String> outKeySet =  wholeOut.keySet();
				for(String outKey :outKeySet){
					StrategyOut strategyOut = strategyOutMap.get(outKey);
					//TODO outTYpe
					/*String outType = strategyOut.getType();
					if("x".equals(outType)){
						List<Object> values = new ArrayList<Object>();
						values.addAll(xData);
						strategyOut.setValues(values);
						//strategyOut.setType("line");
					}else if("yield".equals(outType)){
						List<Object> values = new ArrayList<Object>();
						values.addAll(yieldRates);
						strategyOut.setType("line");
						strategyOut.setValues(values);
					}else if("myield".equals(outType)){
						List<Object> values = new ArrayList<Object>();
						values.addAll(markYieldRates);
						strategyOut.setValues(values);
						strategyOut.setType("line");
					}else if("trans".equals(outType)){
						List<Object> values = new ArrayList<Object>();
						values.addAll(transList);
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("cols", "period,code,amount,price,cash,cap");
						strategyOut.setParamMap(paramMap);
						strategyOut.setValues(values);
						strategyOut.setType("table");
					}else if("posi".equals(outType)){
						List<Object> values = new ArrayList<Object>();
						values.addAll(posiList);
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("cols", "period,code,amount,buyprice,cash,cap");
						strategyOut.setParamMap(paramMap);
						strategyOut.setValues(values);
						strategyOut.setType("table");
					}*/
					strategyOutMap.put(outKey,strategyOut);
				}
			}
			Set<String> outKeySet =  strategyOutMap.keySet();
			List<StrategyOut> outList = new ArrayList<StrategyOut>();
			for(String outKey :outKeySet){
				StrategyOut strategyOut = strategyOutMap.get(outKey);
				boolean accu = strategyOut.isAccu();
				if(accu){
					List<Object> wholeVlues = strategyOut.getValues();
					Map<String,Object> valueSum = null;
					for(Object wholeValue : wholeVlues){
						if(valueSum==null){
							valueSum = (Map<String,Object>)wholeValue;
						}else{
							Map<String,Object> valueTemp = (Map<String,Object>)wholeValue;
							Set<String> valueKeySet = valueTemp.keySet();
							for(String valueKey : valueKeySet){
								String vStr = valueTemp.get(valueKey).toString();
								BigDecimal v = new BigDecimal(vStr);
								String sumVStr = valueSum.get(valueKey).toString();
								BigDecimal sumV = new BigDecimal(sumVStr);
								sumV = sumV.add(v);
								valueSum.put(valueKey, sumV);
							}
						}
					}
					strategyOut.addValueOnlyOne(valueSum);
				}
				outList.add(strategyOut);
			}
			strategy.setOutList(outList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strategy;
	}
	public boolean isNeedAccount() {
		return needAccount;
	}

	public void setNeedAccount(boolean needAccount) {
		this.needAccount = needAccount;
	}
	
	
	public static void main(String[] args) {
		try {
			Gson gson = new GsonBuilder() 
			.setDateFormat("yyyy-MM-dd HH:mm:ss") 
			.create();
			String aa = "{code:benchmark,col:\u0027qqq\u0027,start:start,end:end,gpNum:yyyyyy-1yyyyyy}";
			Map<String, String> aaaaMap = gson.fromJson(aa, Map.class);
			System.out.println(3/2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCustom(String custom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAccountCode(String accountCode) {
		// TODO Auto-generated method stub
		
	}

}
