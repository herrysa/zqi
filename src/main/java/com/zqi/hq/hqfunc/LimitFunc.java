package com.zqi.hq.hqfunc;

import java.util.List;
import java.util.Map;

public class LimitFunc implements IHQFunc{

	@Override
	public List<Map<String, Object>> parse(Map<String, Object> param) {
		Map<String, Object> hqData = (Map<String, Object>)param.get("hqData");
		Map<String, Object> lastHQData = (Map<String, Object>)param.get("lastHQData");
		Map<String, Object> hQStatusData = (Map<String, Object>)param.get("hQStatusData");
		
		return null;
	}

}
