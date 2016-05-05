package com.zqi.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.SQLUtil;

@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{

	private SQLUtil sQLUtil = new SQLUtil(new String[]{"code","name","type","remark"}, "r_report", "code", "");
	
	@RequestMapping("/reportMain")
	public String reportMain(){
		
		return "report/reportMain";
	}
	
	@RequestMapping("/reportList")
	public String reportList(){
		
		return "report/reportList";
	}
	
	@RequestMapping("/reportForm")
	public String reportForm(HttpServletRequest request,ModelMap model){
		String id = request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			Map<String, Object> report = zqiDao.findFirst("select * from report where code='"+id+"'");
			model.put("report", report);
		}
		return "report/reportForm";
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public String save(HttpServletRequest request){
		String id = request.getParameter("id");
		Map<String, String> entityMap = getSaveMap(request,sQLUtil.columns);
		if(id!=null&&!"".equals(id)){
			String updateSql = sQLUtil.sql_update(entityMap, id);
			zqiDao.update(updateSql);
		}else{
			String inseartSql = sQLUtil.sql_inseart(entityMap);
			zqiDao.excute(inseartSql);
		}
		return "保存成功!";
	}
	
	@ResponseBody
	@RequestMapping("/reportGridList")
	public Map<String, Object> reportGridList(HttpServletRequest request){
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, "select * from r_report", filters);
		List<Map<String, Object>> reportData = pagedRequests.getList();
		makeResultMap(pagedRequests);
		return resultMap;
	}
	
	@RequestMapping("/show")
	public String showReport(){
		
		return "report/reportShow";
	}
	
	public Map<String, String> getSaveMap(HttpServletRequest request,String[] columns){
		Map<String, String> saveMap = new HashMap<String, String>();
		for(String column : columns){
			String value = request.getParameter(column);
			saveMap.put(column, value);
		}
		return saveMap;
	}
}
