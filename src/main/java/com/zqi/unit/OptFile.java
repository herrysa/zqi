package com.zqi.unit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class OptFile {

    /**
     * 文件读写方法
     * 
     * @param content
     * @param filePath
     */
    public synchronized static void writeFile( String content, String filePath ) {
        mkParent( filePath );
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {

            fw = new FileWriter( filePath, true );
            bw = new BufferedWriter( fw );
            bw.write( content );
            bw.flush();
            fw.flush();
        }
        catch ( Exception e ) {
            e.printStackTrace();

        }
        finally {
            if ( bw != null && fw != null ) {
                try {
                    bw.close();
                    fw.close();
                }
                catch ( Exception ex ) {

                }
                bw = null;
            }
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

    /**
     * 复制文件
     * 
     * @param 
     */
    public static void copyFile( File sourceFile, File targetFile )
        throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream( new FileInputStream( sourceFile ) );

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream( new FileOutputStream( targetFile ) );

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ( ( len = inBuff.read( b ) ) != -1 ) {
                outBuff.write( b, 0, len );
            }
            // 刷新此缓冲的输出�?
            outBuff.flush();
        }
        finally {
            // 关闭�?
            if ( inBuff != null )
                inBuff.close();
            if ( outBuff != null )
                outBuff.close();
        }
    }

    /**
     * @description	读文件方�?
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
            // TODO: handle exception
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
                // TODO: handle exception
                e.printStackTrace();
            }

        }
        return fileContent;
    }

}
