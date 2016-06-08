package com.zqi.primaryData.fileDataBase;

import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;

public class RHisFileDataBase extends FileDataBase implements IFileDataBase{

	private String key = "period" ;
	private String[] rDayColumn = {"period","code","name","type","settlement","open","high","low","close","volume","amount","changeprice","changepercent"};
	private String[] rDayColumnType = {"String","String","String","String","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal"};
	private static String rHisDataDir = Tools.getResource("rhisDir");
	String year ;
	public RHisFileDataBase(String year){
		super(rHisDataDir+year);
		this.year = year;
	}
	@Override
	public List<Map<String, Object>> readList(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> readByKey(String fileName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLoadFileSql(String fileName){
		String filePath = getFilePath(fileName);
		String dataCol = getCol(rDayColumn);
		String loadDataSql = "load data infile '"+filePath+"' into table "+fileName+"("+dataCol+");";
		return loadDataSql;
	}
}
