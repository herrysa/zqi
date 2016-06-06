package com.zqi.dataFinder;

import java.util.List;
import java.util.Map;

public interface IFinderFh {

	public static String[] fhColumn = {"code","fhYear","ggDate","djDate","cqDate","sg","zz","fh","sgss","zzss","sgdz","zzdz","txt"};
	
	public List<Map<String, Object>> findFhInfo(Map<String, Object> gp);
	
}
