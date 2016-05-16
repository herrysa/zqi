package com.zqi.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Strategy {

	private Map<String, String> title;
	private Map<String, String> initMap;
	private List<StrategyOut> outList;
	private String body;
	
	private String code;
	private List<Object> xData;
	
	private Strategy parent;

	public Strategy getParent() {
		return parent;
	}

	public void setParent(Strategy parent) {
		this.parent = parent;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		initMap.put("code", code);
	}

	public List<Object> getxData() {
		return xData;
	}

	public void setxData(List<Object> xData) {
		JSONArray xArr = JSONArray.fromObject(xData);
		String xDataStr = xArr.toString();
		initMap.put("xData", xDataStr);
		this.xData = xData;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<StrategyOut> getOutList() {
		return outList;
	}

	public void setOutList(List<StrategyOut> outList) {
		this.outList = outList;
	}

	public Map<String, String> getInitMap() {
		return initMap;
	}

	public void setInitMap(Map<String, String> initMap) {
		this.initMap = initMap;
	}

	public Map<String, String> getTitle() {
		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	
	public void init(List<String> contentList){
		boolean titleEnd = false,initEnd = false;
		title = new HashMap<String, String>();
		initMap = new HashMap<String, String>();
		outList = new ArrayList<StrategyOut>();
		for(String content : contentList){
			if("".equals(content)){
				continue;
			}else {
				content = content.trim();
				if(!titleEnd){
					if(content.startsWith("//")){
						if(content.equals("//init")){
							titleEnd = true;
							initEnd = true;
							continue;
						}
						content = content.replaceAll("/","");
						String[] titleContent = content.split(":");
						String key = titleContent[0].trim();
						String value = "";
						if(titleContent.length>1){
							value = titleContent[1].trim();
						}
						title.put(key, value);
						continue;
					}else{
						titleEnd = true;
					}
				}
				if(!initEnd){
					if(content.equals("//init")){
						initEnd = true;
						continue;
					}
					String[] initArr = content.split(";");
					for(String param :initArr){
						String[] paramArr = param.split("=");
						String var = paramArr[0].trim();
						String value = paramArr[1].trim();
						if(paramArr.length>1){
							if("out".equals(var)){
								String out = value;
								if(out.startsWith("{")){
									JSONObject outObject = JSONObject.fromObject(out);
									Iterator outIt = outObject.keys();
									while(outIt.hasNext()){
										String key = outIt.next().toString();
										String type = outObject.get(key).toString();
										StrategyOut strategyOut = new StrategyOut();
										strategyOut.setName(key);
										strategyOut.setType(type);
										outList.add(strategyOut);
									}
								}else{
									if(out.contains(",")){
										String[] outArr = out.split(",");
										for(String o :outArr){
											StrategyOut strategyOut = new StrategyOut();
											strategyOut.setName(o);
											strategyOut.setType("line");
											outList.add(strategyOut);
										}
									}else{
										if(value.contains("'")){
											value = value.replaceAll("'", "");
										}
										if(value.contains("\"")){
											value = value.replaceAll("\"", "");
										}
										StrategyOut strategyOut = new StrategyOut();
										strategyOut.setName(value);
										strategyOut.setType("line");
										outList.add(strategyOut);
									}
								}
							}else{
								initMap.put(var, value);
							}
						}
					}
					continue;
				}
				body += content;
			}
		}
	}
	
	public String getStrategyScript(){
		Set<Entry<String, String>> initSet = initMap.entrySet();
		String explainedStrategyHead ="";
		for(Entry<String, String> param : initSet){
			String varName = param.getKey();
			String func = param.getValue();
			if(func!=null){
				func = func.trim();
			}
			String varLine = varName+"="+func+";";
			explainedStrategyHead += varLine;
		}
		return utilSript+"\n"+explainedStrategyHead+"\n"+strategyBodyScript;
	}
}
