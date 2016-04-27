package com.zqi.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zqi.frame.util.Tools;
import com.zqi.unit.DBHelper;

public class QiInit {
	static String basePath = "D://hsA";
    String[] table = {"d_gpDic"};
    String[] col = {};
    static Map<String, String> priceMap = new HashMap<String, String>();
	public static void main(String[] args) { 
		creatZqiTable();
		creatGpInfo();
		createGpCwInfo();
		creatLogTable();
		//insertDayData();
		//findBkInfo();
		//Calendar calendar = Calendar.getInstance();
		//calendar.set(Calendar.MONTH, 2);
		//System.out.println(Calendar.getInstance().getTimeInMillis());
		//String a = findDayData("","");
    }
	
	public static void creatZqiTable(){
		System.out.println("---------------creatZqiTable---------------");
		String[] hs_aKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
		int i=0,daytableIndex = 1;
		for(int page=1;page<=50;page++){
			String hs_aKeyUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22hq%22,%22hs_a%22,%22%22,0,"+page+",60]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  hs_aDataList = getHttpUrlMap(hs_aKey,hs_aKeyUrl,titleMap);
			System.out.println((page)*hs_aDataList.size());
			try {
				DBHelper dicDb = new DBHelper();
				String dicSql= "select count(*) from information_schema.TABLES where table_name = 'd_gpdic' and TABLE_SCHEMA = 'zqi'";
				dicDb.prepareStatementSql(dicSql);
				ResultSet rs = dicDb.pst.executeQuery();
				int count = 0;
				while(rs.next()){
					count = rs.getInt(1);
					if(count==0){
						dicSql= "create table d_gpdic(symbol varchar(20),code varchar(20),name varchar(20),pinyinCode varchar(10),daytable varchar(20));";
						dicDb.prepareStatementSql(dicSql);
						dicDb.pst.execute();
					}
				}
				
				DBHelper dayTableDb = new DBHelper();
				String period = titleMap.get("day");
				//String total = titleMap.get("count");
				for(Map<String, String> gpData : hs_aDataList){
					daytableIndex = i/50+1;
					String symbol = gpData.get("symbol");
					String code = gpData.get("code");
					String name = gpData.get("name");
					String pinyinCode = Tools.getPYIndexStr(name, true);
					String open = gpData.get("open");
                    String high = gpData.get("high");
                    String low = gpData.get("low");
                    String close = gpData.get("trade");
                    String volume = gpData.get("volume");
                    String amount = gpData.get("amount");
					dicSql= "select * from d_gpDic where symbol='"+symbol+"'";
					dicDb.prepareStatementSql(dicSql);
					rs = dicDb.pst.executeQuery();
					if(!rs.next()){
						dicSql= "insert into d_gpDic (symbol,code,name,pinyinCode,daytable) values('"+symbol+"','"+code+"','"+name+"','"+pinyinCode+"','daytable"+daytableIndex+"');";
						dicDb.prepareStatementSql(dicSql);
						dicDb.pst.execute();
					}else{
						String dbName = rs.getString("name");
						if(!dbName.equals(name)){
							dicSql= "update d_gpDic set name='"+name+"',pinyinCode='"+pinyinCode+"' where symbol='"+symbol+"';";
							dicDb.prepareStatementSql(dicSql);
							dicDb.pst.execute();
						}
					}
					dicSql= "select count(*) from information_schema.TABLES where table_name = 'daytable"+daytableIndex+"' and TABLE_SCHEMA = 'zqi'";
					//dicDb = new DBHelper();
					dicDb.prepareStatementSql(dicSql);
					rs = dicDb.pst.executeQuery();
					while(rs.next()){
						count = rs.getInt(1);
						if(count==0){
							dicSql= "create table daytable"+daytableIndex+"(period varchar(10) not null,code varchar(20),name varchar(20),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3)),mfsum decimal(10,3),mfratio2 decimal(20,3)),mfratio10 decimal(20,3));";
							dicDb.prepareStatementSql(dicSql);
							dicDb.pst.execute();
							//dayTableDb.addBatchSql(dicSql);
						}
                        //System.out.println(period+":"+price.length);
                        //dicSql= "insert into daytable"+daytableIndex+"(period,code,name,open,high,low,close,volume,amount) values ('"+period+"','"+symbol+"','"+name+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+amount+"');";
                        //dayTableDb.addBatchSql(dicSql);
					}
					i++;
				}
				//dayTableDb.st.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void creatZqiTableWy(){
		System.out.println("---------------creatZqiTable网易---------------");
		String[] hs_aKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
		int i=0,daytableIndex = 1;
		for(int page=1;page<=50;page++){
			String hs_aKeyUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22hq%22,%22hs_a%22,%22%22,0,"+page+",60]]&callback=FDC_DC.theTableData";
			Map<String, String> titleMap = new HashMap<String, String>();
			List<Map<String, String>>  hs_aDataList = getHttpUrlMap(hs_aKey,hs_aKeyUrl,titleMap);
			System.out.println((page)*hs_aDataList.size());
			try {
				DBHelper dicDb = new DBHelper();
				String dicSql= "select count(*) from information_schema.TABLES where table_name = 'd_gpdic' and TABLE_SCHEMA = 'zqi'";
				dicDb.prepareStatementSql(dicSql);
				ResultSet rs = dicDb.pst.executeQuery();
				int count = 0;
				while(rs.next()){
					count = rs.getInt(1);
					if(count==0){
						dicSql= "create table d_gpdic(symbol varchar(20),code varchar(20),name varchar(20),pinyinCode varchar(10),daytable varchar(20));";
						dicDb.prepareStatementSql(dicSql);
						dicDb.pst.execute();
					}
				}
				
				DBHelper dayTableDb = new DBHelper();
				String period = titleMap.get("day");
				//String total = titleMap.get("count");
				for(Map<String, String> gpData : hs_aDataList){
					daytableIndex = i/50+1;
					String symbol = gpData.get("symbol");
					String code = gpData.get("code");
					String name = gpData.get("name");
					String pinyinCode = Tools.getPYIndexStr(name, true);
					String open = gpData.get("open");
                    String high = gpData.get("high");
                    String low = gpData.get("low");
                    String close = gpData.get("trade");
                    String volume = gpData.get("volume");
                    String amount = gpData.get("amount");
					dicSql= "select * from d_gpDic where symbol='"+symbol+"'";
					dicDb.prepareStatementSql(dicSql);
					rs = dicDb.pst.executeQuery();
					if(!rs.next()){
						dicSql= "insert into d_gpDic (symbol,code,name,pinyinCode,daytable) values('"+symbol+"','"+code+"','"+name+"','"+pinyinCode+"','daytable"+daytableIndex+"');";
						dicDb.prepareStatementSql(dicSql);
						dicDb.pst.execute();
					}else{
						String dbName = rs.getString("name");
						if(!dbName.equals(name)){
							dicSql= "update d_gpDic set name='"+name+"',pinyinCode='"+pinyinCode+"' where symbol='"+symbol+"';";
							dicDb.prepareStatementSql(dicSql);
							dicDb.pst.execute();
						}
					}
					dicSql= "select count(*) from information_schema.TABLES where table_name = 'daytable"+daytableIndex+"' and TABLE_SCHEMA = 'zqi'";
					//dicDb = new DBHelper();
					dicDb.prepareStatementSql(dicSql);
					rs = dicDb.pst.executeQuery();
					while(rs.next()){
						count = rs.getInt(1);
						if(count==0){
							dicSql= "create table daytable"+daytableIndex+"(period varchar(10) not null,code varchar(20),name varchar(20),settlement decimal(10,3),open decimal(10,3),high decimal(10,3),low decimal(10,3),close decimal(10,3),volume decimal(20,3),amount decimal(20,3),changeprice decimal(10,3),changepercent decimal(10,3),swing decimal(10,3),turnoverrate decimal(10,3),fiveminute decimal(10,3),lb decimal(10,3),wb decimal(10,3),tcap decimal(20,3),mcap decimal(20,3),pe decimal(10,3)),mfsum decimal(10,3),mfratio2 decimal(20,3)),mfratio10 decimal(20,3));";
							dicDb.prepareStatementSql(dicSql);
							dicDb.pst.execute();
							//dayTableDb.addBatchSql(dicSql);
						}
                        //System.out.println(period+":"+price.length);
                        //dicSql= "insert into daytable"+daytableIndex+"(period,code,name,open,high,low,close,volume,amount) values ('"+period+"','"+symbol+"','"+name+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+amount+"');";
                        //dayTableDb.addBatchSql(dicSql);
					}
					i++;
				}
				//dayTableDb.st.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void creatGpInfo(){
		System.out.println("---------------creatGpInfo---------------");
		try {
			DBHelper dbHelper = new DBHelper();
			String createql= "select count(*) from information_schema.TABLES where table_name = 'i_gpinfo' and TABLE_SCHEMA = 'zqi'";
			dbHelper.prepareStatementSql(createql);
			ResultSet rs;
			rs = dbHelper.pst.executeQuery();
			while(rs.next()){
				int count = rs.getInt(1);
				if(count==0){
					createql= "create table i_gpinfo(period varchar(10),code varchar(20),name varchar(20),infoType varchar(20),info varchar(100));";
					dbHelper.prepareStatementSql(createql);
					dbHelper.pst.execute();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createGpCwInfo(){
		System.out.println("---------------createGpCwInfo---------------");
		try {
			DBHelper dbHelper = new DBHelper();
			String createql= "select count(*) from information_schema.TABLES where table_name = 'i_gpCw' and TABLE_SCHEMA = 'zqi'";
			dbHelper.prepareStatementSql(createql);
			ResultSet rs;
			rs = dbHelper.pst.executeQuery();
			while(rs.next()){
		    	int count = rs.getInt(1);
		    	if(count==0){
		    		createql= "create table i_gpCw(period varchar(10),code varchar(20),name varchar(20),cwType varchar(20),cwData varchar(20));";
		    		dbHelper.prepareStatementSql(createql);
		    		dbHelper.pst.execute();
		    	}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createGpFhInfo(){
		try {
			DBHelper dbHelper = new DBHelper();
			String createql= "select count(*) from information_schema.TABLES where table_name = 'i_gpFh' and TABLE_SCHEMA = 'zqi'";
			dbHelper.prepareStatementSql(createql);
			ResultSet rs;
			rs = dbHelper.pst.executeQuery();
	    	int count = rs.getInt(1);
	    	if(count==0){
	    		createql= "create table i_gpFh(period varchar(10),code varchar(20),name varchar(20),ssrq varchar(10),info varchar(100));";
	    		dbHelper.prepareStatementSql(createql);
	    		dbHelper.pst.execute();
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void creatLogTable(){
		try {
			DBHelper dbHelper = new DBHelper();
			String createql= "select count(*) from information_schema.TABLES where table_name = '_log' and TABLE_SCHEMA = 'zqi'";
			dbHelper.prepareStatementSql(createql);
			ResultSet rs;
			rs = dbHelper.pst.executeQuery();
			while(rs.next()){
		    	int count = rs.getInt(1);
		    	if(count==0){
		    		createql= "create table _log(id varchar(32),type varchar(20),mainId varchar(50),assistId varchar(50),info varchar(100),logDate varchar(20));";
		    		dbHelper.prepareStatementSql(createql);
		    		dbHelper.pst.execute();
		    	}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void creatZqiTable1(){
		File parentFile = new File(basePath);
		String[] files = parentFile.list();
		int fileIndex = 0,daytableIndex = 1;
        for(String fileName : files){
        	daytableIndex = fileIndex/50+1;
        	fileIndex++;
        	File file = new File(basePath+"//"+fileName);
        	if(file.isDirectory()){
        		continue;
        	}
        	String name = fileName.split("\\.")[0];
        	String[] typeAndCode = name.split("#");
        	System.out.println("-------"+name+"-------");
        	name = typeAndCode[0]+typeAndCode[1];
        	String cnName = "";
        	//String dicSql= "select * from d_gpDic where code='"+fileName+"'";
        	String dicSql= "select * from d_gpDic where code='"+name+"'";
        	DBHelper dicDb = new DBHelper();
        	try {
                String encoding="GBK";
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null ;
                    int  lineIndex = 0;
                    lineTxt = bufferedReader.readLine();
                    dicDb.prepareStatementSql(dicSql);
                    ResultSet rs = dicDb.pst.executeQuery();
                    if(lineTxt!=null){
                    	String[] title = lineTxt.replace("  ", " ").split(" ");
                    	cnName = title[1];
                    	if(!rs.next()){
                    		dicSql= "insert into d_gpDic (code,name,daytable) values('"+name+"','"+cnName+"','daytable"+daytableIndex+"');";
                        	//dicDb = new DBHelper();
                        	dicDb.prepareStatementSql(dicSql);
                        	dicDb.pst.execute();
                    	}
                    }
                   dicSql= "select count(*) from information_schema.TABLES where table_name = 'daytable"+daytableIndex+"' and TABLE_SCHEMA = 'zqi'";
                	//dicDb = new DBHelper();
                	dicDb.prepareStatementSql(dicSql);
                	rs = dicDb.pst.executeQuery();
                	boolean hasRs = rs.next();
                	int count = rs.getInt(1);
                	if(count==0){
                		dicSql= "create table daytable"+daytableIndex+"(period varchar(10) not null,code varchar(20),name varchar(20),open decimal(10,2),high decimal(10,2),low decimal(10,2),close decimal(10,2),volume decimal(20,2),turnover decimal(20,2));";
                    	//dicDb = new DBHelper();
                    	dicDb.prepareStatementSql(dicSql);
                    	dicDb.pst.execute();
                	}
                	 read.close();
                }else{
                    System.out.println("找不到指定的文件");
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
	}
	public static List<String[]> findDayData(String code,String year,String jidu){
		String str = "";
		List<String[]> dayList = new ArrayList<String[]>();
        //创建一个webclient
        try {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);

        java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF); 
        //htmlunit 对css和javascript的支持不好，所以请关闭之
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        //webClient.waitForBackgroundJavaScript(600*1000);  
        //webClient.setAjaxController(new NicelyResynchronizingAjaxController()); 
        //获取页面
        HtmlPage page;
            page = webClient.getPage("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/"+code+".phtml?year="+year+"&jidu="+jidu);
        //webClient.waitForBackgroundJavaScript(1000*5); 
        //webClient.setJavaScriptTimeout(5000);  
//        //获取页面的TITLE
//        str = page.getTitleText();
//        System.out.println(str);
//        //获取页面的XML代码
//        str = page.asXml();
//        System.out.println(str);
//        //获取页面的文本
//        str = page.asText();
//        System.out.println(str);
//        str = page.asXml();
//        System.out.println(str);
        DomElement domElement = page.getElementById("FundHoldSharesTable");
        if(domElement!=null){
        	str = domElement.asText();
        	str = str.replaceAll("\r\n\t\r\n", " ").replaceAll("\r\n", "\t");
        	String[] rowArr = str.split("\t");
        	for(String row : rowArr){
        		String[] colArr = row.split(" ");
        		dayList.add(colArr);
        	}
        }
        //System.out.println(domElement.asText());

        //关闭webclient
        webClient.closeAllWindows();
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dayList;
	}
	public static void insertDayData(){
		priceMap.put("period","0");
		priceMap.put("open","1");
		priceMap.put("high","2");
		priceMap.put("low","3");
		priceMap.put("close","4");
		priceMap.put("volume","5");
		priceMap.put("turnover","6");
		
		File parentFile = new File(basePath);
		String[] files = parentFile.list();
		int fileIndex = 0,daytableIndex = 1,fileFrom = 0,fileTo = 10000;
        for(String fileName : files){
        	if(fileIndex>fileTo){
        		break;
        	}
        	if(fileFrom>fileIndex){
        		fileIndex++;
        		continue;
        	}
        	
        	daytableIndex = fileIndex/50+1;
        	fileIndex++;
        	File file = new File(basePath+"//"+fileName);
        	if(file.isDirectory()){
        		continue;
        	}
        	String name = fileName.split("\\.")[0];
        	String[] typeAndCode = name.split("#");
        	System.out.println("-------"+name+"-------");
        	name = typeAndCode[0]+typeAndCode[1];
        	String cnName = "";
        	//String dicSql= "select * from d_gpDic where code='"+fileName+"'";
        	String dicSql= "select * from d_gpDic where code='"+name+"'";
        	DBHelper dicDb = new DBHelper();
        	try {
                String encoding="GBK";
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null ;
                    int  lineIndex = 0;
                    lineTxt = bufferedReader.readLine();
                    dicDb.prepareStatementSql(dicSql);
                    ResultSet rs = dicDb.pst.executeQuery();
                    if(lineTxt!=null){
                    	String[] title = lineTxt.replace("  ", " ").split(" ");
                    	cnName = title[1];
                    	if(!rs.next()){
                    		dicSql= "insert into d_gpDic (code,name,daytable) values('"+name+"','"+cnName+"','daytable"+daytableIndex+"');";
                        	//dicDb = new DBHelper();
                        	dicDb.prepareStatementSql(dicSql);
                        	dicDb.pst.execute();
                    	}
                    }
                   dicSql= "select count(*) from information_schema.TABLES where table_name = 'daytable"+daytableIndex+"' and TABLE_SCHEMA = 'zqi'";
                	//dicDb = new DBHelper();
                	dicDb.prepareStatementSql(dicSql);
                	rs = dicDb.pst.executeQuery();
                	boolean hasRs = rs.next();
                	int count = rs.getInt(1);
                	if(count==0){
                		dicSql= "create table daytable"+daytableIndex+"(period varchar(10) not null,code varchar(20),name varchar(20),open decimal(10,2),high decimal(10,2),low decimal(10,2),close decimal(10,2),volume decimal(20,2),turnover decimal(20,2));";
                    	//dicDb = new DBHelper();
                    	dicDb.prepareStatementSql(dicSql);
                    	dicDb.pst.execute();
                	}
                    /*lineTxt = bufferedReader.readLine();
                    DBHelper dayDb = new DBHelper();
                    while((lineTxt = bufferedReader.readLine()) != null){
                        //System.out.println(lineTxt);
                        String[] price = lineTxt.split("\t");
                        String period = "";
                        String open = "0";
                        String high = "0";
                        String low = "0";
                        String close = "0";
                        String volume = "0";
                        String turnover = "0";
                        if(price.length>6){
                        	turnover = price[Integer.parseInt(priceMap.get("turnover"))];
                        }
                        if(price.length>5){
                        	volume = price[Integer.parseInt(priceMap.get("volume"))];
                        }
                        if(price.length>4){
                        	close = price[Integer.parseInt(priceMap.get("close"))];
                        }
                        if(price.length>3){
                        	low = price[Integer.parseInt(priceMap.get("low"))];
                        }
                        if(price.length>2){
                        	high = price[Integer.parseInt(priceMap.get("high"))];
                        }
                        if(price.length>1){
                        	open = price[Integer.parseInt(priceMap.get("open"))];
                        }
                        if(price.length>0){
                        	period = price[Integer.parseInt(priceMap.get("period"))];
                        }
                        //System.out.println(period+":"+price.length);
                        dicSql= "insert into daytable"+daytableIndex+"(period,code,name,open,high,low,close,volume,turnover) values ('"+period+"','"+name+"','"+cnName+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+turnover+"');";
                        dayDb.addBatchSql(dicSql);
                    	//dicDb.prepareStatementSql(dicSql);
                    	//dicDb.pst.execute();
                        lineIndex++;
                    }*/
                    //dayDb.st.executeBatch();
                    //DBHelper logDb = new DBHelper();
                    //logDb.
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        }
	}
	
	public static void findBkInfo() {
		int findType = 1;
		String[] bkCodeArr = {"gainianbankuai","diyu","bkshy"};
		DBHelper dicDb = new DBHelper();
		String deleteBkInfoSql = "";
		int i = 0 ;
		if(findType<=1){
			deleteBkInfoSql = "delete from i_gpinfo where infoType in ('gainianbankuai','diyu','bkshy')";
			dicDb.prepareStatementSql(deleteBkInfoSql);
			try {
				dicDb.pst.execute();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			for(String bkCode : bkCodeArr){
				findBkGp(bkCode);
				System.out.println("-------"+bkCode+"--------");
			}
		}
		if(findType<=2){
			String[] zs1BkCodeArr = {"cyb","zxqy"};
			String[] zs1BkNameArr = {"创业板","中小板"};
			deleteBkInfoSql = "delete from i_gpinfo where infoType in ('cyb','zxqy')";
			dicDb.prepareStatementSql(deleteBkInfoSql);
			try {
				dicDb.pst.execute();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			for(String bkCode : zs1BkCodeArr){
				findBkGp(bkCode,zs1BkNameArr[i],null,null);
				System.out.println("-------"+zs1BkNameArr[i]+"--------");
				i++;
			}
		}
		if(findType<=3){
			String[] zs2BkCodeArr = {"zhishu_000001","zhishu_399001","hs300"};
			String[] zs2BkNameArr = {"上证","深证","沪深300"};
			deleteBkInfoSql = "delete from i_gpinfo where infoType in ('zhishu_000001','zhishu_399001','hs300')";
			dicDb.prepareStatementSql(deleteBkInfoSql);
			try {
				dicDb.pst.execute();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			i = 0 ;
			for(String bkCode : zs2BkCodeArr){
				String[] gnbkgpKey = {"symbol","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","code","ticktime","focus","fund"};
				String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22jjhq%22,1,2000,%22%22,0,%22"+bkCode+"%22]]&callback=FDC_DC.theTableData";
				findBkGp(bkCode,zs2BkNameArr[i],url,gnbkgpKey);
				System.out.println("-------"+zs2BkNameArr[i]+"--------");
				i++;
			}
		}
	}
	
	public static void findBkGp(String bkCode) {
		String[] gnbkKey = {"name","code","number","count","volume","amount","trade","changeprice","changepercent","symbol","sname","strade","schangeprice","schangepercent"};
		String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bknode%22,%22"+bkCode+"%22,%22%22,0]]&callback=FDC_DC.theTableData";
		Map<String, String> titleMap = new HashMap<String, String>();
		List<Map<String, String>>  gnbkDataList = getHttpUrlMap(gnbkKey,url,titleMap);
		SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate=new Date();
		String period = myFmt2.format(nowDate);
		//String period = titleMap.get("day");
		DBHelper dicDb = new DBHelper();
		List<String> updateSqlList = new ArrayList<String>();
		for(Map<String, String> gnbkData :gnbkDataList){
			String code = gnbkData.get("code");
			String bkName = gnbkData.get("name");
			String[] gnbkgpKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
			String gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bkshy_node%22,%22"+code+"%22,%22%22,0,1,40]]&callback=FDC_DC.theTableData";
			List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl,null);
			for(Map<String, String> gnbkgpData : gnbkgpDataList){
				String symbol = gnbkgpData.get("symbol");
				String gpName = gnbkgpData.get("name");
				//String updateGnbkSql = "update d_gpdic set b_gn='"+bkName+"' where code='"+symbol.toUpperCase()+"'";
				String updateGnbkSql = "insert into i_gpinfo (period,code,name,infoType,info) values('"+period+"','"+symbol+"','"+gpName+"','"+bkCode+"','"+bkName+"')";
				//updateSqlList.add(updateGnbkSql);
				dicDb.addBatchSql(updateGnbkSql);
			}
		}
		try {
			dicDb.st.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void findBkGp(String bkCode,String bkName,String url,String[] dataKey){
		String[] gnbkgpKey = {"symbol","code","name","trade","pricechange","changepercent","buy","sell","settlement","open","high","low","volume","amount","ticktime","per","per_d","nta","pb","mktcap","nmc","turnoverratio","favor","guba"};
		String gnbkgpUrl = "";
		if(url==null){
			gnbkgpUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22hq%22,%22"+bkCode+"%22,%22%22,0,1,2000]]&callback=FDC_DC.theTableData";
		}else{
			gnbkgpUrl = url;
		}
		if(dataKey!=null){
			gnbkgpKey = dataKey;
		}
		Map<String, String> titleMap = new HashMap<String, String>();
		List<Map<String, String>>  gnbkgpDataList = getHttpUrlMap(gnbkgpKey,gnbkgpUrl,titleMap);
		//String period = titleMap.get("day");
		SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate=new Date();
		String period = myFmt2.format(nowDate);
		DBHelper dicDb = new DBHelper();
		for(Map<String, String> gnbkgpData : gnbkgpDataList){
			String symbol = gnbkgpData.get("symbol");
			String gpName = gnbkgpData.get("name");
			//String updateGnbkSql = "update d_gpdic set b_gn='"+bkName+"' where code='"+symbol.toUpperCase()+"'";
			String updateGnbkSql = "insert into i_gpinfo (period,code,name,infoType,info) values('"+period+"','"+symbol+"','"+gpName+"','"+bkCode+"','"+bkName+"')";
			//updateSqlList.add(updateGnbkSql);
			dicDb.addBatchSql(updateGnbkSql);
		}
		try {
			dicDb.st.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<Map<String,String>> getHttpUrlMap(String[] keys,String url,Map<String, String> titleMap){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			/*Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
			System.out.println(key + "--->" + map.get(key));
			}*/
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
			connection.getInputStream(),"GBK"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
			    if (in != null) {
			        in.close();
			    }
			} catch (Exception e2) {
			    e2.printStackTrace();
			}
		}
		if(result.contains("theTableData")){
			result = result.substring(result.indexOf("theTableData")+13, result.length()-1);
			JSONArray rsArr = JSONArray.fromObject(result);
			JSONObject rsObject = (JSONObject) rsArr.get(0);
			if(titleMap!=null){
				Object dayObject = rsObject.get("day");
				if(dayObject!=null){
					titleMap.put("day", dayObject.toString());
				}
				//titleMap.put("day", rsObject.getString("day"));
				titleMap.put("count", rsObject.getString("count"));
			}
			JSONArray items = (JSONArray) rsObject.get("items");
			Iterator<JSONArray> itemIt = items.iterator();
			while (itemIt.hasNext()) {
				JSONArray item = itemIt.next();
				Iterator<JSONArray> propertyIt = item.iterator();
				int i=0;
				Map<String, String> data = new HashMap<String, String>();
				while (propertyIt.hasNext()) {
					Object property = propertyIt.next();
					data.put(keys[i], property.toString());
					i++;
				}
				dataList.add(data);
			}
		}else{
			System.out.println(url);
		}
		return dataList;
	}
}
