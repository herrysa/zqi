package com.zqi.primaryData;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.dataFinder.IFinderBk;
import com.zqi.dataFinder.sina.FinderSinaBk;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;
import com.zqi.primaryData.fileDataBase.FileDataBase;
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
		final String url = "http://quotes.money.163.com/cjmx/"+year+"/"+period+"/%code%.xls";
		List<Map<String, Object>>  gpList = findAGpDicList("0,1");
		final FileDataBase fileDataBase = new FileDataBase("tradeData/"+year+"/"+period);
		fileDataBase.deleteDataBase();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10); 
		for(final Map<String, Object> gp : gpList){
			Thread findTradeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String code = gp.get("code").toString();
					String type = gp.get("type").toString();
					String urlTemp = url.replace("%code%", type+code);
					String result = Tools.getExcelByHttpUrl(urlTemp);
					fileDataBase.writeStr(code, result, 0);  
					System.out.println(code);
				}
			});
			fixedThreadPool.execute(findTradeThread);
		}
		fixedThreadPool.shutdown();
		try {
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setMessage("下载明细数据成功！");
		return resultMap;
	}
	
}
