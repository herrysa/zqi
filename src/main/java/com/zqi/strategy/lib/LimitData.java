package com.zqi.strategy.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.tictactec.ta.lib.RetCode;


@Component("limitData")
public class LimitData extends DataMethod{

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
				BigDecimal colValue = (BigDecimal)data.get(col);
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
				double max[] = new double[length];
				double min[] = new double[length];
				RetCode maxRetCode = lib.max(0,dataLength-1,dataArr,v.intValue(),outBegIdx,outNbElement,max);
				RetCode minRetCode = lib.min(0,dataLength-1,dataArr,v.intValue(),outBegIdx,outNbElement,min);
				Map<String, Object> data = dataList.get(dataList.size()-1);
				if(maxRetCode==RetCode.Success){
					BigDecimal maxObj = new BigDecimal(max[0]);
					maxObj = maxObj.setScale(2, BigDecimal.ROUND_HALF_UP);
					data.put(key+"_max_"+v.intValue(), maxObj.doubleValue());
				}
				if(minRetCode==RetCode.Success){
					BigDecimal minObj = new BigDecimal(min[0]);
					minObj = minObj.setScale(2, BigDecimal.ROUND_HALF_UP);
					data.put(key+"_min_"+v.intValue(), minObj.doubleValue());
				}
			}
		}
	}

	
}
