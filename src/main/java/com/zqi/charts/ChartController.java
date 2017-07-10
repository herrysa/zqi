package com.zqi.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.abel533.echarts.AxisPointer;
import com.github.abel533.echarts.DataZoom;
import com.github.abel533.echarts.Grid;
import com.github.abel533.echarts.Legend;
import com.github.abel533.echarts.Tooltip;
import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitArea;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.DataZoomType;
import com.github.abel533.echarts.code.PointerType;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.K;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.github.abel533.echarts.style.TextStyle;
import com.zqi.frame.controller.BaseController;
import com.zqi.strategy.StrategyJs;
import com.zqi.strategy.StrategyJsFactoy;
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
		name = name+"(日线)";
		String dayDataSql = "select * from daytable_all where code='"+code+"' and close<>0 order by period";
		
		List<String> legendList = new ArrayList<String>();
		List<Series> series = new ArrayList<Series>();
		
		//数据处理
		List<Map<String, Object>> dataList = zqiDao.findAll(dayDataSql);
		List<Object[]> kDataList = new ArrayList<Object[]>();
		List<Object> categoryData = new ArrayList<Object>();
		for(Map<String, Object> data : dataList){
			String period = data.get("period").toString();
			categoryData.add(period);
			Object[] kData = new Object[]{data.get("open"), data.get("close"), data.get("low"), data.get("high")};
			kDataList.add(kData);
		}
		legendList.add("日k");
		K k = new K();
		k.setName("日k");
		k.setData(kDataList);
		series.add(k);
		
		model.put("code", code);
		HttpSession session = request.getSession();
		session.setAttribute("categoryData", categoryData);
		
		//均线
		String avgCol = "close";
		String avgLine = "5,10,20,60";
		String[] avgLineArr = avgLine.split(",");
		Data.avg(dataList, "{close:["+avgLine+"]}");
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
		
		GsonOption kOption = getKCahrtOption(name,legendList,categoryData);
		kOption.series(series);
		//System.out.println(kOption.toString());
		model.put("kOption", kOption.toString());
		//指标
		/*String indicator = "ZRSI";
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
					line.setxAxisIndex(1);
					line.setyAxisIndex(1);
				}else if("bar".equals(outType)){
					
				}
			}
		}*/
		
		
		/*String indicator = "ZRSI";
		String[] indicatorArr = indicator.split(",");
		List<String> indiLegendList = new ArrayList<String>();
		List<Series> indiSeries = new ArrayList<Series>();
		for(String indi : indicatorArr){
			StrategyFactoy strategyFactoy = (StrategyFactoy)SpringContextHelper.getBean("strategyFactoy");
			strategyFactoy.init("indicator/ZRSI.js");
			strategyFactoy.setCode(code);
			strategyFactoy.setxData(categoryData);
			strategyFactoy.setStart(null);
			strategyFactoy.setEnd(null);
			strategyFactoy.eval();
			List<StrategyOut> outList = strategyFactoy.getOutList();
			for(StrategyOut strategyOut :outList){
				String outNname = strategyOut.getName();
				String outType = strategyOut.getType();
				List<Object> values = strategyOut.getValues();
				if("line".equals(outType)){
					String lineName = outNname;
					indiLegendList.add(lineName);
					Line line = new Line();
					line.setSmooth(true);
					line.setName(lineName);
					line.setData(values);
					indiSeries.add(line);
				}else if("bar".equals(outType)){
					
				}
			}
		}
		GsonOption indiOption = getKCahrtOption(indicator,indiLegendList,categoryData);
		indiOption.series(indiSeries);
		System.out.println(indiOption.toString());
		model.put("indiOption", indiOption.toString());
		*/
		/*String zsCode = "" ;
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
		
		model.put("optionZRSI", zqsiChartOption);*/
		//System.out.println(kChartOption);
		
		
		return "charts/kChart";
	}
	
	@ResponseBody
	@RequestMapping("/indicator")
	public Map<String, Object> indicator(HttpServletRequest request){
		String code = request.getParameter("code");
		String indicator = request.getParameter("indi");
		List<Object> categoryData = new ArrayList<Object>();
		HttpSession session = request.getSession();
		categoryData = (List<Object>)session.getAttribute("categoryData");
		String[] indicatorArr = indicator.split(",");
		List<String> indiLegendList = new ArrayList<String>();
		List<Series> indiSeries = new ArrayList<Series>();
		for(String indi : indicatorArr){
			StrategyJsFactoy strategyFactoy = (StrategyJsFactoy)SpringContextHelper.getBean("strategyFactoy");
			StrategyJs strategy = strategyFactoy.getStrategy("indicatorChart/"+indicator+".js");
			strategy.setJavaParam("code",code);
			strategy.setxData(categoryData);
			strategy.setJavaParam("start",null);
			strategy.setJavaParam("end",null);
			strategy.eval();
			List<StrategyOut> outList = strategy.getOutList();
			for(StrategyOut strategyOut :outList){
				String outNname = strategyOut.getName();
				StrategyOut.OUTTYPE outType = strategyOut.getType();
				List<Object> values = strategyOut.getValues();
				if(outType==StrategyOut.OUTTYPE.line){
					String lineName = outNname;
 					indiLegendList.add(lineName);
					Line line = new Line();
					line.setSmooth(true);
					line.setName(lineName);
					line.setData(values);
					indiSeries.add(line);
				}else if(outType==StrategyOut.OUTTYPE.bar){
					
				}
			}
		}
		GsonOption indiOption = getKCahrtOption(indicator,indiLegendList,categoryData);
		indiOption.series(indiSeries);
		resultMap.put("indiOption", indiOption.toString());
		return resultMap;
	}
	
	private GsonOption getKCahrtOption(String name,List<String> legendList,List<Object> categoryList){
		GsonOption option = new GsonOption();
		
		option.title().text(name).x(X.left).left(0).setTextStyle(new TextStyle().fontSize(14));;
		
		Tooltip tooltip = option.tooltip();
		AxisPointer axisPointer = tooltip.axisPointer();
		axisPointer.setType(PointerType.line);
		tooltip.setAxisPointer(axisPointer);
		tooltip.setTrigger(Trigger.axis);
		option.setTooltip(tooltip);
		
		Legend legend = option.legend();
		legend.setData(legendList);
		
		Grid grid = option.grid();
		grid.left("30").right("30").bottom("65");
		
		option.yAxis(new ValueAxis().scale(true).splitArea(new SplitArea().show(true)));
		
		CategoryAxis categoryAxis = new CategoryAxis()
		.scale(true)
		.boundaryGap(false)
		.min("dataMin")
		.max("dataMax")
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		categoryAxis.setData(categoryList);
		
		option.xAxis(categoryAxis);
		
		List<DataZoom> dataZooms = new ArrayList<DataZoom>();
		DataZoom dataZoomInside = new DataZoom();
		dataZoomInside.setType(DataZoomType.inside);
		dataZoomInside.start(50);
		dataZoomInside.end(100);
		dataZooms.add(dataZoomInside);
		DataZoom dataZoomSlider = new DataZoom();
		dataZoomSlider.show(true);
		dataZoomSlider.setType(DataZoomType.slider);
		dataZoomSlider.y("90%");
		dataZoomSlider.start(50);
		dataZoomSlider.end(100);
		dataZooms.add(dataZoomSlider);
		
		option.setDataZoom(dataZooms);
		return option;
	}
	
	private GsonOption getIndiCahrtOption(String name,List<String> legendList,List<Object> categoryList){
		GsonOption option = new GsonOption();
		
		option.title().text(name).x(X.left).left(0);
		
		Tooltip tooltip = option.tooltip();
		AxisPointer axisPointer = tooltip.axisPointer();
		axisPointer.setType(PointerType.line);
		tooltip.setAxisPointer(axisPointer);
		tooltip.setTrigger(Trigger.axis);
		option.setTooltip(tooltip);
		
		Legend legend = option.legend();
		legend.setData(legendList);
		
		Grid grid = option.grid();
		grid.left("5%").right("5%").bottom("17%");
		
		option.yAxis(new ValueAxis().scale(true).splitArea(new SplitArea().show(true)));
		
		CategoryAxis categoryAxis = new CategoryAxis()
		.scale(true)
		.boundaryGap(false)
		.min("dataMin")
		.max("dataMax")
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		categoryAxis.setData(categoryList);
		
		option.xAxis(categoryAxis);
		
		List<DataZoom> dataZooms = new ArrayList<DataZoom>();
		DataZoom dataZoomInside = new DataZoom();
		dataZoomInside.setType(DataZoomType.inside);
		dataZoomInside.start(50);
		dataZoomInside.end(100);
		dataZooms.add(dataZoomInside);
		DataZoom dataZoomSlider = new DataZoom();
		dataZoomSlider.show(true);
		dataZoomSlider.setType(DataZoomType.slider);
		dataZoomSlider.y("90%");
		dataZoomSlider.start(50);
		dataZoomSlider.end(100);
		dataZooms.add(dataZoomSlider);
		
		option.setDataZoom(dataZooms);
		return option;
	}
}
