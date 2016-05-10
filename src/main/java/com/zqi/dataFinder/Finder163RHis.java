package com.zqi.dataFinder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;

public class Finder163RHis implements IFinderRHis{
	public static String[] rHisColumn163 = {"0","1","2","7","6","4","5","3","11","12","8","9","wu","10","wu","wu","wu","13","14","wu","wu","wu","wu"};
	@Override
	public List<Map<String, Object>> findRHis(Map<String, Object> gp,String dateFrom, String dateTo) {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String url = "http://quotes.money.163.com/service/chddata.html?code=%code%&start=%dateFrom%&end=%dateTo%&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
		String fromYear = dateFrom.substring(0,4);
		String toYear = dateTo.substring(0,4);
		dateFrom = dateFrom.replace("-", "");
		dateTo = dateTo.replace("-", "");
		String code = gp.get("code").toString();
		String type = gp.get("type").toString();
		String code163 = "";
		if("0".equals(type)){
			code163 = "0"+code;
		}else if("1".equals(type)){
			code163 = "1"+code;
		}else{
			code163 = code;
		}
		String urlTemp = url;
		urlTemp = urlTemp.replace("%code%", code163);
		urlTemp = urlTemp.replace("%dateFrom%", dateFrom);
		urlTemp = urlTemp.replace("%dateTo%", dateTo);
		String result = Tools.getByHttpUrl(urlTemp);
		if(fromYear.equals(toYear)){
			result = result.replace(fromYear+"-", "\n"+fromYear+"-");
		}else{
			result = result.replace(fromYear+"-", "\n"+fromYear+"-");
			result = result.replace(toYear+"-", "\n"+toYear+"-");
		}
		String[] rowArr = result.split("\n");
		for(int r=1;r<rowArr.length;r++){
			String row = rowArr[r];
			//System.out.println(row);
			String[] col = row.split(",");
			Map<String, Object> data = new HashMap<String, Object>();
			for(int c=0;c<rDayColumn.length;c++){
				String key = rDayColumn[c];
				String datakey = rHisColumn163[c];
				if("wu".equals(datakey)){
					continue;
				}
				String datatype = rDayColumnType[c];
				int dataIndex = Integer.parseInt(datakey);
				if(dataIndex>col.length-1){
					continue;
				}
				String v = col[dataIndex];
				if(v==null||"".equals(v)){
					continue;
				}
				if("code".equals(key)){
					v = v.substring(1);
					if("2".equals(type)){
						v = "0"+v;
					}else if("3".equals(type)){
						v = "1"+v;
					}
				}
				if("name".equals(key)){
					v = v.replaceAll(" ", "");
				}
				if("decimal".equals(datatype)){
					if("None".equals(v)){
						v = "0";
					}
					//System.out.println(v);
					data.put(key, new BigDecimal(v));
				}else{
					data.put(key, v);
				}
			}
			data.put("type", type);
			dataList.add(data);
		}
		return dataList;
	}
	public static void main(String[] args) {
		String urlTemp = "http://quotes.money.163.com/service/chddata.html?code=1300141&start=20160112&end=20160428&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
		String result = Tools.getByHttpUrl(urlTemp);
		System.out.println();
	}
}
