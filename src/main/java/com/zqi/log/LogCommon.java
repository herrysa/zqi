package com.zqi.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;

@Controller
@RequestMapping("/log")
public class LogCommon extends BaseController{

	@RequestMapping("/logList")
	public String showLog(HttpServletRequest request,ModelMap model){
		String mainId = request.getParameter("mainId");
		model.addAttribute("mainId", mainId);
		return "log/logList";
	}
	
	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> logGridList(HttpServletRequest request,String type,String mainId){
		if(mainId!=null&&!"".equals(mainId)){
			String logSql = "select * from _log where type='"+type+"' and mainId='"+mainId+"'";
			List<Map<String, Object>> logList = zqiDao.findAll(logSql);
			/*List<Map<String, String>> result = new ArrayList<Map<String,String>>();
			Map<String, String> row = new HashMap<String, String>();
			row.put("customer_id", "1");
			row.put("lastname", "1");
			row.put("firstname", "1");
			row.put("email", "1");
			result.add(row);*/
			resultMap.put("page", "1");
			resultMap.put("records", "1000");
			resultMap.put("rows", logList);
			resultMap.put("total", logList.size());
		}
		return resultMap;
	}
}
