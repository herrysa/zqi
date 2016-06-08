package com.zqi.primaryData.fileDataBase;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

public class FileDataBase {

	protected String basePath = Tools.getResource("baseDir");
	protected String databaseName = null;
	
	public FileDataBase(String databaseName){
		this.databaseName = databaseName;
	}
	
	public String readStr(String fileName) {
		String filePath = getFilePath(fileName);
		String content = FileUtil.readFile(filePath);
		return content;
	}
	
	public String readStr(File file) {
		String content = FileUtil.readFile(file);
		return content;
	}
	
	public void writeStr(String fileName, String content) {
		String filePath = getFilePath(fileName);
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		FileUtil.writeFile(content, filePath);
	}
	
	public String getFilePath(String fileName){
		String filePath = basePath+databaseName+"/"+fileName+".txt";
		if(fileName==null){
			filePath = basePath+databaseName;
		}
		return filePath;
	}
	
	public void deleteDataBase(){
		String databasePath = getFilePath(null);
		FileUtil.delFolder(databasePath);
	}
	
	public File[] getFiles(){
		String databasePath = getFilePath(null);
		File dbDir = new File(databasePath);
		File[] tableFiles = dbDir.listFiles();
		return tableFiles;
	}
	
	public String getCol(String[] column){
		String colStr = "";
		for(String col : column){
			colStr += col+",";
		}
		if(!"".equals(colStr)){
			colStr = colStr.substring(0,colStr.length()-1);
		}
		return colStr;
	}
}
