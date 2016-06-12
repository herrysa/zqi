package com.zqi.primaryData;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.dataFinder.IFinderBk;
import com.zqi.dataFinder.sina.FinderSinaBk;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.Tools;
import com.zqi.unit.DateUtil;
import com.zqi.unit.FileUtil;

@Controller
@RequestMapping("/tradeData")
public class TradeDataController  extends BaseController{

	@RequestMapping("/tradeDataList")
	public String primaryDataList(){
		return "primaryData/tradeDataList";
	}
	
	@ResponseBody
	@RequestMapping("/tradeDataGridList")
	public Map<String, Object> bkDataGridList(HttpServletRequest request){
		String findSql = "select * from d_gpbk where 1=1";
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, findSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/downLoadTradeData")
	public Map<String, Object> downLoadTradeData(HttpServletRequest request){
		String period = request.getParameter("period");
		String year = period.split("-")[0];
		period = period.replace("-", "");
		String url = "http://quotes.money.163.com/cjmx/%year%/%period%/1002340.xls";
		IFinderBk iFinderBk = new FinderSinaBk();
		String bkData = iFinderBk.findBkInfoStr();
		String basePath = Tools.getResource("baseDir");
		String dicPath = basePath+Tools.getResource("dicDir");
		String filePath = dicPath+"d_gpbk.txt";
		File bkFile = new File(filePath);
		bkFile.deleteOnExit();
		FileUtil.writeFile(bkData, filePath);
		String dataCol = "code,name,bkType,bkName";
		String loadDataSql = "load data infile '"+filePath+"' into table d_gpbk("+dataCol+");";
		String deleteBkInfoSql = "delete from d_gpbk";
		zqiDao.excute(deleteBkInfoSql);
		zqiDao.excute(loadDataSql);
		setMessage("更新板块成功！");
		return resultMap;
	}
	
	public String getExcelByHttpUrl(String url){
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
				HSSFWorkbook wb = new HSSFWorkbook(new BufferedInputStream(connection.getInputStream()));
				File file = new File("D:/zqi/aaa.xls");
				wb.write(new FileOutputStream(file));
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
}
