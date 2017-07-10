package com.zqi.strategy.signal;

import java.util.Map;
import java.util.Set;

public interface ISignal {

	public void filter(Set<Map<String, Object>> dataSet);
}
