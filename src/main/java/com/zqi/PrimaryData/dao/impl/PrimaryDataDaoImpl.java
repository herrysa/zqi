package com.zqi.PrimaryData.dao.impl;

import org.springframework.stereotype.Repository;

import com.zqi.PrimaryData.dao.IPrimaryDataDao;
import com.zqi.frame.dao.impl.ZqiDao;

@Repository("primaryDataDaoImpl")
public class PrimaryDataDaoImpl extends ZqiDao implements IPrimaryDataDao{

	public PrimaryDataDaoImpl(){
		super();
	}
	
}
