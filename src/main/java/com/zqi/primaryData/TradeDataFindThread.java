package com.zqi.primaryData;

import java.util.Map;

import com.zqi.dataFinder.IFinderTrade;
import com.zqi.dataFinder.wy163.Finder163Trade;

public class TradeDataFindThread implements Runnable{

	Map<String, Object> gp;
	String period;
	String temp;
	String year;
	String month;
	String date;
	
	public TradeDataFindThread(Map<String, Object> gp,String period,String temp){
		this.gp = gp;
		this.period = period;
		this.temp = temp;
	}
	
	@Override
	public void run() {
		IFinderTrade iFinder163Trade = new Finder163Trade(gp, year, month, date);
		if("1".equals(temp)){
			
		}else{
			iFinder163Trade = new Finder163Trade(gp, year, month, date);
		}
		iFinder163Trade.findTrade();
	}

}
