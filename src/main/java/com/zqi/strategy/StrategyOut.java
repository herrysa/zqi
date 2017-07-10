package com.zqi.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyOut implements Cloneable{

	public enum OUTTYPE{
		x,table,line,bar,json,txt;
	}
	
	private String name;
	private OUTTYPE type;
	private boolean accu = false;

	private Map<String, Object> paramMap;

	private List<Object> values;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OUTTYPE getType() {
		return type;
	}
	public void setType(OUTTYPE type) {
		this.type = type;
	}
	public List<Object> getValues() {
		if(values==null){
			values = new ArrayList<Object>();
		}
		return values;
	}
	public void setValues(List<Object> values) {
		this.values = values;
	}
	
	public void clearValues(){
		if(this.values!=null){
			this.values.clear();
		}
	}
	
	public void addValue(Object value) {
		if(this.values==null){
			this.values = new ArrayList<Object>();
		}
		if(value!=null){
			this.values.add(value);
		}
	}
	
	public void addValueOnlyOne(Object value) {
		if(this.values==null){
			this.values = new ArrayList<Object>();
		}else{
			this.values.clear();
		}
		if(value!=null){
			this.values.add(value);
		}
	}
	
	public void addValues(List<Object> values) {
		if(this.values==null){
			this.values = new ArrayList<Object>();
		}
		if(values!=null){
			this.values.addAll(values);
		}
	}
	
	public Map<String, Object> getParamMap() {
		if(paramMap==null){
			paramMap = new HashMap<String, Object>();
		}
		return paramMap;
	}
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	
	public void addParam(String key,Object param){
		if(paramMap==null){
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put(key, param);
	}
	public boolean isAccu() {
		return accu;
	}
	public void setAccu(boolean accu) {
		this.accu = accu;
	}
	
	@Override
	public StrategyOut clone() {
		StrategyOut o = null;
		try {
			o = (StrategyOut) super.clone();
			List<Object> values = o.getValues();
			if(values!=null){
				List<Object> valuesClone = new ArrayList<Object>();
				valuesClone.addAll(values);
				o.setValues(valuesClone);
			}
			
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
