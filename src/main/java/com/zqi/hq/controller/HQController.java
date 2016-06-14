package com.zqi.hq.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;

@Controller
@RequestMapping("/hq")
public class HQController extends BaseController{

	@RequestMapping("/findHQ")
	public String findHQ(){
		
		return "hq/hqList";
	}
	
	@ResponseBody
	@RequestMapping("/findHQGridList")
	public Map<String, Object> findHQGridList(){
		TestTimer tt = new TestTimer("11");
		tt.begin();
		List<Map<String, Object>> gpDicList = findAGpDicList(null);
		List<Map<String, Object>> dayHqData = new ArrayList<Map<String,Object>>();
		String url = "http://hq.sinajs.cn/";
		String listStr = "";
		int i = 0;
		for(Map<String, Object> gp : gpDicList){
			if(i==500){
				String result = Tools.getByHttpUrl(url+"list="+listStr);
				listStr = "";
				String[] resultArr = result.split(";");
				String[] rsArr = result.split("=")[1].split(",");
				if(rsArr.length>1){
					Map<String, Object> hqMap = new HashMap<String, Object>();
					//hqMap.put("period", period);
					//hqMap.put("code", code.toString());
					hqMap.put("name", rsArr[0].substring(1));
					hqMap.put("open", rsArr[1]);
					hqMap.put("close", rsArr[3]);
					hqMap.put("high", rsArr[4]);
					hqMap.put("low", rsArr[5]);
					hqMap.put("volume", rsArr[8]);
					hqMap.put("turnover", rsArr[9]);
					String yesterday = rsArr[2];
					String now = rsArr[3];
					dayHqData.add(hqMap);
				}

			}
			listStr += gp.get("symbol").toString()+",";
			i++;
		}
		tt.done();
		//this.resultMap.put("page_data", hqList);
		//this.resultMap.put("total_rows", hqList.size());
		return this.resultMap;
	}
//	@ResponseBody
//	@RequestMapping("/hqRk")
//	public String hqRk(){
//		List<Map<String, Object>> dayHqData = getRealHQ();
//		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(zqiDao.getJdbcTemplate());
//		simpleJdbcInsert.setTableName("");
//		Map<String, Object>[] dayHqArr = dayHqData.toArray(new HashMap[dayHqData.size()]);
//		simpleJdbcInsert.executeBatch(dayHqArr);
//		return "";
//	}
	
	@ResponseBody
	@RequestMapping("/hqRk")
	public String hqRk(){
		List<Map<String, Object>> dayHqData = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> gpDicList = zqiDao.findAll("select * from d_gpDic order by code");
		int i = 0;
		String dayTableStr = "";
		SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate=new Date();
		String period = myFmt2.format(nowDate);
		for(Map<String, Object> gpDic: gpDicList){
			Object code = gpDic.get("code");
			Object dayTable = gpDic.get("daytable");
			if(dayTable!=null){
				String newDayTableStr = dayTable.toString();
				if(!"".equals(dayTableStr)&&!newDayTableStr.equals(dayTableStr)){
					//dayTableStr
					//System.out.println(i);
					String deleteSql = "delete from "+dayTableStr+" where period='"+period+"'";
					zqiDao.getJdbcTemplate().execute(deleteSql);
					SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(zqiDao.getJdbcTemplate());
					simpleJdbcInsert.setTableName(dayTableStr);
					Map<String, Object>[] dayHqArr = dayHqData.toArray(new HashMap[dayHqData.size()]);
					simpleJdbcInsert.executeBatch(dayHqArr);
					dayHqData.clear();
				}
				dayTableStr = newDayTableStr;
			}
			String result = "";
			BufferedReader in = null;
			try {
				String codeStr = code.toString();
				codeStr = codeStr.toLowerCase();
				String url = "http://hq.sinajs.cn/list="+codeStr;
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
		//	System.out.println(result);
        String[] rsArr = result.split("=")[1].split(",");
        if(rsArr.length<=1){
        	continue;
        }
        Map<String, Object> hqMap = new HashMap<String, Object>();
        hqMap.put("period", period);
        hqMap.put("code", code.toString());
        hqMap.put("name", rsArr[0].substring(1));
        hqMap.put("open", rsArr[1]);
        hqMap.put("close", rsArr[3]);
        hqMap.put("high", rsArr[4]);
        hqMap.put("low", rsArr[5]);
        hqMap.put("volume", rsArr[8]);
        hqMap.put("turnover", rsArr[9]);
        String yesterday = rsArr[2];
        String now = rsArr[3];
        if(yesterday!=null&&!yesterday.equals("")&&now!=null&&!now.equals("")){
        	BigDecimal y = new BigDecimal(yesterday);
        	BigDecimal limitPrice = y.multiply(new BigDecimal(0.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
        	BigDecimal limitUpPrice = y.add(limitPrice);
        	BigDecimal limitDownPrice = y.subtract(limitPrice);
        	BigDecimal n = new BigDecimal(now);
        	BigDecimal increasePrice = n.subtract(y).setScale(2, BigDecimal.ROUND_HALF_UP);
        	BigDecimal increasePercent = n.subtract(y).divide(y,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        	hqMap.put("increasePercent", increasePercent.toString());
        	
        	//boolean limitUp =false,limitDown =false;
        	int limitUp = n.compareTo(limitUpPrice);
        	int limitDown = n.compareTo(limitDownPrice);
        	

        }
        dayHqData.add(hqMap);
        /*System.out.println("股票:"+rsArr[0]+" "+rsArr[30]+" "+rsArr[31]); 
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
        System.out.println("买五:"+rsArr[19]+" "+rsArr[18]); */
        i++;
        System.out.println(code);
		}
		String deleteSql = "delete from "+dayTableStr+" where period='"+period+"'";
		zqiDao.getJdbcTemplate().execute(deleteSql);
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(zqiDao.getJdbcTemplate());
		simpleJdbcInsert.setTableName(dayTableStr);
		Map<String, Object>[] dayHqArr = dayHqData.toArray(new HashMap[dayHqData.size()]);
		simpleJdbcInsert.executeBatch(dayHqArr);
		/*if(!"".equals(dayTableStr)){
			dayTableStr = dayTableStr.replace("daytable", "");
			int daytableIndex = Integer.parseInt(dayTableStr);
			List<String> deleteSqlList = new ArrayList<String>();
			for(int t=1;t<=daytableIndex;t++){
				String deleteSql = "delete from daytable"+t+" where period='"+period+"'";
				deleteSqlList.add(deleteSql);
			}
			String[] delSqls = deleteSqlList.toArray(new String[deleteSqlList.size()]);
			zqiDao.bathUpdate(delSqls);
		}*/
		
		return "rrr";
	}
}
