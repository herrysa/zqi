package com.zqi.report;

import java.util.List;
import java.util.Map;

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
@RequestMapping("/reportFunc")
public class ReportFuncController extends BaseController{
	
	private SQLUtil sQLUtil = new SQLUtil(new String[]{"code","name","category","type","params","funcSql","remark"}, "r_reportFunc", "code", "");
	
	@RequestMapping("/main")
	public String reportFuncMain(){
		
		return "report/reportFuncMain";
	}
	
	@RequestMapping("/funcList")
	public String reportFuncList(){
		
		return "report/reportFuncList";
	}
	
	@RequestMapping("/funcForm")
	public String reportFuncForm(HttpServletRequest request,ModelMap model){
		String id = request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			Map<String, Object> reportFunc = zqiDao.findFirst("select * from r_reportFunc where code='"+id+"'");
			model.put("reportFunc", reportFunc);
		}
		return "report/reportFuncForm";
	}
	
	@RequestMapping("/reportForm")
	public String reportForm(HttpServletRequest request,ModelMap model){
		String id = request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			Map<String, Object> report = zqiDao.findFirst("select * from r_report where code='"+id+"'");
			model.put("report", report);
		}
		return "report/reportForm";
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public Map<String, Object> save(HttpServletRequest request){
		super.save(request,sQLUtil);
		resultMap.put("message", "保存成功!");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public Map<String, Object> delete(HttpServletRequest request){
		String id = request.getParameter("code");
		if(id!=null&&!"".equals(id)){
			String updateSql = sQLUtil.sql_delete( id);
			zqiDao.update(updateSql);
		}
		resultMap.put("message", "删除成功!");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/reportFuncGridList")
	public Map<String, Object> reportGridList(HttpServletRequest request){
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, "select * from r_reportFunc", filters);
		List<Map<String, Object>> reportData = pagedRequests.getList();
		makeResultMap(pagedRequests);
		return resultMap;
	}

}
