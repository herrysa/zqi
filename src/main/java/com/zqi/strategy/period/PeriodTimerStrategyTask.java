package com.zqi.strategy.period;

import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.StrategyJava;
import com.zqi.strategy.StrategyOutProcesser;
import com.zqi.strategy.account.Account;


public class PeriodTimerStrategyTask implements PeriodTimerTask{
	
	private StrategyJava quant ;
	private HQDataHandler hqDataHandler;
	private Account account ;
	private StrategyOutProcesser outProcesser ;
	
	public PeriodTimerStrategyTask(StrategyJava quant,HQDataHandler hqDataHandler,Account account,StrategyOutProcesser outProcesser){
		this.quant = quant;
		this.account = account;
		this.outProcesser = outProcesser;
		this.hqDataHandler = hqDataHandler;
	}
	
	@Override
	public void run(PeriodTimerState periodTimerState) {
		account.setCurrent_date(periodTimerState.getPeriod());
		account.beforeTrading();
		hqDataHandler.setYear(""+periodTimerState.getYear());
		quant.strategy(hqDataHandler, account, outProcesser, periodTimerState);
		account.afterTrading();
	}

}
