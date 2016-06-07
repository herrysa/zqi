package com.zqi.primaryData.fileDataBase;

import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;


public interface IFileDataBase {

	String basePath = Tools.getResource("baseDir");
	
	public String readStr(String fileName);
	public void writeStr(String fileName,String content);
	
	public List<Map<String, Object>> readList(String fileName);
	public void writeList(String fileName,String content);
	
	public String getFilePath(String fileName);
}
