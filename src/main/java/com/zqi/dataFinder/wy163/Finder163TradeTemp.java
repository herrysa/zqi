package com.zqi.dataFinder.wy163;

import java.util.Map;

import com.zqi.dataFinder.IFinderTrade;
import com.zqi.frame.util.Tools;
import com.zqi.primaryData.fileDataBase.FileDataBase;

public class Finder163TradeTemp implements IFinderTrade{

	private Map<String, Object> gp ;
	private String year ;
	private String date ;
	
	public Finder163TradeTemp(Map<String, Object> gp , String year , String date){
		this.gp = gp;
		this.year = year;
		this.date = date;
	}
	
	@Override
	public void findTrade() {
		String code = gp.get("code").toString();
		String type = gp.get("type").toString();
		String url = "http://quotes.money.163.com/cjmx/"+year+"/"+date+"/"+type+code+".xls";
		String datePath = "tradeData/temp/"+year+"/"+date;
		FileDataBase fileDataBase = new FileDataBase(datePath);
		//fileDataBase.deleteDataBase();
		String result = Tools.getExcelByHttpUrl(url);
		fileDataBase.writeStr(code, result, 0);  
		System.out.println(code);
	}

}
