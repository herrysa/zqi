package com.zqi.frame.dao;

import java.util.List;
import java.util.Map;

import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.BSPager;
import com.zqi.frame.controller.pagers.JQueryPager;

public interface IZqiDao {

	/**  
     * 根据SQL建表  
     * @param sql  
     */  
    public void createTableBySQL(String sql);  
    
    /**  
     * 插入/更新/删除数据  
     * @param sql 有参数语句  
     * @param obj 参数值数组  
     */  
    public int update(String sql);
    
    //public int update(SQLUtil sqlUtil, String id);
    
    public void excute(String sql);
    
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
    
    public List findAll(String sql,Class T);
    
    public Map<String, Object> findFirst(String sql);
    
    public List findByFilter(BSPager bsPager,String tableName,List<PropertyFilter> filters);
    
    public int[] bathUpdate(String[] sqls);
    
    public int addList(List<Map<String, Object>> list,String table);
	
	public int add(Map<String, Object> map,String table);
	
	public JQueryPager findWithFilter(JQueryPager paginatedList,String sql,List<PropertyFilter> filters);
}
