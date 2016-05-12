package com.zqi.charts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zqi.frame.controller.BaseController;
import com.zqi.strategy.StrategyFactoy;
import com.zqi.unit.SpringContextHelper;

@Controller
@RequestMapping("/chart")
public class ChartController extends BaseController{

	@RequestMapping("/kChart")
	public String kChart(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String dayDataSql = "select * from daytable_all where code='"+code+"' order by period asc limit 0,50";
		List<Map<String, Object>> dataList = zqiDao.findAll(dayDataSql);
		Map<String, String> optionMap = new HashMap<String, String>();
		optionMap.put("name", name);
		String kChartOption  = ChartsUtil.getKCahrtOption(optionMap, dataList);
		
		String zsCode = "" ;
		if(code.startsWith("6")){
			zsCode = "0000001";
		}else if(code.startsWith("0")){
			zsCode = "1399001";
		}else if(code.startsWith("3")){
			zsCode = "1399006";
		}
		dayDataSql = "select * from daytable_all where code='"+zsCode+"' order by period asc limit 0,50";
		List<Map<String, Object>>  zhishudataList = zqiDao.findAll(dayDataSql);
		Map<String, Map<String, Object>> zhishuDataMap = new HashMap<String, Map<String,Object>>();
		for(Map<String, Object> zhishuData : zhishudataList){
			String period = zhishuData.get("period").toString();
			zhishuDataMap.put(period, zhishuData);
		}
		List<Object> zrsiDataList = new ArrayList<Object>();
		List<Object> zrsiCategoryList = new ArrayList<Object>();
		for(Map<String, Object> data : dataList){
			String period = data.get("period").toString();
			String close = data.get("close").toString();
			Map<String, Object> zhishu = zhishuDataMap.get(period);
			String zhishuClose = zhishu.get("close").toString();
			String zrsi = new BigDecimal(close).multiply(new BigDecimal(100)).divide(new BigDecimal(zhishuClose),10,BigDecimal.ROUND_HALF_DOWN).toString();
			zrsiDataList.add(zrsi);
			zrsiCategoryList.add(period);
		}
		Map<String, String> optionZRSIMap = new HashMap<String, String>();
		optionZRSIMap.put("name", "ZRSI");
		String zqsiChartOption  = ChartsUtil.getLineChartOption(optionMap, zrsiCategoryList,zrsiDataList);
		
		model.put("optionK", kChartOption);
		model.put("optionZRSI", zqsiChartOption);
		//System.out.println(kChartOption);
		
		StrategyFactoy strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
		strategyFactoy.init("indicator/ZRSI.js");
		strategyFactoy.parse(code);
		
		
		return "charts/kChart";
	}
}
