package com.zqi.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class GpPool {
	
	private HQFinder hqFinder;
	private List<Map<String, Object>> codeList = new ArrayList<Map<String,Object>>();
	private List<Map<String, Object>> dicList = null;
	private String codeStr = "";
	private String[] codeArr ;
	
	/*private String start;
	private String end;
	private String ex_suspended;
	private String ex_new;
	private Map<String, String> optionMap;
	
	public String getStart() {
		return start;
	}

	public GpPool setStart(String start) {
		this.start = start;
		return this;
	}

	public String getEnd() {
		return end;
	}

	public GpPool setEnd(String end) {
		this.end = end;
		return this;
	}

	public String getEx_suspended() {
		return ex_suspended;
	}

	public GpPool setEx_suspended(String ex_suspended) {
		this.ex_suspended = ex_suspended;
		return this;
	}

	public String getEx_new() {
		return ex_new;
	}

	public GpPool setEx_new(String ex_new) {
		this.ex_new = ex_new;
		return this;
	}*/

	public GpPool(HQFinder hqFinder){
		this.hqFinder = hqFinder;
	}
	
	public List<Map<String, Object>> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<Map<String, Object>> codeList) {
		this.codeList = codeList;
	}

	public String getCodeStr() {
		return codeStr;
	}

	public void setCodeStr(String codeStr) {
		this.codeStr = codeStr;
	}
	
	public String[] getCodeArr() {
		return codeArr;
	}
	
	@SuppressWarnings("unchecked")
	public void findCodeArr() {
		if(codeList.isEmpty()&&"all".equals(codeStr)){
			codeList = hqFinder.findAll("select * from d_gpdic where type in ('0','1') order by code asc");
			codeArr = new String[codeList.size()];
			int i = 0;
			for(Map<String, Object> codeMap: codeList){
				String code = codeMap.get("code").toString();
				codeArr[i] = code;
				i++;
			}
		}
	}

	public void setCodeArr(String[] codeArr) {
		this.codeArr = codeArr;
	}
	
	private void makeCodeStr(){
		codeStr = "";
		codeArr = new String[codeList.size()];
		int i = 0;
		for(Map<String, Object> codeMap: codeList){
			String code = codeMap.get("code").toString();
			codeStr += "'"+code+"',";
			codeArr[i] = code;
			i++;
		}
		if(StringUtils.isEmpty(codeStr)){
			codeStr = "('')";
		}else{
			codeStr = "("+codeStr.substring(0,codeStr.length()-1)+")";
		}
	}

	public GpPool add(String code ){
		codeList = hqFinder.findAll("select * from d_gpdic where code="+code);
		makeCodeStr();
		return this;
	}
	
	public GpPool add(List<Map<String, Object>> gps){
		codeList.addAll(gps);
		makeCodeStr();
		return this;
	}
	
	public GpPool addRandom(int num){
		if(dicList==null){
			dicList = hqFinder.findAll("select * from d_gpdic where type in ('0','1')");
		}
		codeList.clear();
		for(int i=0;i<num;i++){
			int randomi =(int) (Math.random()*dicList.size()+1);
			codeList.add(dicList.get(randomi-1));
		}
		makeCodeStr();
		return this;
	}
	
	public GpPool addAll(){
		//codeList = hqFinder.findAll("select * from d_gpdic where type in ('0','1')");
		codeStr = "all";
		//makeCodeStr();
		return this;
	}
	
	public void addByJsLib(String flieName){
		
	}
	
	public void addByJavaLib(String libName){
		
	}
	
	/*public Map<String, String> getOptionMap(){
		if(optionMap==null){
			optionMap = new HashMap<String, String>();
		}
		optionMap.put("start", start);
		optionMap.put("end", end);
		optionMap.put("ex_suspended", ex_suspended);
		optionMap.put("ex_new", ex_new);
		return optionMap;
	}*/
	
	/*public HQDataBox getGpHq(){
		if(optionMap==null){
			getOptionMap();
		}
		List<Map<String, Object>> dataList = hqFinder.getGpHq(codeStr, optionMap);
		HQDataBox hqDataBox = new HQDataBox();
		hqDataBox.setDataList(dataList);
		return hqDataBox;
	}*/
}
