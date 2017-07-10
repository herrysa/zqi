package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zqi.strategy.StrategyOut.OUTTYPE;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.account.AccountPosition;
import com.zqi.strategy.account.AccountTrans;

public class StrategyOutProcesser {

	private Map<String , StrategyOut> outMap = new HashMap<String, StrategyOut>();
	
	public Map<String, StrategyOut> getOutMap() {
		return outMap;
	}

	public void setOutMap(Map<String, StrategyOut> outMap) {
		this.outMap = outMap;
	}
	
	public void addOut(String name,StrategyOut strategyOut){
		outMap.put(name, strategyOut);
	}
	
	public StrategyOut addOut(String name , OUTTYPE type ){
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName(name);
		strategyOut.setType(type);
		this.outMap.put(name,strategyOut);
		return strategyOut;
	}
	public StrategyOut addAccuOut(String name , OUTTYPE type ){
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName(name);
		strategyOut.setType(type);
		strategyOut.setAccu(true);
		this.outMap.put(name,strategyOut);
		return strategyOut;
	}
	
	public void putValue(String name,Object value){
		StrategyOut strategyOut = this.outMap.get(name);
		strategyOut.addValue(value);
	}
	
	public void putValues(String name,List<Object> values){
		StrategyOut strategyOut = this.outMap.get(name);
		strategyOut.addValues(values);
	}
	
	@SuppressWarnings("unchecked")
	public void putAccuValue(String name,Object value){
		StrategyOut strategyOut = this.outMap.get(name);
		List<Object> values = strategyOut.getValues();
		if(values==null||values.isEmpty()){
			strategyOut.addValue(value);
		}else{
			Map<String,Object> valueSum = (Map<String,Object>)values.get(0);
			Map<String,Object> valueTemp = (Map<String,Object>)value;
			Set<String> valueKeySet = valueTemp.keySet();
			for(String valueKey : valueKeySet){
				String vStr = valueTemp.get(valueKey).toString();
				BigDecimal v = new BigDecimal(vStr);
				String sumVStr = valueSum.get(valueKey).toString();
				BigDecimal sumV = new BigDecimal(sumVStr);
				sumV = sumV.add(v);
				valueSum.put(valueKey, sumV);
			}
			strategyOut.addValueOnlyOne(valueSum);
		}
	}
	
	public void dealAccountOut(Account mainAccount){
		dealXDataOut(mainAccount);
		dealYieldOut(mainAccount);
		dealMYieldOut(mainAccount);
		dealTransOut(mainAccount);
		dealPosiOut(mainAccount);
		dealWholeIndexOut(mainAccount);
	}
	
	private void dealXDataOut(Account mainAccount){
		List<String> xData = mainAccount.getxData();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName("xData");
		strategyOut.setType(OUTTYPE.x);
		List<Object> values = new ArrayList<Object>();
		values.addAll(xData);
		strategyOut.setValues(values);
		outMap.put("xData",strategyOut);
	}
	
	private void dealYieldOut(Account mainAccount){
		List<Double> yieldRates = mainAccount.getYieldRates();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName("收益率");
		List<Object> values = new ArrayList<Object>();
		values.addAll(yieldRates);
		strategyOut.setType(OUTTYPE.line);
		strategyOut.setValues(values);
		outMap.put("收益率",strategyOut);
	}
	
	private void dealMYieldOut(Account mainAccount){
		List<Double> markYieldRates = mainAccount.getMarkYieldRates();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName(mainAccount.getBenchmarkName());
		List<Object> values = new ArrayList<Object>();
		values.addAll(markYieldRates);
		strategyOut.setType(OUTTYPE.line);
		strategyOut.setValues(values);
		outMap.put(mainAccount.getBenchmarkName(),strategyOut);
	}
	
	private void dealTransOut(Account mainAccount){
		List<AccountTrans> transList = mainAccount.getTransactionList();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName("trans");
		List<Object> values = new ArrayList<Object>();
		values.addAll(transList);
		strategyOut.setType(OUTTYPE.table);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cols", "period,code,amount,price,cost,cash,cap,speriod,samount,sprice,scost,scash,scap,pl");
		strategyOut.setParamMap(paramMap);
		strategyOut.setValues(values);
		outMap.put("trans",strategyOut);
	}
	
	
	
	private void dealPosiOut(Account mainAccount){
		List<AccountPosition> posiList = mainAccount.getPositionList();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName("posi");
		List<Object> values = new ArrayList<Object>();
		values.addAll(posiList);
		strategyOut.setType(OUTTYPE.table);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cols", "period,code,amount,buyprice,cash,cap");
		strategyOut.setParamMap(paramMap);
		strategyOut.setValues(values);
		outMap.put("posi",strategyOut);
	}
	
	private void dealWholeIndexOut(Account mainAccount){
		String wholeIndexTxt = mainAccount.getWholeIndexTxt();
		StrategyOut strategyOut = new StrategyOut();
		strategyOut.setName("wholeIndex");
		List<Object> values = new ArrayList<Object>();
		values.add(wholeIndexTxt);
		strategyOut.setType(OUTTYPE.txt);
		strategyOut.setValues(values);
		outMap.put("wholeIndex",strategyOut);
	}
	
	public List<StrategyOut> dealOut(){
		Set<String> outKeySet =  outMap.keySet();
		List<StrategyOut> outList = new ArrayList<StrategyOut>();
		for(String outKey :outKeySet){
			StrategyOut strategyOut = outMap.get(outKey);
			/*boolean accu = strategyOut.isAccu();
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
			}*/
			outList.add(strategyOut);
		}
		return outList;
	}
}
