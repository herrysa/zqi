package com.zqi.strategy;

import java.util.List;
import java.util.Map;

public interface IStrategy {

	public StrategyTitle getTitle();
	
	public List<StrategyOut> getOutList();
}
