package com.zqi.frame.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.KeyHolder;

import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.BSPager;
import com.zqi.frame.dao.IZqiDao;

public class ZqiDao implements IZqiDao{

	JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ZqiDao(){
		
	}

	@Override
	public void createTableBySQL(String sql) {
		jdbcTemplate.execute(sql);
		
	}

	@Override
	public KeyHolder insertActor(Map entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int operateActor(String sql, Object[] obj) {
		return jdbcTemplate.update(sql, obj);
	}

	@Override
	public int findRowCountBySQL(String sql) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findAll(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> findFirst(String sql) {
		List list = jdbcTemplate.queryForList(sql);
		Map<String, Object> rs0 = null;
		if(list!=null&&list.size()>0){
			rs0 = (Map<String, Object>)list.get(0);
		}else{
			rs0 = new HashMap<String, Object>();
		}
		return rs0;
	}

	@Override
	public List findByFilter(BSPager bsPager, String tableName,List<PropertyFilter> filters) {
		
		return null;
	}
	
	

}
