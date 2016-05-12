package com.zqi.unit;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Test {

	public static void main(String[] args) {
		String util = FileUtil.readFile("E:/git/zqi/src/main/webapp/strategy/util.js");
		String ZRSI = FileUtil.readFile("E:/git/zqi/src/main/webapp/strategy/indicator/ZRSI.js");
		String[] ZRSIArr = ZRSI.split("//init");
		String[] initArr = ZRSIArr[0].split(";");
		ScriptEngineManager manager = new ScriptEngineManager();  
		ScriptEngine engine = manager.getEngineByName("js");
		Bindings bindings  = engine.createBindings();
		//bindings.put("aa", "{show:2}");
		try {
			engine.eval(new FileReader("E:\\git\\zqi\\src\\main\\webapp\\strategy\\indicator\\ZRSI.js"),bindings);
			System.out.println(bindings.get("result").toString());
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
