package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {

	private BigDecimal capital_base = new BigDecimal(1000000);
	private BigDecimal cash = new BigDecimal(0);
	private String current_date;
	private List<Map<String, Object>> transaction;
	private List<Map<String, Object>> position;
	private List<Map<String, Object>> blotter;
	private List<Map<String, Object>> capital;
	private List<Map<String, Object>> assets;
	private int interrupt = 0;
	private BigDecimal current_yield_rate;
	private List<BigDecimal> yield_rate;
	
	public Account(){
		
	}
	
	public Account(Long capital){
		capital_base = new BigDecimal(capital);
		transaction = new ArrayList<Map<String,Object>>();
		position = new ArrayList<Map<String,Object>>();
	}
	
	public void init(){
		this.cash = this.capital_base;
	}
	
	public void changePosition(Map<String, Object> p){
		boolean positioned = false,positionChanged =false;
		String pCode = p.get("code").toString();
		BigDecimal pAmount = (BigDecimal)p.get("amount");
		BigDecimal pPrice = (BigDecimal)p.get("price");
		for(Map<String, Object> c_p : this.position){
			String cpCode = c_p.get("code").toString();
			BigDecimal cpAmount = (BigDecimal)c_p.get("amount");
			BigDecimal cpCap = (BigDecimal)c_p.get("cap");
			if(cpCode.equals(pCode)){
				cpAmount = cpAmount.add(pAmount);
				cpCap = cpAmount.multiply(pPrice);
				positioned =true;
				positionChanged = true;
				break;
			}
		}
		if(!positioned){
			if(pAmount.compareTo(new BigDecimal(0))==1){
				BigDecimal cap = pAmount.multiply(pPrice);
				p.put("cap", cap);
				this.position.add(p);
				positionChanged = true;
			}
		}
		if(positionChanged){
			BigDecimal changeCash = pAmount.multiply(pPrice);;
			Map trans = new HashMap<String, Object>();
			trans.put("period", this.current_date);
			trans.put("code", pCode);
			trans.put("amount", pAmount);
			trans.put("price", pPrice);
			trans.put("cap", changeCash);
			this.transaction.add(trans);
			this.cash = this.cash.subtract(changeCash);
		}
		
	}
	
	public void balance(){
		/*BigDecimal sum = this.cash;
		for(Map<String, Object> c_p : this.position){
			var price = this.code.data[this.current_date].close;
			var cap = c_p.amount.mul(price);
			sum = sum.add(cap);
		}
		this.assets.push(sum);
		var yield_rate = sum.sub(this.capital_base).div(this.capital_base).mul(100);
		this.current_yield_rate = yield_rate;
		this.yield_rate.push(yield_rate);*/
	}
	
	public void order(String code,BigDecimal amount,BigDecimal price,String otype){
		//amount = amount.s
		if(amount.compareTo(new BigDecimal(0))==0){
			return ;
		}
		BigDecimal changeCash = amount.multiply(price);
		BigDecimal remainCash = this.cash.subtract(changeCash);
		int r = remainCash.compareTo(new BigDecimal(0));
		if(r>=0){
			Map<String,Object> p = new HashMap<String,Object>();
			p.put("code", code);
			p.put("amount", amount);
			p.put("price", price);
			this.changePosition(p);
		}else{
			
		}
	}
}
