package com.zqi.frame.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.KeyHolder;

import com.zqi.frame.dao.IZqiDao;

public class ZqiDao extends JdbcDaoSupport implements IZqiDao{

	JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
	public ZqiDao() {
		// TODO Auto-generated constructor stub
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

}
