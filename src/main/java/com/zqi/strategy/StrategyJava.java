package com.zqi.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zqi.strategy.account.Account;
import com.zqi.strategy.account.AccountPosition;
import com.zqi.strategy.period.PeriodTimerState;

public abstract class StrategyJava implements IStrategy{

	
	private List<StrategyOut> outList;
	private StrategyTitle title = new StrategyTitle();
	
	protected Double profit = 20d;
	protected Double loss = -10d;
	protected Double ps = 0.1d;
	
	protected String quantName = "";
	
	public String getQuantName() {
		return quantName;
	}

	public void setQuantName(String quantName) {
		this.quantName = quantName;
	}

	public StrategyTitle getTitle() {
		return title;
	}

	public void setTitle(StrategyTitle title) {
		this.title = title;
	}

	public List<StrategyOut> getOutList() {
		return outList;
	}

	public void setOutList(List<StrategyOut> outList) {
		this.outList = outList;
	}
	

	public abstract void init(StrategyContext context);
	
	/*@SuppressWarnings("unchecked")
	public Set<Map<String, Object>> dealDataList(List<Object> dataList){
		Set<Map<String, Object>> dataSet = new HashSet<Map<String,Object>>();
		for(Object data : dataList){
			dataSet.add((Map<String, Object>)data);
		}
		return dataSet;
	}*/
	
	public Set<Map<String, Object>> dealDataList(List<Map<String, Object>> dataList){
		Set<Map<String, Object>> dataSet = new HashSet<Map<String,Object>>();
		dataSet.addAll(dataList);
		return dataSet;
	}
	
	public void profitSell(Account account,String period ,double amount){
		List<AccountPosition> positions = account.getPosition();
		for(AccountPosition accountPosition : positions){
			String code = accountPosition.getCode();
			Double price = accountPosition.getClose();
			Double yield = accountPosition.getYield();
			if(yield!=null&&(yield>=profit||yield<=loss)){
				account.order(period, code, amount, price, "");
			}
		}
	}
	
	public void sell(Account account , String period , Set<Map<String, Object>> sellSet){
		for(Map<String,Object> sellData : sellSet){
			String code = sellData.get("code").toString();
			Double close = (Double)sellData.get("close");
			account.order(period, code, -1, close.doubleValue(), "");
		}
	}
	
	public void buy(Account account , String period , Set<Map<String, Object>> buySet){
		for(Map<String,Object> sellData : buySet){
			String code = sellData.get("code").toString();
			Double close = (Double)sellData.get("close");
			account.order(period, code, ps, close.doubleValue(), "");
		}
	}
	
	public abstract void strategy(HQDataHandler hqDataHandler ,Account account,StrategyOutProcesser outProcesser ,PeriodTimerState periodTimerState);
}
