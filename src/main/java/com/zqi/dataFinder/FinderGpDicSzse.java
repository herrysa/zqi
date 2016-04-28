package com.zqi.dataFinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;
import com.zqi.unit.DateUtil;

public class FinderGpDicSzse implements IFinderGpDic{
	
	public String[] gpDicColumnSzse = {"0","5","1","6","7","8","9","endDate"};
	public int codeIndex = 0;
	public int nameIndex = 1;
	@Override
	public List<Map<String, Object>> findGpDic() {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		String result = getSzseGpList();
		if(result==null||"".equals(result)){
			System.out.println("---------------getSzseGpList error---------------");
		}else{
			String period = DateUtil.getDateNow();
			String[] rowArr = result.split("\t\n");
			for(String row : rowArr){
				String codeValue = "",daytable = "daytable",nameValue = "";
				Map<String, Object> data = new HashMap<String, Object>();
				String[] cellArr = row.split("\t");
				boolean addFlag = true;
				for(int k=0;k<gpDicColumn.length;k++){
					String key = gpDicColumn[k];
					if(key.equals("pinyinCode")){
						data.put(key,Tools.getPYIndexStr(nameValue, true));
					}else if(key.equals("daytable")){
						data.put(key,daytable);
					}else if(key.equals("remark")){
						data.put(key,"");
					}else{
						String datakey = gpDicColumnSzse[k];
						if("endDate".equals(datakey)){
							data.put(key, period);
						}else{
							int vIndex = Integer.parseInt(datakey);
							String v = cellArr[vIndex];
							if(key.equals(name)){
								nameValue = v;
							}else if(key.equals(code)){
								codeValue = v;
								if(codeValue.equals("")){
									addFlag =false;
									break;
								}
								//System.out.println(codeValue);
								daytable += codeValue.substring(0,1)+"_";
								Long codeNum = Long.parseLong(codeValue.substring(1));
								daytable += ""+(codeNum/50+1);
							}
							data.put(key, v);
						}
					}
				}
				if(addFlag){
					dataList.add(data);
				}
			}
			System.out.println("---------------深交所A:"+dataList.size()+"---------------");
		}
		return dataList;
	}
	
	private static String getSzseGpList(){
		String result = "";
		BufferedReader in = null;
		try {
			Calendar calendar = Calendar.getInstance();
			Long longTime = calendar.getTimeInMillis();
			long random = new Double(Math.floor(Math.random()*(100000+1))).longValue();
			String url = "http://www.szse.cn/szseWeb/ShowReport.szse?SHOWTYPE=EXCEL&CATALOGID=1110&tab1PAGENUM=6&tab1PAGECOUNT=177&tab1RECORDCOUNT=1765&ENCODE=1&TABKEY=tab1";
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			//connection.setRequestProperty("Host","query.sse.com.cn");
			//connection.setRequestProperty("Referer"," http://www.sse.com.cn/assortment/stock/list/share/");
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
			/*in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(),"gbk"));
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}*/
			/*Workbook workbook = null;
			workbook = new HSSFWorkbook(connection.getInputStream()); 
			Sheet sheet = workbook.getSheetAt(0);
			int begin = sheet.getFirstRowNum(); 
			int end = sheet.getLastRowNum(); 
			for (int i = begin; i <= end; i++) {  
				Row row = sheet.getRow(i);
		        if (null == sheet.getRow(i)) {  
		          continue;  
		        }
		        int cellBegin = row.getFirstCellNum();
		        int cellEnd = row.getFirstCellNum();
		        for (int j = cellBegin; j <= cellEnd; j++) {  
		        	Cell cell = row.getCell(j);
		        	System.out.println(cell.getStringCellValue());
		        }
		      } */
			in = new BufferedReader(new InputStreamReader(
			connection.getInputStream(),"gbk"));
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				result += line;
			}
			result = result.replaceAll("<tr  class='cls-data-tr' bgcolor='#FFFFFF'>", "\t\n");
			result = result.replaceAll("<tr  class='cls-data-tr' bgcolor='#F8F8F8'>", "\t\n");
			result = result.replaceAll("</tr>", "");
			result = result.replaceAll("<td  class='cls-data-td'  align='center' >", "\t");
			result = result.replaceAll("<td  class='cls-data-td'  align='left' >", "\t");
			result = result.replaceAll("<td  class='cls-data-td' style='mso-number-format:\\\\@' align='center' >", "");
			result = result.replaceAll("</td>", "");
			result = result.substring(result.lastIndexOf("公司网址")+6);
			//System.out.println(result);
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getDateTimeNow());
		FinderGpDicSzse finderGpDicSzse = new FinderGpDicSzse();
		finderGpDicSzse.findGpDic();
		System.out.println(DateUtil.getDateTimeNow());
	}
}
