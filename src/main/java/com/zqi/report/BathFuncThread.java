package com.zqi.report;

import java.util.List;

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
		String rs = "";
		try {
			if(reportFunc.isExecute()){
				JdbcTemplate jtl = (JdbcTemplate)SpringContextHelper.getBean("jdbcTemplate");
				String type = reportFunc.getType();
				if("2".equals(type)){
					List<Object> rsList = jtl.queryForList(reportFunc.getFunc(),Object.class);
					for(Object v : rsList){
						if(v!=null){
							rs += v.toString()+",";
						}
					}
					if(!"".equals(rs)){
						rs = rs.substring(0,rs.length()-1);
					}
				}else{
					if(reportFunc.getPara()==null||reportFunc.getPara().length==0){
						rs = jtl.queryForObject(reportFunc.getFunc(),String.class);
					}else{
						rs = jtl.queryForObject(reportFunc.getFunc(), reportFunc.getPara(),String.class);
					}
				}
				reportFunc.setValue(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
