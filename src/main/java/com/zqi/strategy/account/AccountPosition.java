package com.zqi.strategy.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.DecimalUtil;


public class AccountPosition implements Cloneable{

	private String transCode;
	private String period;
	private String code;
	private Double amount;//持仓数量
	private Double cash;//资金
	private Double avgPrice;//买入平均价
	private Double money;//成本金额
	private Double cost;//交易费用
	private Double nowCap;//当前市值
	private Double yield;//当前收益率
	private Double close;//当前价格
	private Double fhRate = 1d;
	private boolean shouldSell = false;

	private List<Map<String,Object>> transMap;

	public void addTrans(Map<String,Object> trans){
		if(transMap==null){
			transMap = new ArrayList<Map<String,Object>>();
		}
		transMap.add(trans);
	}
	
	public Map<String,Object> popTrans(){
		return transMap.remove(0);
	}
	
	public boolean isEmpty(){
		if(transMap.isEmpty()){
			return true;
		}else{
			return false;
		}
	}

	public List<Map<String, Object>> getTransMap() {
		return transMap;
	}

	public void setTransMap(List<Map<String, Object>> transMap) {
		this.transMap = transMap;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Double getYield() {
		if(close!=null){
			yield  = DecimalUtil.percent(amount*close,(money+cost));
		}
		return yield;
	}

	public void setYield(Double yield) {
		this.yield = yield;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}
	public double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public Double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getNowCap() {
		nowCap = DecimalUtil.scale(amount*close);
		return nowCap;
	}

	public void setNowCap(Double nowCap) {
		this.nowCap = nowCap;
	}
	
	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	
	public Double getFhRate() {
		return fhRate;
	}

	public void setFhRate(Double fhRate) {
		this.fhRate = fhRate;
	}
	
	public boolean isShouldSell() {
		return shouldSell;
	}

	public void setShouldSell(boolean shouldSell) {
		this.shouldSell = shouldSell;
	}
	
	@Override
	public AccountPosition clone() {
		AccountPosition o = null;
		try {
			o = (AccountPosition) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
