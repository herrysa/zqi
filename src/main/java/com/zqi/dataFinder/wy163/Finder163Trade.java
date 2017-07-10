package com.zqi.dataFinder.wy163;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.zqi.dataFinder.IFinderTrade;
import com.zqi.frame.util.Tools;
import com.zqi.frame.util.ZipCompress;
import com.zqi.primaryData.fileDataBase.FileDataBase;
import com.zqi.unit.FileUtil;

public class Finder163Trade implements IFinderTrade{

	private Map<String, Object> gp ;
	private String year ;
	private String month ;
	private String date ;
	private String dateFull ;
	
	public Finder163Trade(Map<String, Object> gp , String year , String month , String date){
		this.gp = gp;
		this.year = year;
		this.month = month;
		this.date = date;
		this.dateFull = year+month+date;
	}
	
	@Override
	public void findTrade() {
		String code = gp.get("code").toString();
		String type = gp.get("type").toString();
		String url = "http://quotes.money.163.com/cjmx/"+year+"/"+dateFull+"/"+type+code+".xls";
		String datePath = "tradeData/"+year+"/"+code+"/"+month+"/"+date;
		FileDataBase fileDataBase = new FileDataBase(datePath);
		fileDataBase.deleteDataBase();
		String result = Tools.getExcelByHttpUrl(url);
		String[] resultArr = result.split("\t\n");
		String minuteTemp = null;
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
		datePath = fileDataBase.getFilePath(datePath);
		try {
			ZipCompress.writeByApacheZipOutputStream(datePath, datePath+".zip", "");
			FileUtil.delAllFile(datePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		System.out.println(code);
	}

}
