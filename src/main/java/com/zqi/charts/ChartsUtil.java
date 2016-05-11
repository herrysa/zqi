package com.zqi.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.K;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.style.LineStyle;

public class ChartsUtil {

	public static String getLineChartOption(Map<String, String> optionMap,List<Object> categoryList,List<Object> dataList){
		GsonOption option = new GsonOption();
		String name = optionMap.get("name");
		option.title().text(name).x(X.left);
		option.yAxis(new ValueAxis().scale(true));

		CategoryAxis categoryAxis = new CategoryAxis()
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		//创建Line数据
		Line line = new Line().smooth(true);

		categoryAxis.setData(categoryList);
		line.setData(dataList);
		option.xAxis(categoryAxis);
		option.series(line);
		
		return option.toString();
	}
	
	public static String getKCahrtOption(Map<String, String> optionMap,List<Map<String, Object>> dataList){
		GsonOption option = new GsonOption();
		String name = optionMap.get("name");
		option.title().text(name+"(日线)").x(X.left);
		//option.legend().data("访问量").x(X.center).y(Y.bottom).borderWidth(1); //均线需要图例
		option.yAxis(new ValueAxis().scale(true));
		CategoryAxis categoryAxis = new CategoryAxis()
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		//categoryAxis.setData(dataList);
		K k = new K();
		
		List<Object[]> kDataList = new ArrayList<Object[]>();
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
		}
		k.setData(kDataList);
		option.xAxis(categoryAxis);
		option.series(k);
		
		return option.toString();
	}
}
