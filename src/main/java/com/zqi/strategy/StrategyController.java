package com.zqi.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.Static;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.unit.SpringContextHelper;

@Controller
@RequestMapping("/strategy")
public class StrategyController extends BaseController{

	
	@RequestMapping("/strategyList")
	public String strategyList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "strategy/strategyList";
	}
	
	@ResponseBody
	@RequestMapping("/strategyGridList")
	public Map<String, Object> strategyGridList(HttpServletRequest request){
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		List<Map<String, String>> strategies = new ArrayList<Map<String, String>>();
		String basePath = request.getSession().getServletContext().getRealPath("/");
		File parentFile = new File(basePath+"/strategy/loopbacktest");
		String[] files = parentFile.list();
		for(String fileName : files){
			String fileFullPath = basePath+"strategy/"+fileName;
			StrategyFactoy strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
			Strategy strategy = strategyFactoy.getStrategy(fileFullPath);
			strategies.add(strategy.getTitle());
		}
		pagedRequests.setList(strategies);
		//pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@RequestMapping("/strategyResult")
	public String strategyResult(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		model.put("code", code);
		return "strategy/strategyResult";
	}
	
}
