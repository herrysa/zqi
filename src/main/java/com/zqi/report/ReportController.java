package com.zqi.report;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
		return "report/reportShow";
	}
	
	@ResponseBody
	@RequestMapping("/getReportXml")
	public String getReportXml(HttpServletRequest request,HttpServletResponse response){
		String code = request.getParameter("code");
		HttpSession session = request.getSession();
		String reportPath = session.getServletContext().getRealPath("report");
		File report = new File(reportPath+"/user/"+code+".xml");
		String reportXml = null;
		if(!report.exists()){
			report = new File(reportPath+"/blank.xml");
		}
		reportXml = XMLUtil.xmltoString(XMLUtil.read(report, "UTF-8"));
		try {
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/xml;charset=utf-8");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out;
			out = response.getWriter();
			out.write(reportXml.toString());  
			out.flush();  
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
        return null;
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
		sql = replaceVari(request,sql);
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
	
	@ResponseBody
	@RequestMapping("/findPeriodList")
	public Map<String, Object> findPeriodList(HttpServletRequest request){
		List<Map<String, Object>> periodList = null;
		periodList = getPeriodList(request);
		resultMap.put("periodList", periodList);
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
	
	public String replaceVari(HttpServletRequest request,String str){
		List<Map<String, Object>> periodList = null;
		periodList = getPeriodList(request);
		str = str.replaceAll("%lastperiod%", periodList.get(0).get("period").toString());
		
		return str;
	}
	
	public List<Map<String, Object>> getPeriodList(HttpServletRequest request){
		HttpSession session = request.getSession();
		List<Map<String, Object>> periodList = null;
		periodList = (List<Map<String, Object>>)session.getAttribute("periodList");
		if(periodList==null){
			String sql = "select period from daytable_all where code='0000001' order by period desc";
			periodList = zqiDao.findAll(sql);
		}
		return periodList;
	}
}
