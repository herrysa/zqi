package com.zqi.dataFinder;

import java.util.List;
import java.util.Map;

public interface IFinderBk {

	public static String[] bkColumn = {"code","symbol","name"};
	
	public List<Map<String, String>> findBkInfo();
	
	public String findBkInfoStr();
}
