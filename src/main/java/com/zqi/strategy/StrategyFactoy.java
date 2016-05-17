package com.zqi.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

@Component("strategyFactoy")
public class StrategyFactoy {

	private ZqiDao zqiDao;
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	@Autowired
	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}
	
	Map<String, Object> context;
	
	public StrategyFactoy(){
		String basePath = "E:/git/zqi/src/main/webapp/strategy/";
		String utilName = "util.js";
		
		context = new HashMap<String, Object>();
		context.put("basePath", basePath);
		
		String utilSript = FileUtil.readFile(basePath+utilName);
		utilSript = utilSript.replaceAll("\t", "");
		utilSript = utilSript.replaceAll("\n", "");
		
		Map<String, String> utilMap = new HashMap<String, String>();
		utilMap.put("util", utilSript);
		context.put("util", utilMap);
		
		context.put("dao", zqiDao);
	}
	
	public Strategy getStrategy(String fileName){
		Strategy strategy = new Strategy();
		strategy.init(context,fileName);
		return strategy;
	}
	
	public static List<String> readStrategyFile( String filePath ) {
        File ds = null;
        FileReader fr = null;
        BufferedReader br = null;
        //String fileContent = "";
        List<String> contentList = new ArrayList<String>();
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                fr = new FileReader( ds );
                br = new BufferedReader( fr );
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
                if ( fr != null ) {
                    fr.close();
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
