package com.zqi.dataFinder.xq;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.zqi.dataFinder.IFinderFh;
import com.zqi.frame.util.Tools;
import com.zqi.unit.DateUtil;

public class FinderXqFh implements IFinderFh{

	String[] fhKey = {"symbol","bonusyear","bonusimpdate","recorddate","exrightdate","bonusskratio","tranaddskraio","taxcdividend","bonussklistdate","tranaddsklistdate","bonusskaccday","tranaddskaccday","summarize"};
	String[] fhType = {"string","string","date","date","date","decimal","decimal","decimal","date","date","date","date","string"};
	String url = "https://xueqiu.com/stock/f10/bonus.json?symbol=%code%&page=1&size=50";
	
	public List<Map<String, Object>> findFhInfo(Map<String, Object> gp){
		String code = gp.get("code").toString();
		String type = gp.get("type").toString();
		String urlTemp = "";
		if("0".equals(type)){
			urlTemp = url.replace("%code%", "SH"+code);
		}else if("1".equals(type)){
			urlTemp = url.replace("%code%", "SZ"+code);
		}
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = Tools.getByHttpUrl(urlTemp);
		if(result==null&&!"".equals(result)){
			JSONObject rsObj = JSONObject.fromObject(result);
			JSONArray list = (JSONArray)rsObj.get("list");
			Iterator<JSONObject> itemIt = list.iterator();
			while (itemIt.hasNext()) {
				JSONObject item = itemIt.next();
				int i=0;
				Map<String, Object> data = new HashMap<String, Object>();
				for(String col : fhColumn){
					String dateKey = fhKey[i];
					String dateType = fhType[i];
					Object value = item.get(dateKey);
					String v = "";
					if(value!=null){
						v = value.toString();
						if("date".equals(dateType)){
							Date date = null;
							try {
								date = DateUtil.convertStringToDate(v);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							v = DateUtil.convertDateToString(date);
							data.put(col, v);
						}else if("decimal".equals(dateType)){
							data.put(col, new BigDecimal(v));
						}else{
							data.put(col, v);
						}
					}
				}
				dataList.add(data);
			}
		}
		return dataList;
	}
	
	
}
