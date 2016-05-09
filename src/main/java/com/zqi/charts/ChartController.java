package com.zqi.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.style.LineStyle;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.dao.impl.ZqiDao;

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
		//获取数据

		//getKCahrtOption();
		model.put("option", kChartOption);
		System.out.println(kChartOption);
		return "charts/kChart";
	}
}
