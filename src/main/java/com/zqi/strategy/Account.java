package com.zqi.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Account {

	private BigDecimal capital_base = new BigDecimal(1000000);
	private BigDecimal cash = new BigDecimal(0);
	private String current_date = null;
	private List<Map<String, Object>> transaction = null;
	private List<Map<String, Object>> position = null;
	private List<Map<String, Object>> blotter = null;
	private List<Map<String, Object>> xData = null;
	private Map code = null;
	private List<Map<String, Object>> capital = null;
	private List<Map<String, Object>> assets = null;
	private int interrupt = 0;
	private BigDecimal current_yield_rate = null;
	private List<BigDecimal> yield_rate = null;
	
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
				//c_p.cap = c_p.amount.mul(p.price);
				cpCap = cpAmount.multiply(pPrice);
				positioned =true;
				positionChanged = true;
				break;
			}
		}
		/*if(!positioned){
			var amount = p.amount;
			if(amount>0){
				var cap = p.amount.mul(p.price);
				p.cap = cap;
				this.position.push(p);
				positionChanged = true;
			}
		}
		if(positionChanged){
			var changeCash = p.amount.mul(p.price);
			var trans = {period:this.current_date,code:p.code,amount:p.amount,price:p.price,cap:changeCash};
			this.transaction.push(trans);
			this.cash -= changeCash;
		}*/
		
	}
}
