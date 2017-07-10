package com.zqi.strategy.signal.ma;

import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;
import com.zqi.strategy.signal.QSignal;

public class S_MACross extends QSignal{
	
	private int ma1 = 10;
	private int ma2 = 30;
	
	public S_MACross(HQDataHandler hqDataHandler,QuantContext quantContext , int ma1 , int ma2) {
		super(hqDataHandler, quantContext);
		this.ma1 = ma1;
		this.ma2 = ma2;
	}

	@Override
	public void signal(Set<Map<String, Object>> dataSet) {
		for (Map<String, Object> hqDataMap : dataSet) {
			//String code = codeArr[i];
			// hqDataMap = (Map<String, Object>)dataList.get(i);//hqDataHandler.getRHQData(code, period);
			//HQDataHandler.dealRHQData(hqDataMap);
			String code = hqDataMap.get("code").toString();
			
			Double close_eam_1 = (Double)hqDataMap.get("close_ema_"+ma1);
			Double close_eam_2 = (Double)hqDataMap.get("close_ema_"+ma2);
			
			if(close_eam_1==null||close_eam_1==0){
				continue;
			}
			if(close_eam_2==null||close_eam_2==0){
				continue;
			}
			Double close = (Double)hqDataMap.get("close");
			Object flag = quantContext.getContext(code+"_cross");
			if(close_eam_1>close_eam_2){
				if(flag!=null){
					if("0".equals(flag.toString())){
						if(close.doubleValue()>close_eam_1){
							buySet.add(hqDataMap);
						}
					}else{
						if(close.doubleValue()<close_eam_2){
							sellSet.add(hqDataMap);
						}
					}
				}
				quantContext.setContext(code+"_cross", "1");
			}else{
				if(flag!=null){
					if("1".equals(flag.toString())){
						sellSet.add(hqDataMap);
					}
				}
				quantContext.setContext(code+"_cross", "0");
			}
		}
	}

}
