package com.zqi.dataAnalysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zqi.frame.controller.BaseController;

@Controller
@RequestMapping("/dataAnalysis")
public class DataAnalysis extends BaseController{

	@RequestMapping("/dayLimit")
	public String dayLimit(){
		
		return "dataAnalysis/dayLimit";
	}
	
	@RequestMapping("/dayLimitGridList")
	public String dayLimitGridList(){
		
		return "dataAnalysis/dayLimit";
	}
}
