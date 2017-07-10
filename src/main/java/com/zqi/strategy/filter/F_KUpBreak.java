package com.zqi.strategy.filter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;

/**
 * @author Administrator
 * k
 */
public class F_KUpBreak extends QFilter{

	private int ma = 30;
	public F_KUpBreak(HQDataHandler hqDataHandler, QuantContext quantContext,int ma) {
		super(hqDataHandler, quantContext);
		this.ma = ma;
	}

	

	@Override
	public Set<Map<String, Object>> filter(Set<Map<String, Object>> dataSet) {
		Set<Map<String, Object>> gzSet = new HashSet<Map<String,Object>>();
		for (Map<String, Object> hqDataMap : dataSet) {
			String code = hqDataMap.get("code").toString();
			
			Double close_eam_1 = (Double)hqDataMap.get("close_ema_"+ma);
			if(close_eam_1==null||close_eam_1==0){
				continue;
			}
			Double close = (Double)hqDataMap.get("close");
			Double changepercent = (Double)hqDataMap.get("changepercent");
			
			Integer onMATime = (Integer)quantContext.getContext(code+"_onMA");
			if(onMATime==null){
				if(3>changepercent&&changepercent>0){
					quantContext.setContext(code+"_onMA", 1);
					gzSet.add(hqDataMap);
				}
			}else{
				if(close>close_eam_1){
					if(onMATime<2){
						onMATime++;
						quantContext.setContext(code+"_onMA", onMATime);
						gzSet.add(hqDataMap);
					}else{
						this.dataSet.add(hqDataMap);
						quantContext.removeContext(code+"_onMA");
					}
				}else{
					quantContext.removeContext(code+"_onMA");
				}
				
			}
			
		}
		quantContext.setContext("gzSet", gzSet);
		/*for (Map<String, Object> hqDataMap : dataSet2) {
			//String code = hqDataMap.get("code").toString();
			
			Double close = (Double)hqDataMap.get("close");
			Double close_eam_2 = (Double)hqDataMap.get("close_ema_30");
			
			if(close.doubleValue()<close_eam_2){
				sellSet.add(hqDataMap);
			}
			
		}*/
		
		return this.dataSet;
	}

}
