package com.zqi.unit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
  
public class DBHelper {  
    public static final String url = "jdbc:mysql://127.0.0.1/zqi?characterEncoding=utf-8";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "1234";  
  
    public Connection conn = null;  
    public PreparedStatement pst = null;
    public Statement st = null;
  
    public DBHelper() {  
        try {  
            Class.forName(name);//ָ����������  
            conn = DriverManager.getConnection(url, user, password);//��ȡ����  
            st = conn.createStatement();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    public void prepareStatementSql(String sql){
    	try {
			pst = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addBatchSql(String sql){
    	try {
			st.addBatch(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  
    public void close() {  
        try {  
            this.conn.close();  
            this.pst.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}  