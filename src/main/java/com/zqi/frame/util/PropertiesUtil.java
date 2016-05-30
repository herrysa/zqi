package com.zqi.frame.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil 
{ 
    
    private static Logger logger = Logger.getLogger(PropertiesUtil.class); 
        
    /** 
     * 增加属性文件值 
     * @param key 
     * @param value 
     */ 
    public static void addProperties(String key[], String value[], String file) 
    { 
        Properties iniFile = getProperties(file); 
        FileOutputStream oFile = null; 
        try 
        { 
            iniFile.put(key, value); 
            oFile = new FileOutputStream(file, true); 
            iniFile.store(oFile, "modify properties file"); 
        } 
        catch (FileNotFoundException e) 
        { 
            logger.warn("do " + file + " FileNotFoundException:", e); 
        } 
        catch (IOException e) 
        { 
            logger.warn("do " + file + " IOException:", e); 
        } 
        finally 
        { 
            try 
            { 
                if (oFile != null) 
                { 
                    oFile.close(); 
                } 
            } 
            catch (IOException e) 
            { 
                logger.warn("do " + file + " IOException:", e); 
            } 
        } 
    } 
    
    /** 
     * 读取配置文件 
     * @return 
     */ 
    public static Properties getProperties(String file) 
    { 
        Properties pro = null; 
        FileInputStream in = null; 
        try 
        { 
            in = new FileInputStream(file); 
            pro = new Properties(); 
            pro.load(in); 
            
        } 
        catch (Exception e) 
        { 
            logger.warn("Read " + file + " IOException:", e); 
        } 
        finally 
        { 
            try 
            { 
                if (in != null) 
                { 
                    in.close(); 
                } 
            } 
            catch (IOException e) 
            { 
                logger.warn("Read " + file + " IOException:", e); 
            } 
        } 
        return pro; 
    } 
    
    /** 
     * 保存属性到文件中 
     * @param pro 
     * @param file 
     */ 
    public static void saveProperties(Properties pro, String file) 
    { 
        if (pro == null) 
        { 
            return; 
        } 
        FileOutputStream oFile = null; 
        try 
        { 
            oFile = new FileOutputStream(file, false); 
            pro.store(oFile, "modify properties file"); 
        } 
        catch (FileNotFoundException e) 
        { 
            logger.warn("do " + file + " FileNotFoundException:", e); 
        } 
        catch (IOException e) 
        { 
            logger.warn("do " + file + " IOException:", e); 
        } 
        finally 
        { 
            try 
            { 
                if (oFile != null) 
                { 
                    oFile.close(); 
                } 
            } 
            catch (IOException e) 
            { 
                logger.warn("do " + file + " IOException:", e); 
            } 
        } 
    } 
    
    /** 
     * 修改属性文件 
     * @param key 
     * @param value 
     */ 
    public static void updateProperties(String key, String value, String file) 
    { 
        //key为空则返回 
        if (key == null || "".equalsIgnoreCase(key)) 
        { 
            return; 
        } 
        Properties pro = getProperties(file); 
        if (pro == null) 
        { 
            pro = new Properties(); 
        } 
        pro.put(key, value); 
        
        //保存属性到文件中 
        saveProperties(pro, file); 
    } 
}