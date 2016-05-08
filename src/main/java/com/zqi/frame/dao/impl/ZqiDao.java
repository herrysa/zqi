package com.zqi.frame.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.filter.PropertyFilter.MatchType;
import com.zqi.frame.controller.pagers.BSPager;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.SortOrderEnum;
import com.zqi.frame.dao.IZqiDao;

@Repository("zqiDao")
public class ZqiDao implements IZqiDao{

	JdbcTemplate jdbcTemplate;
	DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ZqiDao(){
		
	}

	@Override
	public void createTableBySQL(String sql) {
		jdbcTemplate.execute(sql);
	}


	@Override
	public int update(String sql) {
		return jdbcTemplate.update(sql);
	}
	
	
	@Override
	public int findRowCountBySQL(String sql) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findAll(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> findFirst(String sql) {
		List list = jdbcTemplate.queryForList(sql);
		Map<String, Object> rs0 = null;
		if(list!=null&&list.size()>0){
			rs0 = (Map<String, Object>)list.get(0);
		}else{
			rs0 = new HashMap<String, Object>();
		}
		return rs0;
	}

	@Override
	public List findByFilter(BSPager bsPager, String tableName,List<PropertyFilter> filters) {
		
		return null;
	}

	@Override
	public int[] bathUpdate(String[] sqls) {
		return jdbcTemplate.batchUpdate(sqls);
	}
	@Override
	public int addList(List<Map<String, Object>> list,String table) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(table);
		/*for(Map<String, Object> log :list){
			simpleJdbcInsert.execute(log);
		}*/
		Map<String, Object>[] addArr = list.toArray(new Map[list.size()]);
		simpleJdbcInsert.executeBatch(addArr);
		return 0;
	}
	@Override
	public int add(Map<String, Object> map,String table) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(table);
		return simpleJdbcInsert.execute(map);
	}
	@Override
	public void excute(String sql) {
		jdbcTemplate.execute(sql);
	}
	@Override
	public JQueryPager findWithFilter(JQueryPager paginatedList,String sql,List<PropertyFilter> filters) {
		String orderBy = "";
		String orderName = paginatedList.getSortCriterion();
		if(orderName!=null){
			SortOrderEnum direction = paginatedList.getSortDirection();
			if(direction==SortOrderEnum.ASCENDING){
				orderBy += "order by " + orderName + " asc";
			}else{
				orderBy += "order by " + orderName + " desc";
			}
			
		}
		Iterator itr = filters.iterator();
		List rs = null;
		try {
            while ( itr.hasNext() ) {
                PropertyFilter pf = (PropertyFilter) itr.next();
                String fieldName= pf.getPropertyName();
                String operator = "";
                String v = "";
                if ( pf.getMatchType().equals( MatchType.LIKE ) ) {
                    v = (String) pf.getMatchValue();
                    operator = " like ";
                }
                else if ( pf.getMatchType().equals( MatchType.EQ ) ) {
                	operator = " = ";
                }
                else if ( pf.getMatchType().equals( MatchType.GE ) ) {
                	operator = " >= ";
                }
                else if ( pf.getMatchType().equals( MatchType.GT ) ) {
                	operator = " > ";
                }
                else if ( pf.getMatchType().equals( MatchType.IN ) ) {
                	operator = " in ";
                }
                else if ( pf.getMatchType().equals( MatchType.NI ) ) {
                	operator = " not in ";
                }
                else if ( pf.getMatchType().equals( MatchType.ISNOTNULL ) ) {
                	operator = " not in ";
                }
                else if ( pf.getMatchType().equals( MatchType.ISNULL ) ) {
                	operator = " not in ";
                }
                else if ( pf.getMatchType().equals( MatchType.LE ) ) {
                	operator = " <= ";
                }
                else if ( pf.getMatchType().equals( MatchType.LT ) ) {
                	operator = " < ";
                }
                else if ( pf.getMatchType().equals( MatchType.NE ) ) {
                	operator = " <> ";
                }
                else if ( pf.getMatchType().equals( MatchType.OA ) ) {
                	
                }
                else if ( pf.getMatchType().equals( MatchType.OD ) ) {
                	
                }else if( pf.getMatchType().equals(MatchType.SQ)){
                	
                }
                else {
                	
                }
                sql += " and "+fieldName+operator+v;
	        }
            if(!"".equals(orderBy)){
            	sql += " "+orderBy;
            }
            String listSql = sql+" limit "+paginatedList.getStart()+","+paginatedList.getEnd();
            rs = jdbcTemplate.queryForList(listSql);
            int fromIndex = sql.indexOf("from");
            sql = sql.substring(fromIndex);
            sql = "select count(*) count "+sql;
            Map<String, Object> countMap = jdbcTemplate.queryForMap(sql);
            if(countMap!=null&&!countMap.isEmpty()){
            	Long count = (Long)countMap.get("count");
            	paginatedList.setTotalNumberOfRows(count.intValue());
            }
        } catch (Exception e) {
			e.printStackTrace();
		}
		if(rs==null){
			rs = new ArrayList();
		}
		paginatedList.setList(rs);
		return paginatedList;
	}

}
