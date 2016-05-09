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

	public static String getLineOption(Map<String, String> optionMap){
		//创建Option对象
		GsonOption option = new GsonOption();

		//设置图表标题，并且居中显示
		option.title().text("最近7天访问量图表").x(X.center);

		//设置图例,居中底部显示，显示边框
		option.legend().data("访问量").x(X.center).y(Y.bottom).borderWidth(1);

		//设置y轴为值轴，并且不显示y轴，最大值设置400，最小值-100（OSC为什么要有-100呢？）
		option.yAxis(new ValueAxis().name("IP")
		        .axisLine(new AxisLine().show(true).lineStyle(new LineStyle().width(0)))
		        .max(400).min(-100));

		//创建类目轴，并且不显示竖着的分割线，onZero=false
		CategoryAxis categoryAxis = new CategoryAxis()
		        .splitLine(new SplitLine().show(false))
		        .axisLine(new AxisLine().onZero(false));
		
		//创建Line数据
		Line line = new Line("访问量").smooth(true);

		//根据获取的数据赋值
		//for (AccessData data : datas) {
		    //增加类目，值为日期
		    categoryAxis.data("1");

		    //日期对应的数据
		    line.data("1");
		    categoryAxis.data("2");

		    //日期对应的数据
		    line.data("2");
		    categoryAxis.data("3");

		    //日期对应的数据
		    line.data("3");
		//}

		//设置x轴为类目轴
		option.xAxis(categoryAxis);

		//设置数据
		option.series(line);
		
		return "";
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
