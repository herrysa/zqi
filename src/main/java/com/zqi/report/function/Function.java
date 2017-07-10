package com.zqi.report.function;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.zqi.frame.dao.impl.ZqiDao;

public abstract class Function {

	protected ZqiDao zqiDao;
	protected Map<String, Object> option;
	
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	@SuppressWarnings("unchecked")
	public void init(String option){
		Gson gson = new Gson();
		this.option = gson.fromJson(option, Map.class);
	}
	public abstract String func();
}
