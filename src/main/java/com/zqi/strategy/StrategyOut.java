package com.zqi.strategy;

import java.util.ArrayList;
import java.util.List;

public class StrategyOut implements Cloneable{

	private String name;
	private String type;
	private List<Object> values;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Object> getValues() {
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
		this.values.add(value);
	}
	
	public void addValues(List<Object> values) {
		if(this.values==null){
			this.values = new ArrayList<Object>();
		}
		this.values.addAll(values);
	}
	
	@Override
	public StrategyOut clone() {
		StrategyOut o = null;
		try {
			o = (StrategyOut) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
