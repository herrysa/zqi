package com.zqi.dataFinder;

import java.util.Map;

public interface IDataFinder {

	public String findByJson(String url,Map<String, String> titleMap);
	
	public String findByHtml();
}
