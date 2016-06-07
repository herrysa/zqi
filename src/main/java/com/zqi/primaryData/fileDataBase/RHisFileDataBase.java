package com.zqi.primaryData.fileDataBase;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

public class RHisFileDataBase implements IFileDataBase{

	public static String[] rDayColumn = {"period","code","name","settlement","open","high","low","close","volume","amount","changeprice","changepercent","swing","turnoverrate","fiveminute","lb","wb","tcap","mcap","pe","mfsum","mfratio2","mfratio10"};
	public static String[] rDayColumnType = {"String","String","String","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal","decimal"};
	String rHisDataDir = Tools.getResource("rhisDir");
	String year ;
	public RHisFileDataBase(String year){
		if(year==null||"".equals(year)){
			this.year = "temp";
		}else{
			this.year = year;
		}
	}
	@Override
	public String readStr(String fileName) {
		String filePath = getFilePath(fileName);
		String content = FileUtil.readFile(filePath);
		return content;
	}

	@Override
	public void writeStr(String fileName, String content) {
		String filePath = getFilePath(fileName);
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		FileUtil.writeFile(content, filePath);
	}
	@Override
	public String getFilePath(String fileName){
		String filePath = basePath+rHisDataDir+year+"/"+fileName+".txt";
		return filePath;
	}
	@Override
	public List<Map<String, Object>> readList(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void writeList(String fileName, String content) {
		// TODO Auto-generated method stub
		
	}

}
