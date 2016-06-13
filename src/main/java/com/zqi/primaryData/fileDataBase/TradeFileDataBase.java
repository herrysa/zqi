package com.zqi.primaryData.fileDataBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zqi.frame.util.Tools;

public class TradeFileDataBase extends FileDataBase implements IFileDataBase{

	private static String tradeDataDir = Tools.getResource("tradeDir");
	private static String year;
	private static String date;
	
	public TradeFileDataBase(String period) {
		super(tradeDataDir+year);
		year = period.split("-")[0];
		this.date = period.replace("-", "");
		this.setDatabaseName(tradeDataDir+year+"/"+date);
	}

	@Override
	public List<Map<String, Object>> readList(String fileName) {
		File ds = null;
        FileInputStream fis = null;
        InputStreamReader isr=null;
        BufferedReader br = null;
        String temp = "";
        List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
        try {
        	String filePath = getFilePath(fileName);
            ds = new File( filePath );
            if ( ds.exists() ) {
                fis = new FileInputStream( ds );
                isr = new InputStreamReader(fis,"UTF-8");
                br = new BufferedReader( isr );
                temp = br.readLine();
                while ( temp != null ) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    String[] tradeRow = temp.split("\t");
                    data.put("datetime", tradeRow[0]);
                    data.put("settlement", tradeRow[1]);
                    data.put("changeprice", tradeRow[2]);
                    data.put("volume", tradeRow[3]);
                    data.put("amount", tradeRow[4]);
                    data.put("type", tradeRow[5]);
                    dataList.add(data);
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
		return dataList;
	}

	@Override
	public Map<String, Object> readByKey(String fileName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
