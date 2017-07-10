package com.zqi.strategy;

public interface IStrategyQuarz {

	public void init(String strategyCode);
	
	public void setCustom(String custom);
	
	public IStrategy run();
	
	public boolean isNeedAccount();

	public void setNeedAccount(boolean needAccount);
	
	public void setAccountCode(String accountCode);
}
