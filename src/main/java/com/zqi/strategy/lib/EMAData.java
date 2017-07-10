package com.zqi.strategy.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;


@Component("emaData")
public class EMAData extends DataMethod{

	
	@Override
	public void execute(List<Map<String, Object>> dataList) {
		Map<String, double[]> needDealDataMap = new HashMap<String, double[]>();
		int dataLength = dataList.size();
		Gson gson = new Gson();
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
					String d = null;
					Object dObj = data.get("d");
					if(dObj!=null){
						d = dObj.toString();
					}
					
					Map<String, Object> dMap = null;
					if(!StringUtils.isEmpty(d)){
						dMap = gson.fromJson(d, Map.class);
						Double dValue= (Double)dMap.get(col+"_p");
						if(dValue!=null){
							dataArr[i] = dValue;
						}
					}
					
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
				double ema[] = new double[length];
				RetCode retCode = lib.sma(beginIndex,dataLength-1,dataArr,v.intValue(),outBegIdx,outNbElement,ema);
				if(retCode==RetCode.Success){
					for(int i = outBegIdx.value;i<dataList.size();i++){
						BigDecimal emaObj = new BigDecimal(ema[i-outBegIdx.value]);
						emaObj = emaObj.setScale(3, BigDecimal.ROUND_HALF_UP);
						Map<String, Object> data = dataList.get(i);
						data.put(key+"_ema_"+v.intValue(), emaObj.doubleValue());
					}
				}
				/*for(){
					
				}*/
			}
		}
	}

	public static void main(String[] args) {
		Core lib = new Core();
		MInteger outBegIdx = new MInteger(); 
		MInteger outNbElement = new MInteger();
		double[] dataArr = {14.55,14.71,14.7,15.07,15.35};
		double[] dataArr2 = new double[10];
		RetCode retCode = lib.ema(0,4,dataArr,5,outBegIdx,outNbElement,dataArr2);
		System.out.println();
	}
}
