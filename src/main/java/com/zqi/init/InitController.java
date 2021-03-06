package com.zqi.init;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.dataFinder.IFinderBk;
import com.zqi.dataFinder.IFinderGpDic;
import com.zqi.dataFinder.se.FinderGpDicSe;
import com.zqi.dataFinder.sina.FinderSinaBk;
import com.zqi.dataFinder.wy163.FinderGpDic163Zhishu;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

@Controller
@RequestMapping("/init")
public class InitController extends BaseController{
	//String exceptTable = "[r_report],[r_reportfunc]";
	String exceptTable = "";

	@RequestMapping("/initpage")
	public String init(){
		
		return "init/initpage";
	}
	@ResponseBody
	@RequestMapping("/createTable")
	public Map<String, Object> createTable(){
		try {
			dropTable();
			createDicAndDayTable();
			createGpBkTable();
			createGpInfoTable();
			//findBkInfo();
			createHqInfoTable();
			createGpWaveTable();
			createGpCwInfoTable();
			createGpFhInfoTable();
			creatReportTable();
			creatReportFuncTable();
			creatIndicatorTable();
			creatJGDTable();
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
	
	private void dropTable(){
		String databaseName = Tools.getResource("databaseName");
		String findTableSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"+databaseName+"'";
		List<Map<String, Object>> tableList = zqiDao.findAll(findTableSql);
		for(Map<String, Object> table : tableList){
			String tableName = table.get("TABLE_NAME").toString();
			if(!exceptTable.contains("["+tableName+"]")){
				String dropTable = "DROP TABLE IF EXISTS `"+tableName+"`; ";
				zqiDao.excute(dropTable);
				System.out.println("--------------删除"+tableName+"表-----------------");
			}
		}
	}
	
	private void createDicAndDayTable(){
		String dicSql= "create table d_gpdic(code varchar(20),symbol varchar(20),name varchar(20),symbolName varchar(20),listDate varchar(10),totalShares varchar(20),totalFlowShares varchar(20),endDate varchar(10),pinyinCode varchar(10),type varchar(2),daytable varchar(20),remark varchar(50));";
		zqiDao.excute(dicSql);
		IFinderGpDic iFinderGpDicSse = new FinderGpDicSe();
		List<Map<String, Object>> gpDiList = iFinderGpDicSse.findGpDic();
		IFinderGpDic iFinderGpDicZishu = new FinderGpDic163Zhishu();
		gpDiList.addAll(iFinderGpDicZishu.findGpDic());
		//zqiDao.addList(gpDiList, "d_gpdic");
		//List<String> createDaytableSqls = new ArrayList<String>();
		//String daytableUnion = "";
		//Set<String> daytableSet = new HashSet<String>();
		String gpDataCol = "code,symbol,name,symbolName,listDate,totalShares,totalFlowShares,endDate,pinyinCode,type,daytable";
		String[] gpDicCol = gpDataCol.split(",");
		String gpDataStr = "";
		for(Map<String, Object> gp : gpDiList){
			//String daytble = gp.get("daytable").toString();
			//daytableSet.add(daytble);
			//String createSql = "create table "+daytble+"(period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),settlement_p decimal(10,3),open_p decimal(10,3),high_p decimal(10,3),low_p decimal(10,3),close_p decimal(10,3),prefq char(1) DEFAULT '0',invalid char(1) DEFAULT '0',PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			//String createSql = "create table "+daytble+"(period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),isNew char(1) DEFAULT '0',isFh char(1) DEFAULT '0',PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			/*if(!createDaytableSqls.contains(createSql)){
				createDaytableSqls.add(createSql);
			}*/
			gpDataStr += Tools.getTxtData(gp, gpDicCol);
		}
		String basePath = Tools.getResource("baseDir");
		String dicPath = basePath+Tools.getResource("dicDir");
		String filePath = dicPath+"d_gpdic.txt";
		File bkFile = new File(filePath);
		if(bkFile.exists()){
			bkFile.delete();
		}
		FileUtil.writeFile(gpDataStr, filePath);
		String loadDataSql = "load data infile '"+filePath+"' into table d_gpdic("+gpDataCol+");";
		String deleteBkInfoSql = "delete from d_gpdic";
		zqiDao.excute(deleteBkInfoSql);
		zqiDao.excute(loadDataSql);
		//System.out.println("--------------股票字典添加完毕-----------------");
		//String daytable_lastMonth = "create table daytable_lastMonth (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3),mfsum decimal(10,3),mfratio2 decimal(20,3),mfratio10 decimal(20,3),PRIMARY KEY (`period`,`code`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		//createDaytableSqls.add(daytable_lastMonth);
		//String periodTable = "create table d_period (period date,week varchar(5),PRIMARY KEY (`period`))ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		//createDaytableSqls.add(periodTable);
		/*for(String daytable : daytableSet){
			daytableUnion += daytable+",";
		}*/
		/*if(!"".equals(daytableUnion)){
			daytableUnion = daytableUnion.substring(0, daytableUnion.length()-1);
			String createSql = "create table daytable_all (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),isNew char(1) DEFAULT '0',isFh char(1) DEFAULT '0',PRIMARY KEY (`period`,`code`))ENGINE=MRG_MyISAM DEFAULT CHARSET=utf8 INSERT_METHOD=LAST UNION=("+daytableUnion+");";
			//String createSql = "create table daytable_all (period date,code varchar(20),name varchar(20),type varchar(2),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),settlement_p decimal(10,3),open_p decimal(10,3),high_p decimal(10,3),low_p decimal(10,3),close_p decimal(10,3),prefq char(1) DEFAULT '0',invalid char(1) DEFAULT '0',PRIMARY KEY (`period`,`code`))ENGINE=MRG_MyISAM DEFAULT CHARSET=utf8 INSERT_METHOD=LAST UNION=("+daytableUnion+");";
			createDaytableSqls.add(createSql);
		}*/
		//String[] sqls =  createDaytableSqls.toArray(new String[createDaytableSqls.size()]);
		//zqiDao.bathUpdate(sqls);
		//System.out.println("--------------日数据表建立完毕-----------------");
	}
	
	private void createGpBkTable(){
		String infoSql = "create table d_gpbk(code varchar(20),name varchar(20),bkType varchar(20),bkName varchar(30),PRIMARY KEY (`code`,`bkType`,`bkName`));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票板块表建立完毕-----------------");
	}
	
