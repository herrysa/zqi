package com.zqi.strategy.signal.ma;

import java.util.Map;
import java.util.Set;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;
import com.zqi.strategy.signal.QSignal;

public class T_MACross extends QSignal{
	
	private int ma1 = 5;
	private int ma2 = 10;
	private int ma3 = 20;
	
	public T_MACross(HQDataHandler hqDataHandler,QuantContext quantContext , int ma1 , int ma2, int ma3) {
		super(hqDataHandler, quantContext);
		this.ma1 = ma1;
		this.ma2 = ma2;
		this.ma3 = ma3;
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
			Double close_eam_3 = (Double)hqDataMap.get("close_ema_"+ma3);
			
			if(close_eam_1==null||close_eam_1==0){
				continue;
			}
			if(close_eam_2==null||close_eam_2==0){
				continue;
			}
			if(close_eam_3==null||close_eam_3==0){
				continue;
			}
			Double close = (Double)hqDataMap.get("close");
			Object flag = quantContext.getContext(code+"_cross");
			Object flag2 = quantContext.getContext(code+"_cross2");
			if(flag==null){
				if(close_eam_1<close_eam_2&&close_eam_3>close_eam_2){
					quantContext.setContext(code+"_cross1", "0");
					quantContext.setContext(code+"_cross2", "0");
				}
			}else{
				if("0".equals(flag.toString())&&close_eam_1>close_eam_2&&close_eam_3>close_eam_1){
					quantContext.setContext(code+"_cross1", "1");
					buySet.add(hqDataMap);
				}
				if("1".equals(flag.toString())&&close_eam_1<close_eam_2){
					quantContext.setContext(code+"_cross1", "0");
					sellSet.add(hqDataMap);
				}
				if("1".equals(flag.toString())&&"0".equals(flag2.toString())){
					quantContext.setContext(code+"_cross1", "0");
					sellSet.add(hqDataMap);
				}
			}
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
