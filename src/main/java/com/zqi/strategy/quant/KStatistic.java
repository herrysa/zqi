package com.zqi.strategy.quant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.StrategyContext;
import com.zqi.strategy.StrategyJava;
import com.zqi.strategy.StrategyOut.OUTTYPE;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.period.PeriodTimerState;
import com.zqi.strategy.StrategyOutProcesser;

@Component("kStatistic")
public class KStatistic extends StrategyJava{

	String outName = "k线统计";
	@Override
	public void init(StrategyContext context) {
		getTitle().setName(outName);
		
		context.setStart("2016-01-01").setEnd("2016-01-30");
		context.getGppool().addAll();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cols", "上涨数量,下跌数量,平盘数量,总数");
		context.getStrategyOutProcesser().addAccuOut(outName, OUTTYPE.table).setParamMap(paramMap);
	}

	@Override
	public void strategy(HQDataHandler hqDataHandler,Account account,StrategyOutProcesser outProcesser ,PeriodTimerState periodTimerState){
		String period = account.getCurrent_date();
		List<Map<String, Object>> dataList = hqDataHandler.getHqDataList(period);
		double uNum = 0, dNum = 0 , oNum = 0 , sum = 0;
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> hqDataMap = dataList.get(i);
			if(hqDataMap==null){
				continue;
			}
			BigDecimal changepercent = (BigDecimal)hqDataMap.get("changepercent");
			int compare0 = changepercent.compareTo(new BigDecimal(0));
			if(compare0>0){
				uNum++;
			}else if(compare0==0){
				oNum++;
			}else{
				dNum++;
			}
			sum++;
		}
		Map<String, Object> numMap = new HashMap<String, Object>();
		numMap.put("上涨数量", uNum);
		numMap.put("下跌数量", dNum);
		numMap.put("平盘数量", oNum);
		numMap.put("总数", sum);
		outProcesser.putAccuValue(outName, numMap);
	}

}
