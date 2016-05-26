package com.zqi.unit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

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
    
    /**
     * 文件读写方法
     * 
     * @param content
     * @param filePath
     */
    public synchronized static void writeFile( String content, String filePath ) {
        mkParent( filePath );
        try {
        	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8");
            out.write( content );
            out.flush();
            out.close();
        }
        catch ( Exception e ) {
            e.printStackTrace();

        }
    }
    
    /**
     * 生成路径
     * 
     * @param filePath
     */
    public static void mkParent( String filePath ) {
        File f = new File( filePath );
        File pf = f.getParentFile();
        while ( !pf.exists() ) {
            if ( pf.getParentFile().exists() )
                pf.mkdir();
            else
                mkParent( pf.getPath() );
            try {
                Thread.sleep( 1000 );
            }
            catch ( Exception e ) {

            }
        }
    }
}
