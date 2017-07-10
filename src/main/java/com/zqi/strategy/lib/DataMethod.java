package com.zqi.strategy.lib;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;



public abstract class DataMethod implements Cloneable{

	protected Core lib = new Core();
	protected MInteger outBegIdx = new MInteger(); 
	protected MInteger outNbElement = new MInteger();
	//private DataMethodType methodType;
	//private int outDateNum = 0;
	protected int dataLength = 0;
	
	protected int beginIndex = 0;
	
	protected String[] colArr ;
	protected Double[] valueArr;
	
	public int getDataLength() {
		return dataLength;
	}


	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	private String param;
	
	
	public String getParam(){
		return param;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}


	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	
	public String[] getColArr() {
		return colArr;
	}


	public void setColArr(String[] colArr) {
		this.colArr = colArr;
	}


	public Double[] getValueArr() {
		return valueArr;
	}


	public void setValueArr(Double[] valueArr) {
		this.valueArr = valueArr;
	}
	
	@SuppressWarnings("unchecked")
	public void setParam(String param){
		this.param = param;
		Gson gson = new Gson();
		Map<String, Object> paramMap = gson.fromJson(param, Map.class);
		setParam(paramMap);
	}
	
	@SuppressWarnings("unchecked")
	public void setParam(Map<String, Object> paramMap){
		List<String> cols = (List<String>)paramMap.get("col");
		List<Double> values = (List<Double>)paramMap.get("value");
		Integer dl = (Integer)paramMap.get("dataLength");
		if(dl!=null){
			dataLength = dl.intValue();
		}
		colArr = new String[cols.size()];
		cols.toArray(colArr);
		valueArr = new Double[values.size()];
		values.toArray(valueArr);
		for(Double v : valueArr){
			if(v.intValue()>dataLength){
				dataLength = v.intValue();
			}
		}
	}
	
	public abstract void execute(List<Map<String, Object>> dataList);
	/*public int getOutDateNum(){
		return outDateNum;
	}
	public void setOutDateNum(int num){
		this.outDateNum = num;
	}
	public DataMethodType getMethodType() {
		return methodType;
	}
	public void setMethodType(DataMethodType methodType) {
		this.methodType = methodType;
	}*/
	
	@Override
	public DataMethod clone() {
		DataMethod o = null;
		try {
			o = (DataMethod) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
