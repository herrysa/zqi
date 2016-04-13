package com.zqi.hq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HQFuc {


	public static void main(String[] args) {
		 String result = "";
         BufferedReader in = null;

     try {
         String url = "http://hq.sinajs.cn/list=sh600094";
         String urlNameString = url;
         URL realUrl = new URL(urlNameString);
         // 打开和URL之间的连接
         URLConnection connection = realUrl.openConnection();
         // 设置通用的请求属性
         connection.setRequestProperty("accept", "*/*");
         connection.setRequestProperty("connection", "Keep-Alive");
         connection.setRequestProperty("user-agent",
                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
     String[] rsArr = result.split("=")[1].split(",");
     System.out.println(result);
     System.out.println("股票:"+rsArr[0]+" "+rsArr[30]+" "+rsArr[31]); 
     System.out.println("今日开盘:"+rsArr[1]); 
     System.out.println("昨日收盘:"+rsArr[2]); 
     System.out.println("当前价格:"+rsArr[3]); 
     System.out.println("今日最高:"+rsArr[4]); 
     System.out.println("今日最低:"+rsArr[5]); 
     System.out.println("买一:"+rsArr[6]); 
     System.out.println("卖一:"+rsArr[7]); 
     System.out.println("成交量:"+rsArr[8]); 
     System.out.println("成交额:"+rsArr[9]); 
     System.out.println("卖五:"+rsArr[29]+" "+rsArr[28]); 
     System.out.println("卖四:"+rsArr[27]+" "+rsArr[26]); 
     System.out.println("卖三:"+rsArr[25]+" "+rsArr[24]); 
     System.out.println("卖二:"+rsArr[23]+" "+rsArr[22]); 
     System.out.println("卖一:"+rsArr[21]+" "+rsArr[20]); 
     System.out.println("-----------------------------");
     System.out.println("买一:"+rsArr[11]+" "+rsArr[10]); 
     System.out.println("买二:"+rsArr[13]+" "+rsArr[12]); 
     System.out.println("买三:"+rsArr[15]+" "+rsArr[14]); 
     System.out.println("买四:"+rsArr[17]+" "+rsArr[16]); 
     System.out.println("买五:"+rsArr[19]+" "+rsArr[18]); 
	}
}
