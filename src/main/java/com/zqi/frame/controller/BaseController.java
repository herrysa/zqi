package com.zqi.frame.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.SQLUtil;

public class BaseController {

	protected Map<String, Object> resultMap = new HashMap<String, Object>();
	
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
	
	public void setMessage(String message){
		this.resultMap.put("message", message);
	}

	public BaseController() {
		// TODO Auto-generated constructor stub
	}
	
	public void save(HttpServletRequest request,SQLUtil sQLUtil){
		String findSql = sQLUtil.sql_get(request);
		Map<String, Object> entity = zqiDao.findFirst(findSql);
		Map<String, String> entityMap = sQLUtil.getSaveMap(request);
		if(entity.isEmpty()){
			String inseartSql = sQLUtil.sql_inseart(entityMap);
			zqiDao.excute(inseartSql);
		}else{
			String updateSql = sQLUtil.sql_update(entityMap, sQLUtil.getId(request));
			zqiDao.update(updateSql);
		}
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
			gpMap.put(gp.get("symbol").toString(), gp);
		}
		return gpMap;
	}
	
	public List<Map<String, Object>> findAGpDicList(String type){
		String dicSql = "select * from d_gpdic";
		if(type!=null){
			dicSql += " where type in ("+type+")";
		}
		dicSql += " order by code asc";
		List<Map<String, Object>> gpList = zqiDao.findAll(dicSql);
		return gpList;
	}
	
	public Map<String, List<Map<String, Object>>> findAGpDicListMap(){
		String dicSql = "select * from d_gpdic order by code asc";
		List<Map<String, Object>> gpList = zqiDao.findAll(dicSql);
		Map<String, List<Map<String, Object>>> gpDicListMap = new HashMap<String, List<Map<String,Object>>>();
		for(Map<String, Object> gp : gpList){
			String daytable = gp.get("daytable").toString();
			List<Map<String, Object>> gpListDaytable = gpDicListMap.get(daytable);
			if(gpListDaytable==null){
				gpListDaytable = new ArrayList<Map<String,Object>>();
				gpDicListMap.put(daytable, gpListDaytable);
			}
			gpListDaytable.add(gp);
		}
		return gpDicListMap;
	}

}
