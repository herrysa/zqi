package com.zqi.strategy.period;

import com.zqi.strategy.account.Account;


public class CopyOfPeriodTimerAccountTask implements PeriodTimerTask{
	
	private Account account ;
	
	public CopyOfPeriodTimerAccountTask(Account account){
		this.account = account;
	}
	
	@Override
	public void run(PeriodTimerState periodTimerState) {
		//account.balance(periodTimerState);
	}

}
