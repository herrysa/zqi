package com.zqi.PrimaryData.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController{

	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> primaryDataGridList(){
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		Map<String, String> row = new HashMap<String, String>();
		row.put("customer_id", "1");
		row.put("lastname", "1");
		row.put("firstname", "1");
		row.put("email", "1");
		result.add(row);
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("page_data", result);
		r.put("total_rows", "10");
		return r;
	}
	
	@RequestMapping("/primaryDataList")
	public String primaryDataList(){
		
		return "primaryData/primaryDataList";
	}
}
