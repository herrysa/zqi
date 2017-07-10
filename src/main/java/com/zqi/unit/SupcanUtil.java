package com.zqi.unit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

public class SupcanUtil {

	private Document document;
	private Element table;
	Element row ;
	
	public void creatTreeListDataTable(){
		document = XMLUtil.createDocument();
		table = document.addElement("table");
	}
	
	public void creatTreeListDataRow(){
		row = table.addElement("row");
	}
	
	public void creatTreeListDataCol(String name , String value){
		Element col = row.addElement(name);
		col.setText(value);
	}
	
	public String getXml(){
		return XMLUtil.xmltoString(document);
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> makeSqlList(Map<String, String> makeParam){
		String dataXml = makeParam.get("dataXml");
		String tableName = makeParam.get("tableName");
		String pkId = makeParam.get("pkId");
		String isNew = makeParam.get("isNew");
		String uuid = makeParam.get("uuid");
		String parentCol = makeParam.get("parentCol");
		String parentId = makeParam.get("parentId");
		
		List<String> sqlList = new ArrayList<String>();
		
		Document document = XMLUtil.stringToXml(dataXml);
		Element root = document.getRootElement();
		Iterator<Element> rowIt = root.elementIterator("row");
		while(rowIt.hasNext()){
			String colStr = "";
			String valueStr = "";
			String whereStr = "";
			Element row = rowIt.next();
			Iterator<Element> elementIt = row.elementIterator();
			
			if("1".equals(isNew)){
				//addd
				while(elementIt.hasNext()){
					Element dataElement = elementIt.next();
					String name = dataElement.getName();
					String value = dataElement.getText();
					String v = null;
					if(name.startsWith("ex_")){
						continue;
					}
					colStr += name+",";
					if(StringUtils.isNotEmpty(value)){
						v = "'"+value+"'";
					}
					if(pkId.equals(name)){
						if("1".equals(uuid)){
							String uuidValue = UUIDGenerator.getInstance().getNextValue();
							value = uuidValue;
							v ="'"+value+"'";
						}
						
						valueStr += v+",";
						//makeParam.put("parentCol", pkId);
						makeParam.put("parentId", value);
					}else{
						valueStr += v+",";
					}
				}
				
				if(!"".equals(colStr)){
					if(StringUtils.isNotEmpty(parentCol)){
						colStr += parentCol;
						valueStr += "'"+parentId+"'";
					}else{
						colStr = colStr.substring(0,colStr.length()-1);
						valueStr = valueStr.substring(0,valueStr.length()-1);
					}
					String formSql = "insert into "+tableName+" ( "+colStr+" ) values ("+valueStr+")";
					sqlList.add(formSql);
				}
			}else{
				//update
				while(elementIt.hasNext()){
					//String uuid = UUIDGenerator.getInstance().getNextValue();
					Element dataElement = elementIt.next();
					String name = dataElement.getName();
					String value = dataElement.getText();
					String v = null;
					if(name.startsWith("ex_")){
						continue;
					}
					if(StringUtils.isNotEmpty(value)){
						v = "'"+value+"'";
					}
					if(pkId.equals(name)){
						whereStr += "where "+pkId+"="+v+"";
						//makeParam.put("parentCol", pkId);
						makeParam.put("parentId", value);
					}else{
						colStr += name+"="+v+",";
					}
				}
				if(!"".equals(colStr)){
					colStr = colStr.substring(0,colStr.length()-1);
					String formSql = "update "+tableName+" set "+colStr+" "+whereStr;
					sqlList.add(formSql);
				}
			}
		}
		
		
		return sqlList;
	}
	
	public static String makeDataXml(List<Map<String, Object>> datas){
		Document document = XMLUtil.createDocument();
		Element table = document.addElement("table");
		for(Map<String, Object> data : datas){
			Element row = table.addElement("row");
			Set<String> cols = data.keySet();
			for(String col : cols){
				Element colElement = row.addElement(col);
				colElement.setText(data.get(col)==null?"":data.get(col).toString());
			}
		}
		return XMLUtil.xmltoString(document);
	}
	
	public static String makeColsXml(List<Map<String, Object>> datas){
		Document document = XMLUtil.createDocument();
		Element cols = document.addElement("cols");
		for(Map<String, Object> data : datas){
			String name = data.get("name").toString();
			String code = data.get("code").toString();
			//String datatype = data.get("datatype").toString();
			Element col = cols.addElement("col");
			col.setText(name);
			col.addAttribute("name",code);
			//col.addAttribute("datatype", datatype);
		}
		return XMLUtil.xmltoString(document);
	}
	
	public static String makeColumnsXml(List<Map<String, Object>> datas){
		Document document = XMLUtil.createDocument();
		Element cols = document.addElement("Columns");
		for(Map<String, Object> data : datas){
			String name = data.get("name").toString();
			String code = data.get("code").toString();
			//String datatype = data.get("datatype").toString();
			Element col = cols.addElement("Column");
			Element col_name = col.addElement("name");
			col_name.setText(code);
			Element col_text = col.addElement("text");
			col_text.setText(name);
			/*Element col_name = col.addElement("name");
			Element col_name = col.addElement("name");
			Element col_name = col.addElement("name");
			Element col_name = col.addElement("name");
			Element col_name = col.addElement("name");
			Element col_name = col.addElement("name");*/
			//col.addAttribute("datatype", datatype);
		}
		return XMLUtil.xmltoString(document);
	}
	
	public static String makeItemDataXml(List<Map<String, Object>> datas){
		Document document = XMLUtil.createDocument();
		Element items = document.addElement("items");
		for(Map<String, Object> data : datas){
			Element item = items.addElement("item");
			String key = data.get("id").toString();
			String value = data.get("name").toString();
			item.setText(value);
			item.addAttribute("key", key);
		}
		return XMLUtil.xmltoString(document);
	}
}
