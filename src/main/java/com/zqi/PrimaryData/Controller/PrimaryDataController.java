package com.zqi.PrimaryData.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.PrimaryData.dao.IPrimaryDataDao;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController{

	private IPrimaryDataDao iPrimaryDataDao;
	
	public IPrimaryDataDao getiPrimaryDataDao() {
		return iPrimaryDataDao;
	}

	@Autowired
	public void setiPrimaryDataDao(IPrimaryDataDao iPrimaryDataDao) {
		this.iPrimaryDataDao = iPrimaryDataDao;
	}


	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> primaryDataGridList(HttpServletRequest request,String gpCode){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		String[] columns = request.getParameterValues("columns");
		Object q = request.getAttribute("columns");
		Map columns1 = request.getParameterMap();
		Set<Entry<String, Object>> pp = columns1.entrySet();
		Set<String> keys = columns1.keySet();
		for(Entry<String, Object> p : pp){
			Object v= p.getValue();
			if(v instanceof String){
				System.out.println(p.getKey()+":"+p.getValue());
			}else{
				String[] vArr = (String[])v;
				System.out.println(p.getKey()+":"+vArr[0]);
			}
		}
		Map<String, Object> r = new HashMap<String, Object>();
		if(code!=null&&!"".equals(code)){
			String findDayTableSql = "select daytable from d_gpDic where code='"+code+"'";
			String tableName = "";
			Map<String, Object> rs0 = iPrimaryDataDao.findFirst(findDayTableSql);
			if(!rs0.isEmpty()){
				tableName = rs0.get("daytable").toString();
			}
			String dayDataSql = "select * from "+tableName+" where code='"+code+"'";
			if(period!=null&&!"".equals(period)){
				dayDataSql += " and period='"+period+"'";
			}
			List<Map<String, Object>> dayData = iPrimaryDataDao.findAll(dayDataSql);
			/*List<Map<String, String>> result = new ArrayList<Map<String,String>>();
			Map<String, String> row = new HashMap<String, String>();
			row.put("customer_id", "1");
			row.put("lastname", "1");
			row.put("firstname", "1");
			row.put("email", "1");
			result.add(row);*/
			r.put("page_data", dayData);
			r.put("total_rows", dayData.size());
		}
		return r;
	}
	
	@RequestMapping("/primaryDataList")
	public String primaryDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "primaryData/primaryDataList";
	}
	
	public String findBlockInfo(){
		
		return "";
	}
	
	public static void main(String[] args) {
		//while(true){
            String result = "";
            BufferedReader in = null;

        try {
            String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bknode%22,%22gainianbankuai%22,%22%22,0]]&callback=FDC_DC.theTableData";
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
        result = result.substring(result.indexOf("theTableData")+13, result.length()-1);
        JSONArray c = JSONArray.fromObject(result);
        JSONObject cc = (JSONObject) c.get(0);
        JSONArray ccc = (JSONArray) cc.get("items");
        Iterator<JSONArray> cIt = ccc.iterator();
        while (cIt.hasNext()) {
        	JSONArray ct = cIt.next();
        	Iterator<JSONArray> ccIt = ct.iterator();
        	int i=1;
        	while (ccIt.hasNext()) {
        		Object oo = ccIt.next();
        		if(i==2){
        			System.out.println("----------------"+oo.toString());
        			String result2 = "";
                    BufferedReader in2 = null;

                try {
                    String url = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php/?__s=[[%22bkshy_node%22,%22"+oo.toString()+"%22,%22%22,0,1,40]]&callback=FDC_DC.theTableData";
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
                    in2 = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(),"GBK"));
                    String line;
                    while ((line = in2.readLine()) != null) {
                        result2 += line;
                    }
                } catch (Exception e) {
                    System.out.println("发送GET请求出现异常！" + e);
                    e.printStackTrace();
                }
                // 使用finally块来关闭输入流
                finally {
                    try {
                        if (in2 != null) {
                            in2.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                result2 = result2.substring(result2.indexOf("theTableData")+13, result2.length()-1);
                JSONArray c2 = JSONArray.fromObject(result2);
                JSONObject cc2 = (JSONObject) c2.get(0);
                JSONArray ccc2 = (JSONArray) cc2.get("items");
                Iterator<JSONArray> cIt2 = ccc2.iterator();
                while (cIt2.hasNext()) {
                	JSONArray ct2 = cIt2.next();
                	Iterator<JSONArray> ccIt2 = ct2.iterator();
                	int i2=1;
                	while (ccIt2.hasNext()) {
                		Object oo2 = ccIt2.next();
                		if(i2==3){
                			System.out.println(oo2.toString());
                		}
                		i2++;
                	}
                }
                //System.out.println(result2);
        		}
        		//System.out.println(oo.toString());
        		i++;
			}
		}
        //System.out.println(result);
        //String[] rsArr = result.split("=")[1].split(",");
        //}
	}
	
}
