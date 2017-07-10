package com.zqi.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.unit.FileUtil;

@Component("strategyFactoy")
@Scope("prototype")
public class StrategyJsFactoy {

	private ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	Map<String, Object> context;
	
	public StrategyJsFactoy(){
		String basePath = "E:/git/zqi/src/main/webapp/strategy/";
		String utilName = "util.js";
		
		context = new HashMap<String, Object>();
		context.put("basePath", basePath);
		
		String lib = "Data,indicator,lib";
		context.put("lib", lib);
		
		String utilSript = FileUtil.readFile(basePath+utilName);
		utilSript = utilSript.replaceAll("\t", "");
		utilSript = utilSript.replaceAll("\n", "");
		
		Map<String, String> utilMap = new HashMap<String, String>();
		utilMap.put("util", utilSript);
		context.put("util", utilMap);
		
	}
	
	public StrategyJs getStrategy(String fileName){
		context.put("dao", zqiDao);
		StrategyJs strategy = new StrategyJs();
		strategy.init(context,fileName);
		return strategy;
	}
	
	public static List<String> readStrategyFile( String filePath ) {
        File ds = null;
        BufferedReader br = null;
        //String fileContent = "";
        List<String> contentList = new ArrayList<String>();
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                br = new BufferedReader( new InputStreamReader(new FileInputStream(filePath),"UTF-8") );
                temp = br.readLine();
                while ( temp != null ) {
                    //fileContent += temp;
                    contentList.add(temp);
                    temp = br.readLine();
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( br != null ) {
                    br.close();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        return contentList;
    }
	
	public static void main(String[] args) {
		try {
			String str = "{a:[1,2,3]}";
			JSONObject jsonObject = JSONObject.fromObject(str);
			JSONArray a = (JSONArray)jsonObject.get("a");
			Object[] aa = (Object[])JSONArray.toArray(a);
			List<Object> userList = new ArrayList<Object>();
			Collections.addAll(userList, aa);
			System.out.println();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
