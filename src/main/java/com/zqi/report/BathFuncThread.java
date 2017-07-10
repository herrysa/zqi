package com.zqi.report;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.zqi.report.function.Function;
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
	
	@SuppressWarnings("unchecked")
	public void getFuncValue(ReportFunc reportFunc) {
		String rs = "";
		try {
			String type = reportFunc.getType();
			if("sql".equals(type)){
				if(reportFunc.isExecute()){
					JdbcTemplate jtl = (JdbcTemplate)SpringContextHelper.getBean("jdbcTemplate");
					String rsType = reportFunc.getRsType();
					if("2".equals(rsType)){
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
			}else if("java".equals(type)){
				if(reportFunc.isExecute()){
					String funcMain = reportFunc.getFunc();
					Gson gson = new Gson();
					Map<String, Object> option = gson.fromJson(funcMain, Map.class);
					String className = option.get("class").toString();
					Function function = (Function)SpringContextHelper.getBean(className+"Function");
					function.init(funcMain);
					rs = function.func();
					reportFunc.setValue(rs);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
