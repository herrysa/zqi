package com.zqi.strategy;


public class StrategyConstant {

	static public String packageName = "com.zqi.strategy.quant";
	
	static public String quantList = "[{code:'kStatistic',name:'K线统计',type:'java'}"
									+ ",{code:'tRevert',name:'T线反转',type:'java'}"
									+ ",{code:'redom',name:'随机选股',type:'java'}"
									+ ",{code:'ema1',name:'均线交叉',type:'java'}]";
	
	static public String kStatistic = "";
	
	static public enum outType {  
        table, json, yellow, blue;  
    }
	
	static public enum DATATYPE{
		RHIS
	}
	
}
