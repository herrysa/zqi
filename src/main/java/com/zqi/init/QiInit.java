package com.zqi.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.zqi.unit.DBHelper;

public class QiInit {
	static String basePath = "D://hsA";
    String[] table = {"d_gpDic"};
    String[] col = {};
    static Map<String, String> priceMap = new HashMap<String, String>();
	public static void main(String[] args) { 
		priceMap.put("period","0");
		priceMap.put("open","1");
		priceMap.put("high","2");
		priceMap.put("low","3");
		priceMap.put("close","4");
		priceMap.put("volume","5");
		priceMap.put("turnover","6");
		
		File parentFile = new File(basePath);
		String[] files = parentFile.list();
        for(String fileName : files){
        	File file = new File(basePath+"//"+fileName);
        	if(file.isDirectory()){
        		continue;
        	}
        	String name = fileName.split("\\.")[0];
        	String[] typeAndCode = name.split("#");
        	System.out.println("-------"+name+"-------");
        	name = typeAndCode[0]+typeAndCode[1];
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
                    	if(!rs.next()){
                    		dicSql= "insert into d_gpDic (code,name) values('"+name+"','"+title[1]+"');";
                        	dicDb = new DBHelper();
                        	dicDb.prepareStatementSql(dicSql);
                        	dicDb.pst.execute();
                    	}
                    }
                   /* dicSql= "select count(*) from information_schema.TABLES where table_name = 'g_d_"+name+"' and TABLE_SCHEMA = 'zqi'";
                	dicDb = new DBHelper();
                	dicDb.prepareStatementSql(dicSql);
                	rs = dicDb.pst.executeQuery();
                	boolean hasRs = rs.next();
                	int count = rs.getInt(1);
                	if(count==0){
                		dicSql= "create table g_d_"+name+"(period varchar(10) not null,open decimal(10,2),high decimal(10,2),low decimal(10,2),close decimal(10,2),volume decimal(20,2),turnover decimal(20,2));";
                    	dicDb = new DBHelper();
                    	dicDb.prepareStatementSql(dicSql);
                    	dicDb.pst.execute();
                	}
                    lineTxt = bufferedReader.readLine();
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
                        System.out.println(period+":"+price.length);
                        dicSql= "insert into g_d_"+name+"(period,open,high,low,close,volume,turnover) values ('"+period+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+turnover+"');";
                    	dicDb = new DBHelper();
                    	dicDb.prepareStatementSql(dicSql);
                    	dicDb.pst.execute();
                        lineIndex++;
                    }*/
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
}
