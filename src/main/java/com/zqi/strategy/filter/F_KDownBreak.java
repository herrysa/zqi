package com.zqi.strategy.filter;

import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;

/**
 * @author Administrator
 * k
 */
public class F_KDownBreak extends QFilter{
	
	private int ma = 10;
	public F_KDownBreak(HQDataHandler hqDataHandler, QuantContext quantContext,int ma) {
		super(hqDataHandler, quantContext);
		this.ma = ma;
	}

	

	@Override
	public Set<Map<String, Object>> filter(Set<Map<String, Object>> dataSet) {
		for (Map<String, Object> hqDataMap : dataSet) {
			//String code = hqDataMap.get("code").toString();
			
			Double close_eam_1 = (Double)hqDataMap.get("close_ema_"+ma);
			
			if(close_eam_1==null||close_eam_1==0){
				continue;
			}
			Double close = (Double)hqDataMap.get("close");
			
			if(close.doubleValue()<close_eam_1){
				this.dataSet.add(hqDataMap);
			}
		}
		return this.dataSet;
	}

}
