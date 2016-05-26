package com.zqi.dataFinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import com.zqi.PrimaryData.HisContext;
import com.zqi.frame.util.Tools;

public class GetHttpThread implements Runnable{
	
	String url;
	Map<String, Object> context;
	HisContext hisContext;
	
	public GetHttpThread(Map<String, Object> context){
		this.url = context.get("url").toString();
		this.context = context;
		this.hisContext = (HisContext)context.get("hisContext");
	}

	@Override
	public void run() {
		String result = "";
		String code = context.get("code").toString();
		String countObj = context.get("count").toString();
		int count = Integer.parseInt(countObj);
		count++;
		context.put("count",count);
		if(context.get("result")==null){
			result = getByHttpUrl(url,code);
			context.put("result", result);
		}else{
			ScheduledExecutorService schedule = (ScheduledExecutorService)context.get("schedule");
			schedule.shutdownNow();
			Map<String,Map<String, String>> log = hisContext.getLog();
			Map<String, String> codeLog = log.get(code);
			if(codeLog==null){
				codeLog = new HashMap<String, String>();
				log.put(code, codeLog);
			}
			
			codeLog.put("count", countObj);
		}
	}

	public String getByHttpUrl(String url,String code){
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
				connection.setConnectTimeout(1000);
				connection.setReadTimeout(1000);
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
				Map<String,Map<String, String>> log = hisContext.getLog();
				Map<String, String> codeLog = log.get(code);
				if(codeLog==null){
					codeLog = new HashMap<String, String>();
					log.put(code, codeLog);
				}
				String timeoutcount = codeLog.get("timeoutcount");
				int c = 1;
				if(timeoutcount!=null){
					c = Integer.parseInt(timeoutcount)+1;
				}
				codeLog.put("timeoutcount", ""+c);
				System.out.println("发送GET请求出现异常！" + e.getMessage());
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
}
