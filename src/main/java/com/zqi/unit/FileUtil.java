package com.zqi.unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtil {

	/**
     * @description	读文件方法
     * @author  zzh
     * @param filePath
     * @return
     */

    public static String readFile( String filePath ) {
        File ds = null;
        FileReader fr = null;
        BufferedReader br = null;
        String fileContent = "";
        String temp = "";
        try {
            ds = new File( filePath );
            if ( ds.exists() ) {
                fileContent = "";
                fr = new FileReader( ds );
                br = new BufferedReader( fr );
                temp = br.readLine();
                while ( temp != null ) {
                    fileContent += temp;
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
                if ( fr != null ) {
                    fr.close();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        return fileContent;
    }
}
