 package com.zqi.unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
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
    public static String readFile( File ds ) {
    	FileInputStream fis = null;
    	InputStreamReader isr=null;
        BufferedReader br = null;
        String fileContent = "";
        String temp = "";
        try {
            if ( ds.exists()&& ds.isFile() ) {
                fileContent = "";
                fis = new FileInputStream( ds );
                isr = new InputStreamReader(fis,"UTF-8");
                br = new BufferedReader( isr );
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
    public static void delFolder( String folderPath ) {
        try {
            delAllFile( folderPath ); //删除完里面所有内�?
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File( filePath );
            myFilePath.delete(); //删除空文件夹
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下�?��文件
     * param path 文件夹完整绝对路�?
     * 
     */
    public static boolean delAllFile( String path ) {
        boolean flag = false;
        File file = new File( path );
        if ( !file.exists() ) {
            return flag;
        }
        if ( !file.isDirectory() ) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for ( int i = 0; i < tempList.length; i++ ) {
            if ( path.endsWith( File.separator ) ) {
                temp = new File( path + tempList[i] );
            }
            else {
                temp = new File( path + File.separator + tempList[i] );
            }
            if ( temp.isFile() ) {
                temp.delete();
            }
            if ( temp.isDirectory() ) {
                delAllFile( path + "/" + tempList[i] );//先删除文件夹里面的文�?
                delFolder( path + "/" + tempList[i] );//再删除空文件�?
                flag = true;
            }
        }
        return flag;
    }
}
