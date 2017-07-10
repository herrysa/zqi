package com.zqi.strategy;

public class StrategyTitle {

	private String code;
	private String name;
	private String group;
	private String type;
	private String param;
	private String desc;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public StrategyTitle setName(String name) {
		this.name = name;
		return this;
	}
	public String getGroup() {
		return group;
	}
	public StrategyTitle setGroup(String group) {
		this.group = group;
		return this;
	}
	
	public String getType() {
		return type;
	}
	public StrategyTitle setType(String type) {
		this.type = type;
		return this;
	}
	public String getParam() {
		return param;
	}
	public StrategyTitle setParam(String param) {
		this.param = param;
		return this;
	}
	
	public String getDesc() {
		return desc;
	}
	public StrategyTitle setDesc(String desc) {
		this.desc = desc;
		return this;
	}
	
	
}
