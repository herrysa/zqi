package com.zqi.report.function;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component("transFunction")
public class TransFunction extends Function{

	@SuppressWarnings("unchecked")
	@Override
	public String func() {
		String rs = "";
		Object transCodeObj = option.get("transCode");
		if(transCodeObj!=null){
			String transCode = transCodeObj.toString();
			String func = option.get("func").toString();
			List<Map<String, Object>> jgdList = zqiDao.findAll("select * from i_jgd where transCode='"+transCode+"' order by dateTime asc");
			if("drawback".equals(func)){
				rs = drawback(jgdList);
			}
		}
		return rs;
	}

	public String drawback(List<Map<String, Object>> jgdList){
		String rs = "";
		double capital = 100000000;
		for(Map<String, Object> jgd : jgdList){
			
		}
		return rs;
	}
}
