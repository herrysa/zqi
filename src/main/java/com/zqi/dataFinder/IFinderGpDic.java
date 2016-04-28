package com.zqi.dataFinder;

import java.util.List;
import java.util.Map;

public interface IFinderGpDic {
	public static String[] gpDicColumn = {"code","symbol","name","symbolName","listDate","totalShares","totalFlowShares","endDate","pinyinCode","daytable","remark"};
	public static String code = "symbol";
	public static String name = "symbolName";
	
	public List<Map<String, Object>> findGpDic();
	
}