	private void createGpInfoTable(){
		String infoSql = "create table i_gpinfo(period date,code varchar(20),name varchar(20),infoType varchar(20),info varchar(100));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票信息表建立完毕-----------------");
	}
	
	private void createGpWaveTable(){
		String infoSql = "create table i_gpwave(periodBegin date,periodEnd date,code varchar(20),name varchar(20),waveBegin decimal(10,3),waveEnd decimal(10,3),direct varchar(10),waveNum int,zf decimal(10,3),waveHigh decimal(10,3),waveLow decimal(10,3));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票波段信息表建立完毕-----------------");
		infoSql = "create table i_gpwave_status(`code` varchar(20) DEFAULT NULL,`name` varchar(20) DEFAULT NULL,`waveBegin` decimal(10,3) DEFAULT NULL,`period` varchar(10) DEFAULT NULL,`direct` int(4) DEFAULT NULL,`waveNum` int(4) DEFAULT NULL,`waveHigh` decimal(10,3) DEFAULT NULL,`waveLow` decimal(10,3) DEFAULT NULL,`zf` decimal(10,3) DEFAULT NULL);";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票波段状态表建立完毕-----------------");
		infoSql = "create table i_gpwavecenter(centerBegin date,centerEnd date,code varchar(20),name varchar(20),centerHigh decimal(10,3),centerLow decimal(10,3),centerHigh2 decimal(10,3),centerLow2 decimal(10,3),centerNum int,centerZf decimal(10,3),periodEdge date,waveEdge decimal(10,3),direct int,`left` char(1),waveNum int,waveZf decimal(10,3));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票中枢信息表建立完毕-----------------");
		infoSql = "create table i_gpwavecenter_status(code varchar(20) ,name varchar(20),waveBegin decimal(10,3),periodBegin varchar(10),direct int(4),waveNum int(4),`waveHigh` decimal(10,3) DEFAULT NULL,`waveLow` decimal(10,3) DEFAULT NULL,`zf` decimal(10,3) DEFAULT NULL);";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票中枢状态表建立完毕-----------------");
	}
	
	private void createHqInfoTable(){
		String infoSql = "create table i_hqRadar(datetime datetime,code varchar(20),name varchar(20),mtype varchar(10),ptype varchar(1),message varchar(50),info varchar(20),close decimal(10,3),changepercent decimal(10,3),volume decimal(20,3),amount decimal(20,3),turnoverrate decimal(10,3));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票行情雷达信息表建立完毕-----------------");
	}
	
	private void findBkInfo() {
		IFinderBk iFinderBk = new FinderSinaBk();
		String bkData = iFinderBk.findBkInfoStr();
		String basePath = Tools.getResource("baseDir");
		String dicPath = basePath+Tools.getResource("dicDir");
		String filePath = dicPath+"i_gpinfo.txt";
		File bkFile = new File(filePath);
		bkFile.deleteOnExit();
		FileUtil.writeFile(bkData, filePath);
		String dataCol = "period,code,name,infoType,info";
		String loadDataSql = "load data infile '"+filePath+"' into table i_gpinfo("+dataCol+");";
		String deleteBkInfoSql = "delete from i_gpinfo where infoType in ('gainianbankuai','diyu','bkshy','cyb','zxqy','zhishu_000001','zhishu_399001','hs300')";
		zqiDao.excute(deleteBkInfoSql);
		zqiDao.excute(loadDataSql);
		System.out.println("--------------板块信息更新完毕-----------------");
	}
	
	
	private void createGpCwInfoTable(){
		String infoSql = "create table i_gpCw(period date,code varchar(20),name varchar(20),cwType varchar(20),cwData varchar(20),PRIMARY KEY (`period`,`code`));";
		zqiDao.excute(infoSql);
		System.out.println("--------------股票财务信息表建立完毕-----------------");
	}
	
	private void createGpFhInfoTable(){
		String createql= "CREATE TABLE i_gpFh (code VARCHAR (20),name VARCHAR (30),fhYear VARCHAR (10),ggDate date,djDate date,cqDate date,sg DECIMAL (10, 3),zz DECIMAL (10, 3),fh DECIMAL (10, 3),sgss date,zzss date,sgdz date,zzdz date,pgss date,pgdz date,pg  DECIMAL (10, 3),pgprice  DECIMAL (10, 3),txt varchar(50));";
		zqiDao.excute(createql);
		System.out.println("--------------股票分红信息表建立完毕-----------------");
	}
	
	private void creatReportTable(){
		if(!exceptTable.contains("[r_report]")){
			String createql= "create table r_report(code varchar(20),name varchar(20),type varchar(2),dataSource varchar(500),dsDesc varchar(200),remark varchar(50),PRIMARY KEY (`code`));";
			zqiDao.excute(createql);
			System.out.println("--------------报表表建立完毕-----------------");
		}
	}
	
	private void creatReportFuncTable(){
		if(!exceptTable.contains("[r_reportFunc]")){
			String createql= "create table r_reportFunc(code varchar(20),name varchar(20),category varchar(20),type varchar(2),params varchar(100),funcSql varchar(500), remark varchar(50),PRIMARY KEY (`code`));";
			zqiDao.excute(createql);
			System.out.println("--------------报表函数表建立完毕-----------------");
		}
	}
	
	private void creatIndicatorTable(){
		String createql= "create table i_indicator(code varchar(20),formula varchar(1000),type varchar(10),dataSource varchar(500),dsDesc varchar(200),remark varchar(50));";
		zqiDao.excute(createql);
		System.out.println("--------------指标表建立完毕-----------------");
	}
	
	private void creatJGDTable(){
		String createql= "CREATE TABLE `i_jgd` (`tradeCode` varchar(20) NOT NULL COMMENT '成交编号',`businessType` varchar(20) NOT NULL COMMENT '业务名称',`period` varchar(10) NOT NULL COMMENT '成交日期',`code` varchar(10) DEFAULT NULL COMMENT '证券代码',`name` varchar(20) DEFAULT NULL COMMENT '证券名称',`price` decimal(10,3) DEFAULT NULL COMMENT '成交价格',`amount` int(4) DEFAULT NULL COMMENT '成交数量',`remainder` int(4) DEFAULT NULL COMMENT '剩余数量',`money` decimal(10,3) DEFAULT NULL COMMENT '成交金额',`cmoney` decimal(10,3) DEFAULT NULL COMMENT '清算金额',`cash` decimal(10,3) DEFAULT NULL COMMENT '剩余金额',`stampTex` decimal(10,3) DEFAULT NULL COMMENT '印花税',`transferFee` decimal(10,3) DEFAULT NULL COMMENT '过户费',`commission` decimal(10,3) DEFAULT NULL COMMENT '净佣金',`transferFee2` decimal(10,3) DEFAULT NULL COMMENT '交易规费',`frontFee` decimal(10,3) DEFAULT NULL COMMENT '前台费用',`entrustCode` varchar(20) DEFAULT NULL COMMENT '委托编号',`clientCode` varchar(20) DEFAULT NULL COMMENT '股东代码',PRIMARY KEY (`tradeCode`,`businessType`,`period`)) ;";
		zqiDao.excute(createql);
		System.out.println("--------------交割单表建立完毕-----------------");
		String createql2= "CREATE TABLE `i_trans` (`tradeCode` int(4) NOT NULL COMMENT '成交编号',`transCode` varchar(20) DEFAULT NULL COMMENT '交易名称',`period` date DEFAULT NULL COMMENT '成交日期',`code` varchar(10) DEFAULT NULL COMMENT '证券代码',`name` varchar(20) DEFAULT NULL COMMENT '证券名称',`price` decimal(10,3) DEFAULT NULL COMMENT '成交价格',`amount` int(4) DEFAULT NULL COMMENT '成交数量',`remainder` int(4) DEFAULT NULL COMMENT '剩余数量',`money` decimal(10,3) DEFAULT NULL COMMENT '成交金额',`cmoney` decimal(10,3) DEFAULT NULL COMMENT '清算金额',`cash` decimal(10,3) DEFAULT NULL COMMENT '剩余金额',`cost` decimal(10,3) DEFAULT NULL COMMENT '交易费',`speriod` date NOT NULL COMMENT 'sell成交日期',`sprice` decimal(10,3) DEFAULT NULL COMMENT '成交价格',`sremainder` int(4) DEFAULT NULL COMMENT '剩余数量',`smoney` decimal(10,3) DEFAULT NULL COMMENT '成交金额',`scmoney` decimal(10,3) DEFAULT NULL COMMENT '清算金额',`scash` decimal(10,3) DEFAULT NULL COMMENT '剩余金额',`scost` decimal(10,3) DEFAULT NULL COMMENT '交易费',`profit` decimal(10,3) DEFAULT NULL COMMENT '盈亏百分比',PRIMARY KEY (`tradeCode`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		zqiDao.excute(createql2);
		System.out.println("--------------交易信息表建立完毕-----------------");
	}
	
	private void creatLogTable(){
		String createql= "create table _log(id varchar(32),type varchar(20),mainId varchar(50),assistId varchar(50),info varchar(100),logDate varchar(20));";
		zqiDao.excute(createql);
		System.out.println("--------------日志表建立完毕-----------------");
	}
	
	@ResponseBody
	@RequestMapping("/exeSql")
	public Map<String, Object> exeSql(){
		List<Map<String, Object>> dics = zqiDao.findAll("select DISTINCT(daytable) from d_gpdic ");
		int[] yearArr = {2013,2014,2015,2026};
		for(int y : yearArr){
			for(Map<String, Object> dic : dics){
				String daytable = dic.get("daytable").toString();
				String tableName = y+"_"+daytable;
				String alterSql = "alter table "+tableName+" add d varchar(1000) NULL;";
				zqiDao.excute(alterSql);
			}
			String alterSql = "alter table "+y+"_daytable_all add d varchar(1000) NULL;";
			zqiDao.excute(alterSql);
		}
		return resultMap;
	}
}
