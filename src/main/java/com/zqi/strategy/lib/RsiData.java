package com.zqi.strategy.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.tictactec.ta.lib.RetCode;


@Component("rsiData")
public class RsiData extends DataMethod{

	
	@Override
	public void execute(List<Map<String, Object>> dataList) {
		Map<String, double[]> needDealDataMap = new HashMap<String, double[]>();
		int dataLength = dataList.size();
		for( int i=0;i< dataLength;i++){
			Map<String, Object> data = dataList.get(i);
			for( String col : colArr){
				double[] dataArr = needDealDataMap.get(col);
				if(dataArr==null){
					dataArr = new double[dataLength];
					needDealDataMap.put(col,dataArr);
				}
				BigDecimal colValue = (BigDecimal)data.get(col+"_p");
				if(colValue==null){
					//System.out.println();
				}else{
					dataArr[i] = colValue.doubleValue();
				}
			}
		}
		Set<String> keySet = needDealDataMap.keySet();
		for( String key : keySet){
			//int[] values = (int[])paramMap.get(key);
			
			for(Double v : valueArr){
				//List<Double> dataArrTemp = needDealDataMap.get(key);
				double[] dataArr = needDealDataMap.get(key);
				if(dataArr.length<v.intValue()){
					continue;
				}
				int length = dataArr.length;
				double rsi[] = new double[length];
				RetCode retCode = lib.rsi(beginIndex,dataLength-1,dataArr,v.intValue(),outBegIdx,outNbElement,rsi);
				if(retCode==RetCode.Success){
					for(int i = outBegIdx.value;i<dataList.size();i++){
						BigDecimal rsiObj = new BigDecimal(rsi[i-outBegIdx.value]);
						rsiObj = rsiObj.setScale(2, BigDecimal.ROUND_HALF_UP);
						Map<String, Object> data = dataList.get(i);
						data.put(key+"_rsi_"+v.intValue(), rsiObj.doubleValue());
					}
				}
				/*for(){
					
				}*/
			}
		}
	}

	
}
