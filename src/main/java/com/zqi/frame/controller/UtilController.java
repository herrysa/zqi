package com.zqi.frame.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.util.Tools;

@Controller
@RequestMapping("/util")
public class UtilController extends BaseController{

	@ResponseBody
	@RequestMapping("/autocomplete")
	public Map<String, Object> autocomplete(HttpServletRequest request){
		try {
			List result = new ArrayList<Map<String,String>>();
			String q = request.getParameter( "name_startsWith" );
			q = URLDecoder.decode( q, "UTF-8" );
			String sql = request.getParameter( "sql" );
			sql = sql.replaceAll("&#039;", "'");
			sql = sql.replaceAll("%q%", "%"+q+"%");
			sql += "limit 0,20";
			List<Map<String, Object>> resultList = zqiDao.findAll(sql);
			for(Map<String, Object> row : resultList){
				String idValue = "",nameValue = "",showValue = "";
				Set<Entry<String, Object>> colSet = row.entrySet();
				for(Entry<String, Object> colEntry : colSet){
					String colname = colEntry.getKey();
					Object value = colEntry.getValue();
					String v = "";
					if(value!=null){
						v = value.toString();
					}
					if(colname.equals("id")){
						idValue = v;
					}else if(colname.equals("name")){
						nameValue = v;
					}
					showValue += v+",";
				}
				if(!"".equals(showValue)){
					showValue = showValue.substring(0, showValue.length()-1);
				}
				Map<String, String> rowMap = new HashMap<String, String>();
				rowMap.put("id", idValue);
				rowMap.put("name", nameValue);
				rowMap.put("showValue", showValue);
				result.add(rowMap);
			}
			resultMap.put("result", result);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMap;
	}
	public static void main(String[] args) {
		String a = Tools.getPYIndexStr("大名称",true);
		System.out.println(a);
	}

}
