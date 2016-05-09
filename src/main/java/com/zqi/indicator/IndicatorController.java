package com.zqi.indicator;

import org.springframework.web.bind.annotation.RequestMapping;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.util.SQLUtil;

public class IndicatorController extends BaseController{

	private SQLUtil sQLUtil = new SQLUtil(new String[]{"code","formula"}, "i_indicator", "code", "");

	@RequestMapping("/indicatorList")
	public String indicatorList(){
		
		return "indicator/indicatorList";
	}
}
