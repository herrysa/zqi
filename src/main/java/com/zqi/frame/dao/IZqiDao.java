package com.zqi.frame.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.KeyHolder;

import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.BSPager;

public interface IZqiDao {

	/**  
     * 根据SQL建表  
     * @param sql  
     */  
    public void createTableBySQL(String sql);  
    
    /**  
     * 插入记录并返回自动生成的主键Id  
     * @param ps  
     * @return  
     */  
    public KeyHolder insertActor(Map entity);  
    
    /**  
     * 插入/更新/删除数据  
     * @param sql 有参数语句  
     * @param obj 参数值数组  
     */  
    public int operateActor(String sql,Object[] obj);   
    
    /**  
     * 根据SQL查询记录总数  
     * @param sql  
     * @return  
     */  
    public int findRowCountBySQL(String sql); 
    
    /**  
     * 返回所有对象  
     * @return  
     */  
    public List findAll();
    
    /**  
     * 返回所有对象  
     * @return  
     */  
    public List findAll(String sql);
    
    public Map<String, Object> findFirst(String sql);
    
    public List findByFilter(BSPager bsPager,String tableName,List<PropertyFilter> filters);
    
    public int[] bathUpdate(String[] sqls);
}
