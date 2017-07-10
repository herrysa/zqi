package com.zqi.strategy.signal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;

public abstract class QSignal {

	protected Set<Map<String, Object>> buySet = new HashSet<Map<String,Object>>();
	protected Set<Map<String, Object>> sellSet = new HashSet<Map<String,Object>>();
	protected HQDataHandler hqDataHandler;
	protected QuantContext quantContext;
	
	public QSignal(HQDataHandler hqDataHandler,QuantContext quantContext){
		this.hqDataHandler = hqDataHandler;
		this.quantContext = quantContext;
	}
	public Set<Map<String, Object>> getBuySet() {
		return buySet;
	}
	public void setBuySet(Set<Map<String, Object>> buySet) {
		this.buySet = buySet;
	}
	public Set<Map<String, Object>> getSellSet() {
		return sellSet;
	}
	public void setSellSet(Set<Map<String, Object>> sellSet) {
		this.sellSet = sellSet;
	}
	
	public abstract void signal(Set<Map<String, Object>> dataSet);
}
