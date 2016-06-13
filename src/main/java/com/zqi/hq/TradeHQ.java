package com.zqi.hq;

import java.util.List;
import java.util.Map;

import com.zqi.frame.util.TestTimer;
import com.zqi.primaryData.fileDataBase.TradeFileDataBase;

public class TradeHQ {

	public static void main(String[] args) {
		TestTimer tt = new TestTimer("readFile:");
		tt.begin();
		
		TradeFileDataBase tradeFileDataBase = new TradeFileDataBase("2016-06-08");
		
		for(int i=0 ;i<2000;i++){
			List<Map<String, Object>> tradeList = tradeFileDataBase.readList("000410");
			for(Map<String, Object> trade : tradeList){
				System.out.println();
			}
		}
		tt.done();
		System.out.println();
	}
	
}
