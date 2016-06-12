package com.zqi.primaryData.fileDataBase;

import java.util.List;
import java.util.Map;


public interface IFileDataBase {

	public String readStr(String fileName);
	public void writeStr(String fileName,String content,int type);
	
	public List<Map<String, Object>> readList(String fileName);
	//public void writeList(String fileName,List<Map<String, Object>> dtaList);
	
	public Map<String, Object> readByKey(String fileName,String key);
	
	public String getFilePath(String fileName);
	
	public void deleteDataBase();
}
