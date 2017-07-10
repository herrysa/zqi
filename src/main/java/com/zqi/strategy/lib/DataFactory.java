package com.zqi.strategy.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;


public class DataFactory {
	
    private static Core lib = new Core();

	//{close:[5,10,20]}
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> ema(List<Map<String, Object>> dataList,String param){
		MInteger outBegIdx = new MInteger(); 
		MInteger outNbElement = new MInteger();
		Gson gson = new Gson();
		Map<String, Object> paramMap = gson.fromJson(param, Map.class);
		Set<String> keySet = paramMap.keySet();
		Map<String, double[]> needDealDataMap = new HashMap<String, double[]>();
		int dataLength = dataList.size();
		for( int i=0;i< dataLength;i++){
			Map<String, Object> data = dataList.get(i);
			for( String key : keySet){
				double[] dataArr = needDealDataMap.get(key);
				if(dataArr==null){
					dataArr = new double[dataLength];
					needDealDataMap.put(key,dataArr);
				}
				BigDecimal colValue = (BigDecimal)data.get(key);
				if(colValue==null){
					//System.out.println();
				}else{
					dataArr[i] = colValue.doubleValue();
				}
			}
		}
		/*Map<String, double[]> needDealDataMap2 = new HashMap<String, double[]>();
		for( String key : keySet){
			List<Double> dataArr = needDealDataMap.get(key);
			if(dataArr==null){
				dataArr = new ArrayList<Double>();
				needDealDataMap.put(key,dataArr);
			}
			dataArr.add((Double)data.get(key));
		}*/
		for( String key : keySet){
			List<Double> values = (List<Double>)paramMap.get(key);
			//int[] values = (int[])paramMap.get(key);
			
			for(Double v : values){
				//List<Double> dataArrTemp = needDealDataMap.get(key);
				double[] dataArr = needDealDataMap.get(key);
				/*for(int i=0;i<dataArrTemp.size();i++){
					dataArr[0] = dataArrTemp.get(i).doubleValue();
				}*/
				double ema[] = new double[dataArr.length];
				RetCode retCode = lib.ema(0,dataArr.length-1,dataArr,v.intValue(),outBegIdx,outNbElement,ema);
				if(retCode==RetCode.Success){
					for(int i = outBegIdx.value;i<outNbElement.value;i++){
						Map<String, Object> data = dataList.get(i);
						data.put(key+"_ema_"+v, ema[i]);
					}
				}
				/*for(){
					
				}*/
			}
		}
		return dataList;
			
			
			
			
	}
}
