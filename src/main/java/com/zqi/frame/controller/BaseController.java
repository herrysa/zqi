package com.zqi.frame.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zqi.frame.controller.pagers.JQueryPager;
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
	
	protected void makeResultMap(JQueryPager pagedRequests){
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
	}
	
	public Map<String, Map<String, Object>> findAGpDicMap(){
		Map<String, Map<String, Object>> gpMap = new HashMap<String, Map<String,Object>>();
		String dicSql = "select * from d_gpdic order by code asc";
		List<Map<String, Object>> gpList = zqiDao.findAll(dicSql);
		for(Map<String, Object> gp :gpList){
			gpMap.put(gp.get("code").toString(), gp);
		}
		return gpMap;
	}
	
	public List<Map<String, Object>> findAGpDicList(){
		String dicSql = "select * from d_gpdic order by code asc";
		List<Map<String, Object>> gpList = zqiDao.findAll(dicSql);
		return gpList;
	}

}
