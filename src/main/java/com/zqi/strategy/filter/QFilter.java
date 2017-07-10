package com.zqi.strategy.filter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;

public abstract class QFilter {

	protected Set<Map<String, Object>> dataSet = new HashSet<Map<String,Object>>();
	
	protected HQDataHandler hqDataHandler;
	protected QuantContext quantContext;
	
	public QFilter(HQDataHandler hqDataHandler,QuantContext quantContext){
		this.hqDataHandler = hqDataHandler;
		this.quantContext = quantContext;
	}
	public abstract Set<Map<String, Object>> filter(Set<Map<String, Object>> dataSet);
}
