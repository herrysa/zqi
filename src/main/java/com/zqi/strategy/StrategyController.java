package com.zqi.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

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
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.github.abel533.echarts.style.TextStyle;
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
		StrategyQuarz quarz = (StrategyQuarz)SpringContextHelper.getBean("strategyQuarz");
		quarz.init("loopbacktest/"+code+".js");
		Strategy strategy = quarz.run();
		List<Object> categoryData = new ArrayList<Object>();
		List<String> indiLegendList = new ArrayList<String>();
		List<Series> indiSeries = new ArrayList<Series>();
		List<StrategyOut> outList = strategy.getOutList();
		for(StrategyOut strategyOut :outList){
			String outNname = strategyOut.getName();
			String outType = strategyOut.getType();
			List<Object> values = strategyOut.getValues();
			if("xData".equals(outNname)){
				categoryData = values;
			}else if("table".equals(outType)){
				
			}else if("line".equals(outType)){
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
		Map<String, String> title = strategy.getTitle();
		String name = title.get("name");
		if(name==null){
			name = title.get("code");
		}
		GsonOption strategyOption = getKCahrtOption(name,indiLegendList,categoryData);
		strategyOption.series(indiSeries);
		model.put("strategyOption", strategyOption.toString());
		
		return "strategy/strategyResult";
	}
	
	private String listMapToStr(List<Object> values){
		String str = "[";
		for(Object o : values){
			JSONObject jsonObject = JSONObject.fromObject(o);
			str += jsonObject.toString()+",";
		}
		if(!"[".equals(str)){
			str = str.substring(0,str.length()-1);
		}
		str += "]";
		return str;
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
}
