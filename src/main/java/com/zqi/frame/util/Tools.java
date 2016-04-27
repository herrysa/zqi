package com.zqi.frame.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Tools {

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
	   
	   @SuppressWarnings("unchecked")
		public static List<Map<String,String>> getHttpUrlMap(String[] keys,String url,Map<String, String> titleMap){
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