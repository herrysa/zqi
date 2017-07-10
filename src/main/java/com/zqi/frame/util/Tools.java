package com.zqi.frame.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.zqi.unit.DateUtil;

public class Tools {

	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("zqi");
	
	public static ResourceBundle getResourceBundle(String bundleName){
		ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName);
		return resourceBundle;
	}
	
	public static String getResource(String key){
		return resourceBundle.getString(key);
	}
	
	/**
	* 返回首字母
	* @param strChinese
	* @param bUpCase
	* @return
	*/
	public static String getPYIndexStr(String strChinese, boolean bUpCase){
	       try{
	           StringBuffer buffer = new StringBuffer();
	           byte b[] = strChinese.getBytes("GBK");//把中文转化成byte数组
	           for(int i = 0; i < b.length; i++){
	               if((b[i] & 255) > 128){
	                   int char1 = b[i++] & 255;
	                   char1 <<= 8;//左移运算符用“<<”表示，是将运算符左边的对象，向左移动运算符右边指定的位数，并且在低位补零。其实，向左移n位，就相当于乘上2的n次方
	                   int chart = char1 + (b[i] & 255);
	                   buffer.append(getPYIndexChar((char)chart, bUpCase));
	                   continue;
	               }
	               char c = (char)b[i];
	               if(!Character.isJavaIdentifierPart(c))//确定指定字符是否可以是 Java 标识符中首字符以外的部分。
	                   c = 'A';
	               buffer.append(c);
	           }
	           return buffer.toString();
	       }catch(Exception e){
	           System.out.println((new StringBuilder()).append("\u53D6\u4E2D\u6587\u62FC\u97F3\u6709\u9519").append(e.getMessage()).toString());
	       }
	       return null;
	   }
	
	 /**
	    * 得到首字母
	    * @param strChinese
	    * @param bUpCase
	    * @return
	    */
	   private static char getPYIndexChar(char strChinese, boolean bUpCase){
	       int charGBK = strChinese;
	       char result;
	       if(charGBK >= 45217 && charGBK <= 45252)
	           result = 'A';
	       else
	       if(charGBK >= 45253 && charGBK <= 45760)
	           result = 'B';
	       else
	       if(charGBK >= 45761 && charGBK <= 46317)
	           result = 'C';
	       else
	       if(charGBK >= 46318 && charGBK <= 46825)
	           result = 'D';
	       else
	       if(charGBK >= 46826 && charGBK <= 47009)
	           result = 'E';
	       else
	       if(charGBK >= 47010 && charGBK <= 47296)
	           result = 'F';
	       else
	       if(charGBK >= 47297 && charGBK <= 47613)
	           result = 'G';
	       else
	       if(charGBK >= 47614 && charGBK <= 48118)
	           result = 'H';
	       else
	       if(charGBK >= 48119 && charGBK <= 49061)
	           result = 'J';
	       else
	       if(charGBK >= 49062 && charGBK <= 49323)
	           result = 'K';
	       else
	       if(charGBK >= 49324 && charGBK <= 49895)
	           result = 'L';
	       else
	       if(charGBK >= 49896 && charGBK <= 50370)
	           result = 'M';
	       else
	       if(charGBK >= 50371 && charGBK <= 50613)
	           result = 'N';
	       else
	       if(charGBK >= 50614 && charGBK <= 50621)
	           result = 'O';
	       else
	       if(charGBK >= 50622 && charGBK <= 50905)
	           result = 'P';
	       else
	       if(charGBK >= 50906 && charGBK <= 51386)
	           result = 'Q';
	       else
	       if(charGBK >= 51387 && charGBK <= 51445)
	           result = 'R';
	       else
	       if(charGBK >= 51446 && charGBK <= 52217)
	           result = 'S';
	       else
	       if(charGBK >= 52218 && charGBK <= 52697)
	           result = 'T';
	       else
	       if(charGBK >= 52698 && charGBK <= 52979)
	           result = 'W';
	       else
	       if(charGBK >= 52980 && charGBK <= 53688)
	           result = 'X';
	       else
	       if(charGBK >= 53689 && charGBK <= 54480)
	           result = 'Y';
	       else
	       if(charGBK >= 54481 && charGBK <= 55289)
	           result = 'Z';
	       else
	           result = (char)(65 + (new Random()).nextInt(25));
	       if(!bUpCase)
	           result = Character.toLowerCase(result);
	       return result;
	   }
	   
	   public static List<Map<String,String>> getHttpUrlMapSina(String[] keys,String url,Map<String, String> titleMap){
		   String result = getByHttpUrl(url);
		   List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
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
	   public static List<Map<String,String>> getHttpUrlMap163(String url,Map<String, String> titleMap){
		   String result = getByHttpUrl(url);
		   List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		   if(result!=null&&!"".equals(result)){
				JSONArray rsArr = JSONArray.fromObject(result);
				JSONObject rsObject = (JSONObject) rsArr.get(0);
				if(titleMap!=null){
					Object dayObject = rsObject.get("time");
					if(dayObject!=null){
						String day = dayObject.toString().substring(0, 10);
						titleMap.put("day", day);
					}
					titleMap.put("count", rsObject.getString("total"));
				}
				JSONArray items = (JSONArray) rsObject.get("list");
				Iterator<JSONObject> itemIt = items.iterator();
				while (itemIt.hasNext()) {
					JSONObject item = itemIt.next();
					Set<String> keySet = item.keySet();
					int i=0;
					Map<String, String> data = new HashMap<String, String>();
					for(String key : keySet){
						Object value = item.get(key);
						String v = "";
						if(value!=null){
							if(value instanceof JSONObject){
								JSONObject subValue = (JSONObject)value;
								Set<String> keySet2 = subValue.keySet();
								for(String key2 : keySet2){
									Object value2 = subValue.get(key2);
									String v2 = "";
									if(value2!=null){
										v2 = value2.toString();
										data.put(key+"."+key2, v2);
									}
								}
							}else{
								v = value.toString();
								data.put(key, v);
							}
						}
					}
					dataList.add(data);
				}
			}else{
				System.out.println(url);
			}
			return dataList;
	   }
	   
	   public static String getByHttpUrl(String url){
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
				//connection.setConnectTimeout(1000);
				//connection.setReadTimeout(1000);
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
			return result;
	   }

	public static String findParentheses(String str){
		String pattern = "\\(.*\\)";
		String matcherStr = "";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		//int matcherCount = 0;
		while(matcher.find()){
			matcherStr = matcher.group();
			//matcherCount++;
		}
		return matcherStr;
	}
	
	//获取''字符串
	public static String findStr(String str,List<String> matcherList){
		String pattern = "'(.| )+?'";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		int matcherCount = 0;
		while(matcher.find()){
			String matcherStr = matcher.group();
			matcherList.add(matcherStr);
			str = str.replace(matcherStr, "STR_"+matcherCount);
			matcherCount++;
		}
		return str;
	}
	
	public static String findMethod(String str,List<String> matcherList){
		String pattern = "@.*\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		int matcherCount = 0;
		while(matcher.find()){
			String matcherStr = matcher.group();
			matcherList.add(matcherStr);
			str = str.replace(matcherStr, "@_"+matcherCount);
			matcherCount++;
		}
		return str;
	}
	
	public static String getTxtData(Map<String,Object> dataMap,String[] colArr){
		String dataLine = "";
		for(String col : colArr){
			dataLine += dataMap.get(col)+"\t";
		}
		return dataLine+"\n";
	}
	
	public static  String getExcelByHttpUrl(String url){
		String result = "";
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//connection.setConnectTimeout(1000);
			//connection.setReadTimeout(1000);
			// 建立实际的连接
			connection.connect();
			Workbook wb = new HSSFWorkbook(new BufferedInputStream(connection.getInputStream()));
			Sheet sheet = wb.getSheetAt(0);
			int lastRow = sheet.getLastRowNum();
			for(int rowNum=0;rowNum<=lastRow;rowNum++){
				Row row = sheet.getRow(rowNum);
				int lastCell = row.getLastCellNum();
				for(int cellNum=0;cellNum<lastCell;cellNum++){
					Cell cell = row.getCell(cellNum);
					String cellvalue = getValue(cell);
					result += cellvalue + "\t";
				}
				result += "\n";
			}
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getValue(Cell cell) {
		int cellType = cell.getCellType();
		String value = "";
		switch (cellType) {   
		  
	    case Cell.CELL_TYPE_FORMULA:  
	    	try {
	    		value = ""+cell.getNumericCellValue();
			} catch (Exception e) {
				value = ""+cell.getStringCellValue();
			}
	        break;   
	
	    case Cell.CELL_TYPE_NUMERIC:   
	        if(HSSFDateUtil.isCellDateFormatted(cell)){   
	        	Date dateValue = cell.getDateCellValue();
	            value = ""  
	                + DateUtil.convertDateToString(dateValue);   
	        }else{
	        	double numValue = cell.getNumericCellValue();
	        	BigDecimal decimalValue = new BigDecimal(numValue);
	        	decimalValue = decimalValue.setScale(2, BigDecimal.ROUND_HALF_UP);
	            value = decimalValue.toString();   
	        }   
	           
	        break;
	
	    case Cell.CELL_TYPE_STRING:   
	        value = ""  
	                + cell.getStringCellValue();   
	        break;   
	           
	    case Cell.CELL_TYPE_BOOLEAN:   
	        value = ""  
	                + cell.getBooleanCellValue();   
	           
	        break;   
	
	    default:   
	    }   
		return value;
	}
	
	/**
     * @describe 依据某个字段对集合进行排序
     * @author ...
     * @date 
     * @param list
     *            待排序的集合
     * @param fieldName
     *            依据这个字段进行排序
     * @param asc
     *            如果为true，是正序；为false，为倒序
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(List<T> list, String fieldName, boolean asc) {
        Comparator<?> mycmp = ComparableComparator.getInstance();
        mycmp = ComparatorUtils.nullLowComparator(mycmp); // 允许null
        if (!asc) {
            mycmp = ComparatorUtils.reversedComparator(mycmp); // 逆序
        }
        Collections.sort(list, new BeanComparator(fieldName, mycmp));
    }
    
}
