package com.zqi.hq.hqfunc;

import java.util.List;
import java.util.Map;

public interface IHQFunc {

	public void parse(Map<String, Object> hqData,Map<String, Object> lastHqData,List<Map<String, Object>> unusualList);
}
