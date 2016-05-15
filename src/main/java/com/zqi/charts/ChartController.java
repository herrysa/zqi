package com.zqi.charts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.util.TestTimer;
import com.zqi.strategy.StrategyFactoy;
import com.zqi.strategy.StrategyOut;
import com.zqi.strategy.lib.Data;
import com.zqi.unit.SpringContextHelper;

@Controller
@RequestMapping("/chart")
public class ChartController extends BaseController{

	@RequestMapping("/kChart")
	public String kChart(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String dayDataSql = "select * from daytable_all where code='"+code+"' and close<>0 order by period";
		
		List<String> legendList = new ArrayList<String>();
		
		//数据处理
		List<Map<String, Object>> dataList = zqiDao.findAll(dayDataSql);
		List<Object[]> kDataList = new ArrayList<Object[]>();
		List<Object> categoryData = new ArrayList<Object>();
		for(Map<String, Object> data : dataList){
			String period = data.get("period").toString();
			String open = data.get("open").toString();
			String close = data.get("close").toString();
			String min = data.get("low").toString();
			String max = data.get("high").toString();
			categoryData.add(period);
			Object[] kData = new Object[]{open, close, min, max};
			kDataList.add(kData);
		}
		legendList.add("日k");
		
		//均线
		String avgCol = "close";
		String avgLine = "5,10,20,60";
		String[] avgLineArr = avgLine.split(",");
		Data.avg(dataList, "{close:["+avgLine+"]}");
		List<Series> series = new ArrayList<Series>();
		for(String l : avgLineArr){
			String lineName = "MA"+l;
			Line line = new Line();
			line.setSmooth(true);
			//line.s
			List linData = new ArrayList();
			line.setName(lineName);
			for(Map<String, Object> data : dataList){
				Object avgData = data.get(avgCol+l);
				linData.add(avgData);
			}
			line.setData(linData);
			series.add(line);
			legendList.add(lineName);
		}
		
		//指标
		String indicator = "zrsi";
		String[] indicatorArr = indicator.split(",");
		for(String indi : indicatorArr){
			legendList.add(indi);
			StrategyFactoy strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
			strategyFactoy.init("indicator/ZRSI.js");
			strategyFactoy.eval();
			List<StrategyOut> outList = strategyFactoy.getOutList();
			for(StrategyOut strategyOut :outList){
				String outNname = strategyOut.getName();
				String outType = strategyOut.getType();
				List<Object> values = strategyOut.getValues();
				if("line".equals(outType)){
					String lineName = outNname;
					Line line = new Line();
					line.setSmooth(true);
					line.setName(lineName);
					line.setData(values);
				}else if("bar".equals(outType)){
					
				}
			}
		}
		
		
		Map<String, String> optionMap = new HashMap<String, String>();
		optionMap.put("name", name);
		String kChartOption  = ChartsUtil.getKCahrtOption(optionMap, dataList,series);
		
		String zsCode = "" ;
		if(code.startsWith("6")){
			zsCode = "0000001";
		}else if(code.startsWith("0")){
			zsCode = "1399001";
		}else if(code.startsWith("3")){
			zsCode = "1399006";
		}
		dayDataSql = "select * from daytable_all where code='"+zsCode+"' order by period asc";
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
			System.out.println(period);
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
		
		TestTimer tt = new TestTimer("ss");
		tt.begin();
		StrategyFactoy strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
		strategyFactoy.init("indicator/ZRSI.js");
		strategyFactoy.parse(code);
		String str = strategyFactoy.getStrategyScript();
		ScriptEngineManager manager = new ScriptEngineManager();  
		ScriptEngine engine = manager.getEngineByName("js");
		Bindings bindings  = engine.createBindings();
		//bindings.put("aa", "{show:2}");
		try {
			System.out.println(str);
			engine.eval(str,bindings);
			System.out.println(bindings.get("result").toString());
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tt.done();
		return "charts/kChart";
	}
}
