package com.zqi.strategy;

import java.util.HashMap;
import java.util.Map;

public class QuantContext {

private Map<String, Object> context;
	
	
	public void removeContext(String key){
		if(context!=null){
			context.remove(key);
		}
	}
	public Object getContext(String key) {
		Object v = context==null?null:context.get(key);
		return v;
	}
	public void setContext(String key , Object value) {
		if(context==null){
			context = new HashMap<String, Object>();
		}
		context.put(key,value);
	}
	
	public void addNumToContext(String key , Integer value) {
		if(context==null){
			context = new HashMap<String, Object>();
		}
		Integer sum = (Integer)this.getContext(key);
		if(sum==null){
			this.setContext(key, value);
		}else{
			sum += value;
			this.setContext(key, sum);
		}
	}
}
