package com.zqi.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

public class ChartsUtil {

	public static GsonOption getLineChartOption(Map<String, String> optionMap){
		GsonOption option = new GsonOption();
		String name = optionMap.get("name");
		option.title().text(name).x(X.left);
		option.yAxis(new ValueAxis().scale(true));

		CategoryAxis categoryAxis = new CategoryAxis()
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));

		option.xAxis(categoryAxis);
		
		return option;
	}
	
	public static GsonOption getKCahrtOption(Map<String, String> optionMap){
		GsonOption option = new GsonOption();
		String name = optionMap.get("name");
		String legendStr = optionMap.get("legend");
		
		option.title().text(name+"(日线)").x(X.left).left(0);
		
		Tooltip tooltip = option.tooltip();
		AxisPointer axisPointer = tooltip.axisPointer();
		axisPointer.setType(PointerType.line);
		tooltip.setAxisPointer(axisPointer);
		tooltip.setTrigger(Trigger.axis);
		option.setTooltip(tooltip);
		
		if(legendStr!=null){
			String[] legendArr = legendStr.split(",");
			List<String> legendList = new ArrayList<String>();
			Collections.addAll(legendList, legendArr);
			Legend legend = option.legend();
			legend.setData(legendList);
		}
		
		Grid grid = option.grid();
		grid.left("10%").right("10%").bottom("15%");
		
		option.yAxis(new ValueAxis().scale(true).splitArea(new SplitArea().show(true)));
		
		CategoryAxis categoryAxis = new CategoryAxis()
		.scale(true)
		.boundaryGap(false)
		.min("dataMin")
		.max("dataMax")
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		option.xAxis(categoryAxis);
		
		List<DataZoom> dataZooms = new ArrayList<DataZoom>();
		DataZoom dataZoomInside = new DataZoom();
		dataZoomInside.setType(DataZoomType.inside);
		dataZoomInside.start(50);
		dataZoomInside.end(100);
		dataZooms.add(dataZoomInside);
		DataZoom dataZoomSlider = new DataZoom();
		dataZoomSlider.setType(DataZoomType.slider);
		dataZoomSlider.y("90%");
		dataZoomSlider.start(50);
		dataZoomSlider.end(100);
		dataZooms.add(dataZoomSlider);
		
		option.setDataZoom(dataZooms);
		//categoryAxis.setData(dataList);
		
		
		/*List<Object[]> kDataList = new ArrayList<Object[]>();
		for(Map<String, Object> data : dataList){
			String period = data.get("period").toString();
			String open = data.get("open").toString();
			String close = data.get("close").toString();
			String min = data.get("low").toString();
			String max = data.get("high").toString();
			categoryAxis.data(period);
			Object[] kData = new Object[]{open, close, min, max};
			kDataList.add(kData);
			//k.data(Double.parseDouble(open), Double.parseDouble(close), Double.parseDouble(min), Double.parseDouble(max));
		}*/
		return option;
	}
	
	public static Line getLineSeries(List<Object> data,String name){
		Line line = new Line().smooth(true);
		line.setName(name);
		line.setData(data);
		return line;
	}
	
	public static K getKSeries(List<Object> data,String name){
		K k = new K();
		k.setName(name);
		k.setData(data);
		return k;
	}
}
