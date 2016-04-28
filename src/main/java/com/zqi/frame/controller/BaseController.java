package com.zqi.frame.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.dao.impl.ZqiDao;

public class BaseController {

	protected Map<String, Object> resultMap = new HashMap<String, Object>();
	protected String message = "";
	
	protected ZqiDao zqiDao; 
	protected PagerFactory pagerFactory;
	public PagerFactory getPagerFactory() {
		return pagerFactory;
	}

	@Autowired
	public void setPagerFactory(PagerFactory pagerFactory) {
		this.pagerFactory = pagerFactory;
	}

	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

	public BaseController() {
		// TODO Auto-generated constructor stub
	}

}
