package com.zqi.strategy.quant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.StrategyContext;
import com.zqi.strategy.StrategyJava;
import com.zqi.strategy.StrategyOutProcesser;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.account.AccountPosition;
import com.zqi.strategy.period.PeriodTimerState;

@Component("redom")
public class Redom extends StrategyJava {

	@Override
	public void init(StrategyContext context) {
		
		//context.setStart("2015-01-01").setEnd("2015-12-31");
		context.setNeedAccount(true);
	}

	@Override
	public void strategy(HQDataHandler hqDataHandler, Account account,
			StrategyOutProcesser outProcesser, PeriodTimerState periodTimerState) {
		String period = periodTimerState.getPeriod();
		//System.out.println(period);
		List<AccountPosition> positions = account.getPosition();
		for(AccountPosition accountPosition : positions){
			String code = accountPosition.getCode();
			Double price = accountPosition.getClose();
			Double yield = accountPosition.getYield();
			if(yield!=null&&(yield>=profit||yield<=loss)){
				account.order(period, code, -1, price, "");
			}
		}
		
		hqDataHandler.getGpPool().addRandom(10);
		
		List<Map<String, Object>> dataList = hqDataHandler.getHqDataList(period);
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> hqDataMap = dataList.get(i);
			if(hqDataMap==null||hqDataMap.isEmpty()){
				continue;
			}
			String code = hqDataMap.get("code").toString();
			BigDecimal close = (BigDecimal)hqDataMap.get("close");
			String isNew = hqDataMap.get("isNew").toString();
			if("1".equals(isNew)){
				System.out.println(1);
				continue;
			}
			if(close==null||close.compareTo(new BigDecimal(0))==0){
				continue;
			}
			account.order(period, code, ps , close.doubleValue(), "");
		}
	}

}
