package com.zqi.dataFinder;

import java.util.List;
import java.util.Map;

public interface IFinderRToday {
	
	public static String[] rDayColumn = {"period","code","name","settlement","open","high","low","close","volume","amount","changeprice","changepercent","swing","turnoverrate","fiveminute","lb","wb","tcap","mcap","pe","mfsum","mfratio2","mfratio10"};
	public static String[] rDayColumnType = {"String","String","String","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal"};
	
	public List<Map<String, Object>> findRToday();
}
