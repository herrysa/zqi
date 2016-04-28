package com.zqi.dataFinder;

import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;

public class FinderRHis163 implements IFinderRHis{

	@Override
	public List<Map<String, Object>> findRHis(List<Map<String, Object>> gpList,String dateFrom, String dateTo) {
		try {
			String url = "http://quotes.money.163.com/service/chddata.html?code=%code%&start=%dateFrom%&end=%dateTo%&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
			for(Map<String, Object> gp : gpList){
				String code = gp.get("code").toString();
				String code163 = "";
				if(code.startsWith("6")){
					code163 = "0"+code;
				}else{
					code163 = "1"+code;
				}
				
				String result = Tools.getByHttpUrl(url);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
}
