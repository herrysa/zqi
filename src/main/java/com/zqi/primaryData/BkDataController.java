package com.zqi.primaryData;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.dataFinder.IFinderBk;
import com.zqi.dataFinder.sina.FinderSinaBk;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

@Controller
@RequestMapping("/bkData")
public class BkDataController  extends BaseController{

	@RequestMapping("/bkDataList")
	public String primaryDataList(){
		return "primaryData/bkDataList";
	}
	
	@ResponseBody
	@RequestMapping("/bkDataGridList")
	public Map<String, Object> bkDataGridList(HttpServletRequest request){
		String findSql = "select * from d_gpbk where 1=1";
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, findSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/importBkData")
	public Map<String, Object> importBkData(){
		IFinderBk iFinderBk = new FinderSinaBk();
		String bkData = iFinderBk.findBkInfoStr();
		String basePath = Tools.getResource("baseDir");
		String dicPath = basePath+Tools.getResource("dicDir");
		String filePath = dicPath+"d_gpbk.txt";
		File bkFile = new File(filePath);
		bkFile.deleteOnExit();
		FileUtil.writeFile(bkData, filePath);
		String dataCol = "code,name,bkType,bkName";
		String loadDataSql = "load data infile '"+filePath+"' into table d_gpbk("+dataCol+");";
		String deleteBkInfoSql = "delete from d_gpbk";
		zqiDao.excute(deleteBkInfoSql);
		zqiDao.excute(loadDataSql);
		setMessage("更新板块成功！");
		return resultMap;
	}
}
