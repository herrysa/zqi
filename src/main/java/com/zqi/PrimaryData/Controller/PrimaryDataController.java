package com.zqi.PrimaryData.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.PrimaryData.dao.IPrimaryDataDao;

@Controller
@RequestMapping("/primaryData")
public class PrimaryDataController{

	private IPrimaryDataDao iPrimaryDataDao;
	
	public IPrimaryDataDao getiPrimaryDataDao() {
		return iPrimaryDataDao;
	}

	@Autowired
	public void setiPrimaryDataDao(IPrimaryDataDao iPrimaryDataDao) {
		this.iPrimaryDataDao = iPrimaryDataDao;
	}


	@ResponseBody
	@RequestMapping("/primaryDataGridList")
	public Map<String, Object> primaryDataGridList(HttpServletRequest request,String gpCode){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		String[] columns = request.getParameterValues("columns");
		Object q = request.getAttribute("columns");
		Map columns1 = request.getParameterMap();
		Set<Entry<String, Object>> pp = columns1.entrySet();
		Set<String> keys = columns1.keySet();
		for(Entry<String, Object> p : pp){
			Object v= p.getValue();
			if(v instanceof String){
				System.out.println(p.getKey()+":"+p.getValue());
			}else{
				String[] vArr = (String[])v;
				System.out.println(p.getKey()+":"+vArr[0]);
			}
		}
		Map<String, Object> r = new HashMap<String, Object>();
		if(code!=null&&!"".equals(code)){
			String findDayTableSql = "select daytable from d_gpDic where code='"+code+"'";
			String tableName = "";
			Map<String, Object> rs0 = iPrimaryDataDao.findFirst(findDayTableSql);
			if(!rs0.isEmpty()){
				tableName = rs0.get("daytable").toString();
			}
			String dayDataSql = "select * from "+tableName+" where code='"+code+"'";
			if(period!=null&&!"".equals(period)){
				dayDataSql += " and period='"+period+"'";
			}
			List<Map<String, Object>> dayData = iPrimaryDataDao.findAll(dayDataSql);
			/*List<Map<String, String>> result = new ArrayList<Map<String,String>>();
			Map<String, String> row = new HashMap<String, String>();
			row.put("customer_id", "1");
			row.put("lastname", "1");
			row.put("firstname", "1");
			row.put("email", "1");
			result.add(row);*/
			r.put("page_data", dayData);
			r.put("total_rows", dayData.size());
		}
		return r;
	}
	
	@RequestMapping("/primaryDataList")
	public String primaryDataList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		return "primaryData/primaryDataList";
	}
}
