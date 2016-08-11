package com.zqi.trade;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.primaryData.fileDataBase.IFileDataBase;
import com.zqi.primaryData.fileDataBase.MyTradeFileDataBase;

@Controller
@RequestMapping("/trade")
public class TradeController extends BaseController{

	
	@RequestMapping("/tradeMain")
	public String tradeMain(HttpServletRequest request,ModelMap model){
		
		return "trade/tradeMain";
	}
	
	@RequestMapping("/positionList")
	public String positionList(HttpServletRequest request,ModelMap model){
		
		return "trade/positionList";
	}
	
	@ResponseBody
	@RequestMapping("/positionGridList")
	public Map<String, Object> strategyGridList(HttpServletRequest request){
		
		Calendar calendar = Calendar.getInstance();
		IFileDataBase myTradeFileDataBase = new MyTradeFileDataBase(""+calendar.get(Calendar.YEAR));
		List<Map<String, Object>> positionList = myTradeFileDataBase.readList("position");
		resultMap.put("rows", positionList);
		return resultMap;
	}
	
}
