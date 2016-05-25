package com.zqi.report;

import org.springframework.jdbc.core.JdbcTemplate;

import com.zqi.report.model.ReportFunc;
import com.zqi.unit.SpringContextHelper;

public class BathFuncThread implements Runnable{

	ReportFunc func;
	
	public BathFuncThread(ReportFunc func){
		this.func = func;
	}
	
	@Override
	public void run() {
		getFuncValue(func);
		
	}
	
	public void getFuncValue(ReportFunc reportFunc) {
		String rs;
		try {
			if(reportFunc.isExecute()){
				JdbcTemplate jtl = (JdbcTemplate)SpringContextHelper.getBean("jdbcTemplate");
				rs = jtl.queryForObject(reportFunc.getFunc(), reportFunc.getPara(),String.class);
				reportFunc.setValue(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
