package com.zqi.primaryData;

import java.util.List;
import java.util.Map;

import com.zqi.dataFinder.wy163.Finder163RHis;
import com.zqi.primaryData.fileDataBase.RHisFileDataBase;

public class HisDataFindThread implements Runnable{

	Map<String, Object> gp;
	String dateFrom;
	String dateTo;
	HisContext hisContext;
	String[] colArr;
	
	public HisDataFindThread(Map<String, Object> gp,HisContext hisContext){
		this.gp = gp;
		this.dateFrom = hisContext.getDateFrom();
		this.dateTo = hisContext.getDateTo();
		this.hisContext = hisContext;
		this.colArr = hisContext.getColArr();
	}
	
	@Override
	public void run() {
		Finder163RHis finder163rHis = new Finder163RHis(hisContext);
		StringBuffer insertbBuffer = new StringBuffer();
		List<Map<String,Object>> dataListTemp = finder163rHis.findRHis(gp, dateFrom, dateTo);
		for(Map<String, Object> data : dataListTemp){
			String dataLine = getInsert(data);
			insertbBuffer.append(dataLine);
		}
		RHisFileDataBase fdb = new RHisFileDataBase(hisContext.getYear());
		fdb.writeStr(gp.get("code").toString(), insertbBuffer.toString());
		/*String basePath = Tools.getResource("baseDir");
		String rHisDataDir = Tools.getResource("rhisDir");
		FileUtil.writeFile(insertbBuffer.toString(), basePath+rHisDataDir+daytable+".txt");
		hisContext.getRecordMap().put(daytable, count);*/
	}

	private String getInsert(Map<String,Object> dataMap){
		String dataLine = "";
		for(String col : colArr){
			dataLine += dataMap.get(col)+"\t";
		}
		return dataLine+"\n";
	}
}
