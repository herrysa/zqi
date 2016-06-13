package com.zqi.primaryData.fileDataBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

public class FileDataBase {

	protected String basePath = Tools.getResource("baseDir");
	protected String databaseName = null;
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public FileDataBase(String databaseName){
		this.databaseName = databaseName;
	}
	
	public String readStr(String fileName) {
		String filePath = getFilePath(fileName);
		String content = readFile(filePath);
		return content;
	}
	
	public String readStr(File file) {
		String content = FileUtil.readFile(file);
		return content;
	}
	
	public void writeStr(String fileName, String content,int type) {
		String filePath = getFilePath(fileName);
		File file = new File(filePath);
		if(type==0&&file.exists()){
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
	
	public static String readFile( String filePath ) {
        File ds = null;
        FileInputStream fis = null;
        InputStreamReader isr=null;
        BufferedReader br = null;
        String fileContent = "";
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                fileContent = "";
                fis = new FileInputStream( ds );
                isr = new InputStreamReader(fis,"UTF-8");
                br = new BufferedReader( isr );
                temp = br.readLine();
                while ( temp != null ) {
                    fileContent += temp+"\n";
                    temp = br.readLine();
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( br != null ) {
                    br.close();
                }
                if ( fis != null ) {
                	fis.close();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        return fileContent;
    }
	
	public static String readFile( String filePath ,String periodFrom , String periodTo) {
        File ds = null;
        FileInputStream fis = null;
        InputStreamReader isr=null;
        BufferedReader br = null;
        String fileContent = "";
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                fileContent = "";
                fis = new FileInputStream( ds );
                isr = new InputStreamReader(fis,"UTF-8");
                br = new BufferedReader( isr );
                temp = br.readLine();
                while ( temp != null ) {
                	String period = temp.split("\t")[0];
                	if(period.compareTo(periodFrom)>=0&&period.compareTo(periodTo)<0){
                		fileContent += temp+"\n";
                	}
                    temp = br.readLine();
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( br != null ) {
                    br.close();
                }
                if ( fis != null ) {
                	fis.close();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        return fileContent;
    }
}
