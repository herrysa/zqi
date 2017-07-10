package com.zqi.init;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InItTool {

	public static String[] createYearDayTable(List<Map<String, Object>> gpDicList , String year){
		List<String> createDaytableSqls = new ArrayList<String>();
		List<String> createIndexSqls = new ArrayList<String>();
		String daytableUnion = "";
		Set<String> daytableSet = new HashSet<String>();
		//List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpdic order by code asc");
		for(Map<String, Object> gp : gpDicList){
			String daytble = gp.get("daytable").toString();
			daytableSet.add(year+"_"+daytble);
			String createSql = "create table "+year+"_"+daytble+"(period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),isNew char(1) DEFAULT '0',isFh char(1) DEFAULT '0',d varchar(1000) NULL,PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			if(!createDaytableSqls.contains(createSql)){
				createDaytableSqls.add(createSql);
				createIndexSqls.add("CREATE INDEX "+year+"_"+daytble+"_period ON "+year+"_"+daytble+" (period);");
			}
		}
		Calendar calendar = Calendar.getInstance();
		int y = calendar.get(Calendar.YEAR);
		if((""+y).equals(year)){
			String daytable_lastMonth = "create table daytable_lastMonth (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			createDaytableSqls.add(daytable_lastMonth);
			createIndexSqls.add("CREATE INDEX daytable_lastMonth_period ON daytable_lastMonth (period);");
		}
		
		for(String daytable : daytableSet){
			daytableUnion += daytable+",";
		}
		if(!"".equals(daytableUnion)){
			daytableUnion = daytableUnion.substring(0, daytableUnion.length()-1);
			String createSql = "create table "+year+"_daytable_all (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),isNew char(1) DEFAULT '0',isFh char(1) DEFAULT '0',d varchar(1000) NULL,PRIMARY KEY (`period`,`code`))ENGINE=MRG_MyISAM DEFAULT CHARSET=utf8 INSERT_METHOD=LAST UNION=("+daytableUnion+");";
			//String createSql = "create table daytable_all (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),settlement_p decimal(10,3),open_p decimal(10,3),high_p decimal(10,3),low_p decimal(10,3),close_p decimal(10,3),prefq char(1) DEFAULT '0',invalid char(1) DEFAULT '0',PRIMARY KEY (`period`,`code`))ENGINE=MRG_MyISAM DEFAULT CHARSET=utf8 INSERT_METHOD=LAST UNION=("+daytableUnion+");";
			createDaytableSqls.add(createSql);
			createIndexSqls.add("CREATE INDEX "+year+"_daytable_all_period ON "+year+"_daytable_all (period);");
		}
		createDaytableSqls.addAll(createIndexSqls);
		String[] sqls =  createDaytableSqls.toArray(new String[createDaytableSqls.size()]);
		System.out.println("--------------"+year+"日数据表建立完毕-----------------");
		return sqls;
		//zqiDao.bathUpdate(sqls);
	}
}
