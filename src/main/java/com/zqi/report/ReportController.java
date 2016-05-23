package com.zqi.report;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.SQLUtil;
import com.zqi.frame.util.XMLUtil;

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
	public String showReport(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		model.put("code", code);
		HttpSession session = request.getSession();
		String reportPath = session.getServletContext().getRealPath("report");
		File report = new File(reportPath+"/user/"+code+".xml");
		if(report.exists()){
			model.put("reportFile", "user/"+code+".xml");
		}else{
			model.put("reportFile", "blank.xml");
		}
		return "report/reportShow";
	}
	
	@ResponseBody
	@RequestMapping("/saveReportXml")
	public Map<String, Object> saveReportXml(HttpServletRequest request){
		String code = request.getParameter("code");
		String reportXml = request.getParameter("reportXml");
		
		Document document = XMLUtil.stringToXml(reportXml);
		HttpSession session = request.getSession();
		String reportPath = session.getServletContext().getRealPath("report/user");
		try {
			XMLUtil.writDocumentToFile(document, reportPath+"/"+code+".xml", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("message", "1");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/getDataSourceBySql")
	public List<Map<String, Object>> getDataSourceBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests.setPageSize(500);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, sql, filters);
		List<Map<String, Object>> rsList = pagedRequests.getList();
		return rsList;
	}
	
	@ResponseBody
	@RequestMapping("/getDataBySql")
	public Map<String, Object> getDataBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		Map<String, Object> rs = zqiDao.findFirst(sql);
		resultMap.put("rs", rs);
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/getListDataBySql")
	public Map<String, Object> getListDataBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		List<Map<String, Object>> rs = zqiDao.findAll(sql);
		resultMap.put("rs", rs);
		return resultMap;
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
