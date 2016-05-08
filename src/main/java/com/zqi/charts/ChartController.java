package com.zqi.charts;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chart")
public class ChartController {

	@RequestMapping("/kChart")
	public String primaryDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		
		return "charts/kChart";
	}
}
