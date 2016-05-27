package com.zqi.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.dataFinder.IFinderGpDic;
import com.zqi.dataFinder.se.FinderGpDicSe;
import com.zqi.dataFinder.wy163.FinderGpDic163Zhishu;
import com.zqi.frame.controller.BaseController;

@Controller
@RequestMapping("/init")
public class InitController extends BaseController{

	@RequestMapping("/initpage")
	public String init(){
		
		return "init/initpage";
	}
	@ResponseBody
	@RequestMapping("/createTable")
	public Map<String, Object> createTable(){
		try {
			createDicAndDayTable();
			createGpInfoTable();
			createGpCwInfoTable();
			createGpFhInfoTable();
			creatReportTable();
			creatIndicatorTable();
			creatLogTable();
			this.setMessage("建表成功！");
		} catch (Exception e) {
			this.setMessage("建表失败！");
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/checkDic")
	public String checkDic(){
		try {
			String dicSql = "select * from d_gpdic";
			List<Map<String, Object>> dicList = zqiDao.findAll(dicSql);
			Map<String, Map<String, Object>> dicMap = new HashMap<String, Map<String,Object>>();
			for(Map<String, Object> gpDic : dicList){
				String code = gpDic.get("code").toString();
				dicMap.put(code, gpDic);
			}
			IFinderGpDic iFinderGpDicSse = new FinderGpDicSe();
			List<Map<String, Object>> gpDicList = iFinderGpDicSse.findGpDic();
			List<Map<String, Object>> addGpList = new ArrayList<Map<String,Object>>();
			List<String> deleteList = new ArrayList<String>();
			for(Map<String, Object> gpNew : gpDicList){
				String code = gpNew.get("code").toString();
				Map<String, Object> gpDic = dicMap.get(code);
				boolean addFlag = false;
				if(gpDic!=null){
					String oldName = gpDic.get("name").toString();
					String newName = gpNew.get("name").toString();
					if(!oldName.equals(newName)){
						addFlag = true;
						deleteList.add("delete from d_gpdic where code='"+code+"'");
					}
				}else{
					addFlag = true;
				}
				if(addFlag){
					addGpList.add(gpNew);
				}
			}
			String[] delSqls = deleteList.toArray(new String[deleteList.size()]);
			zqiDao.bathUpdate(delSqls);
			zqiDao.addList(addGpList, "d_gpdic");
			System.out.println("--------------股票字典更新完毕-----------------");
			List<String> createDaytableSqls = new ArrayList<String>();
			for(Map<String, Object> gp : addGpList){
				String daytble = gp.get("daytable").toString();
				String findDaytable = "select count(*) count from information_schema.TABLES where table_name = '"+daytble+"' and TABLE_SCHEMA = 'zqi'";
				Map<String, Object> dayTableCount = zqiDao.findFirst(findDaytable);
				boolean exist = true;
				if(dayTableCount!=null){
					String xxxxxxxxxxxxxxxxxxxxxxx = dayTableCount.get("count").toString();
				}
				String createSql = "create table "+daytble+"(period date not null,code int,name varchar(20),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`));";
				if(!createDaytableSqls.contains(createSql)){
					createDaytableSqls.add(createSql);
				}
			}
			String[] sqls =  createDaytableSqls.toArray(new String[createDaytableSqls.size()]);
			zqiDao.bathUpdate(sqls);
			System.out.println("--------------日数据表更新完毕-----------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private void createDicAndDayTable(){
		String dicSql= "create table d_gpdic(code varchar(20),symbol varchar(20),name varchar(20),symbolName varchar(20),listDate varchar(10),totalShares varchar(20),totalFlowShares varchar(20),endDate varchar(10),pinyinCode varchar(10),type varchar(2),daytable varchar(20),remark varchar(50));";
		zqiDao.excute(dicSql);
		IFinderGpDic iFinderGpDicSse = new FinderGpDicSe();
		List<Map<String, Object>> gpDiList = iFinderGpDicSse.findGpDic();
		IFinderGpDic iFinderGpDicZishu = new FinderGpDic163Zhishu();
		gpDiList.addAll(iFinderGpDicZishu.findGpDic());
		zqiDao.addList(gpDiList, "d_gpdic");
		System.out.println("--------------股票字典添加完毕-----------------");
		List<String> createDaytableSqls = new ArrayList<String>();
		String daytableUnion = "";
		Set<String> daytableSet = new HashSet<String>();
		for(Map<String, Object> gp : gpDiList){
			String daytble = gp.get("daytable").toString();
			daytableSet.add(daytble);
			String createSql = "create table "+daytble+"(period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			if(!createDaytableSqls.contains(createSql)){
				createDaytableSqls.add(createSql);
			}
		}
		String report_daytble = "create table report_daytable (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		createDaytableSqls.add(report_daytble);
		for(String daytable : daytableSet){
			daytableUnion += daytable+",";
		}
		if(!"".equals(daytableUnion)){
			daytableUnion = daytableUnion.substring(0, daytableUnion.length()-1);
			String createSql = "create table daytable_all (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`))ENGINE=MRG_MyISAM DEFAULT CHARSET=utf8 INSERT_METHOD=LAST UNION=("+daytableUnion+");";
			createDaytableSqls.add(createSql);
		}
		String[] sqls =  createDaytableSqls.toArray(new String[createDaytableSqls.size()]);
		zqiDao.bathUpdate(sqls);
		System.out.println("--------------日数据表建立完毕-----------------");
	}
	
	private void createGpInfoTable(){
		String infoSql = "create table i_gpinfo(period date,code varchar(20),name varchar(20),infoType varchar(20),info varchar(100),PRIMARY KEY (`period`,`code`));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票信息表建立完毕-----------------");
	}
	
	private void createGpCwInfoTable(){
		String infoSql = "create table i_gpCw(period date,code varchar(20),name varchar(20),cwType varchar(20),cwData varchar(20),PRIMARY KEY (`period`,`code`));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票财务信息表建立完毕-----------------");
	}
	
	private void createGpFhInfoTable(){
		String createql= "create table i_gpFh(period date,code varchar(20),name varchar(20),ssrq varchar(10),info varchar(100),PRIMARY KEY (`period`,`code`));";
		zqiDao.excute(createql);
		System.out.println("--------------股票分红信息表建立完毕-----------------");
	}
	
	private void creatReportTable(){
		String createql= "create table r_report(code varchar(20),name varchar(20),type varchar(20),remark varchar(50));";
		zqiDao.excute(createql);
		System.out.println("--------------报表表建立完毕-----------------");
	}
	
	private void creatIndicatorTable(){
		String createql= "create table i_indicator(code varchar(20),formula varchar(1000),type varchar(10),dataSource varchar(500),dsDesc varchar(200),remark varchar(50));";
		zqiDao.excute(createql);
		System.out.println("--------------指标表建立完毕-----------------");
	}
	
	private void creatLogTable(){
		String createql= "create table _log(id varchar(32),type varchar(20),mainId varchar(50),assistId varchar(50),info varchar(100),logDate varchar(20));";
		zqiDao.excute(createql);
		System.out.println("--------------日志表建立完毕-----------------");
	}
}
