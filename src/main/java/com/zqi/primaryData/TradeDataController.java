package com.zqi.primaryData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.Tools;
import com.zqi.frame.util.ZipCompress;
import com.zqi.primaryData.fileDataBase.FileDataBase;

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
		String getYear = request.getParameter("year");
		String temp = request.getParameter("temp");
		TestTimer tt = new TestTimer(period);
		tt.begin();
		String[] periodArr = period.split("-");
		final String year = periodArr[0];
		final String month = periodArr[1];
		final String date = periodArr[2];
		final String dateFull = periodArr[0]+periodArr[1]+periodArr[2];
		final String url = "http://quotes.money.163.com/cjmx/"+year+"/"+dateFull+"/%code%.xls";
		List<Map<String, Object>>  gpList = findAGpDicList("0,1");
		//final FileDataBase fileDataBase = new FileDataBase("tradeData/"+year+"/"+dateFull);
		//fileDataBase.deleteDataBase();
		
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(15); 
		for(final Map<String, Object> gp : gpList){
			Thread findTradeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String code = gp.get("code").toString();
					String type = gp.get("type").toString();
					FileDataBase fileDataBase = new FileDataBase("tradeData/"+year+"/"+code+"/"+month+"/"+date);
					fileDataBase.deleteDataBase();
					String urlTemp = url.replace("%code%", type+code);
					String result = Tools.getExcelByHttpUrl(urlTemp);
					String[] resultArr = result.split("\t\n");
					String minuteTemp = null;
					//String content = "";
					//String filePath = null;
					StringBuilder stringBuilder = new StringBuilder();
					for(int i=1;i<resultArr.length;i++){
						String row = resultArr[i];
						String[] rowArr = row.split("\t");
						String time = rowArr[0];
						String[] timeArr = time.split(":");
						String hour = timeArr[0];
						String minute = timeArr[1];
						String second = timeArr[2];
						if(minuteTemp==null||minuteTemp.equals(minute)){
							minuteTemp = minute;
						}else{
							fileDataBase.writeWithNIO(hour+"/"+minute, stringBuilder.toString(), 0);
							stringBuilder.setLength(0);
							minuteTemp = minute;
						}
						stringBuilder.append(second+"\t");
						stringBuilder.append(rowArr[1]+"\t");
						stringBuilder.append(rowArr[2]+"\t");
						stringBuilder.append(rowArr[3]+"\t");
						String selltype = rowArr[5];
						if("买盘".equals(selltype)){
							stringBuilder.append(1+"\t\n");
						}else if("卖盘".equals(selltype)){
							stringBuilder.append(-1+"\t\n");
						}else{
							stringBuilder.append(0+"\t\n");
						}
					}
					String datePath = fileDataBase.getFilePath("tradeData/"+year+"/"+code+"/"+month+"/"+date);
					try {
						ZipCompress.writeByApacheZipOutputStream(datePath, datePath+".zip", "");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}  
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
		tt.done();
		return resultMap;
	}
	
}
