package com.zqi.PrimaryData.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController{

	@RequestMapping("/primaryDataList")
	public String primaryDataList(){
		
		return "primaryData/primaryDataList";
	}
}
