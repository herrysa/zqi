package com.zqi.strategy;

import org.apache.commons.lang.StringUtils;

public class StrategyHead {

	public String name;
	public int type = 0;	//0:字符串 1:数字2:对象3:变量4:函数
	public String initValue;
	public String value;
	public boolean local = false;
	public boolean inited = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getInitValue() {
		return initValue;
	}
	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setJavaValue(String param,Integer type){
		value = param;
		if(type!=null){
			this.type = type;
		}
		switch (this.type) {
		case 0:
			if(StringUtils.isEmpty(value)){
				initValue =  "null";
			}else{
				initValue = "'"+value+"'";
			}
		case 1:
			initValue =  value;
		case 2:
			initValue = value;
		case 3:
			if(StringUtils.isEmpty(value)){
				initValue =  "''";
			}else{
				initValue = value;
			}
		case 4:
			if(StringUtils.isEmpty(value)){
				initValue =  "''";
			}else{
				initValue =  value;
			}
		default:
			initValue =  "''";
		}
	}
	public void setJsValue(String param){
		if(StringUtils.isEmpty(param)){
			initValue = "''";
			value = "";
			setType(0);
		}else{
			if(param.startsWith("'")){
				initValue = param;
				value = param.substring(1,param.length()-1);
				setType(0);	//字符串
			}else if(param.startsWith("{")||param.startsWith("[")){
				initValue = param;
				value = param;
				setType(2);	//对象
			}else{
				if(StringUtils.isNumeric(param)){
					initValue = param;
					value = param;
					setType(1);	//数字
				}else{
					if("null".equals(param)){
						initValue = param;
						value = null;
						setType(0);	//null
					}else{
						if(param.contains(".")){
							String libName = param.split("\\.")[0];
							if(StrategyJs.lib.contains(libName)){
								initValue = param;
								value = null;
								setType(4);	//函数
							}
						}else{
							initValue = param;
							value = param;
							setType(3);	//变量
						}
					}
				}
				
			}
		}
	}
	public String getJsValue(){
		switch (type) {
		case 0:
			if(StringUtils.isEmpty(value)){
				return "null";
			}else{
				return initValue;
			}
		case 1:
			return value;
		case 2:
			return value;
		case 3:
			if(StringUtils.isEmpty(value)){
				return "''";
			}else{
				return value;
			}
		case 4:
			if(StringUtils.isEmpty(value)){
				return "''";
			}else{
				return value;
			}
		default:
			return "''";
		}
	}
	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		this.local = local;
	}
	public boolean isInited() {
		return inited;
	}
	public void setInited(boolean inited) {
		this.inited = inited;
	}
	public static void main(String[] args) {
		String aString="null";
		System.out.println(StringUtils.isEmpty(aString));
	}
}
