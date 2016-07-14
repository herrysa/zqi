package com.zqi.primaryData;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zqi.dataFinder.IFinderGpDic;
import com.zqi.dataFinder.se.FinderGpDicSe;
import com.zqi.dataFinder.wy163.FinderGpDic163Zhishu;
import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.quartz.Zjob; 
import com.zqi.frame.util.Tools;
import com.zqi.unit.FileUtil;

@Service("dicChecker")
public class DicChecker implements Zjob{

	ZqiDao zqiDao;
	
	public ZqiDao getZqiDao() {
		return zqiDao;
	}

	public void setZqiDao(ZqiDao zqiDao) {
		this.zqiDao = zqiDao;
	}

	@Override
	public int execute() {
		IFinderGpDic iFinderGpDicSse = new FinderGpDicSe();
		List<Map<String, Object>> gpDicList = iFinderGpDicSse.findGpDic();
		IFinderGpDic iFinderGpDicZishu = new FinderGpDic163Zhishu();
		gpDicList.addAll(iFinderGpDicZishu.findGpDic());
		/*List<Map<String, Object>> oldList = zqiDao.findAll("select * from d_gpdic");
		Map<String, Map<String, Object>> oldDicMap = new HashMap<String, Map<String,Object>>();
		for( Map<String, Object> oldGp : oldList){
			String code = oldGp.get("code").toString();
			oldDicMap.put(code, oldGp);
		}
		for( Map<String, Object> newGp : gpDicList){
			String code = newGp.get("code").toString();
			String name = newGp.get("name").toString();
			
		}*/
		String gpDataCol = "code,symbol,name,symbolName,listDate,totalShares,totalFlowShares,endDate,pinyinCode,type,daytable";
		String[] gpDicCol = gpDataCol.split(",");
		String gpDataStr = "";
		for(Map<String, Object> gp : gpDicList){
			String daytable = gp.get("daytable").toString();
			String dayTableSql= "select count(*) count from information_schema.TABLES where table_name = '"+daytable+"' and TABLE_SCHEMA = 'zqi'";
			Map<String, Object> daytableRs = zqiDao.findFirst(dayTableSql);
			String count = daytableRs.get("count").toString();
			if("0".equals(count)){
				String daytableNum = daytable.replace("daytable", "");
				String[] numArr = daytableNum.split("_");
				if("6".equals(numArr[0])){
					daytable = "daytable6_1";
				}else if("3".equals(numArr[0])){
					daytable = "daytable3_1";
				}else{
					daytable = "daytable0_1";
				}
				gp.put("daytable",daytable);
			}
			gpDataStr += Tools.getTxtData(gp, gpDicCol);
		}
		String basePath = Tools.getResource("baseDir");
		String dicPath = basePath+Tools.getResource("dicDir");
		String filePath = dicPath+"d_gpdic.txt";
		File bkFile = new File(filePath);
		bkFile.deleteOnExit();
		FileUtil.writeFile(gpDataStr, filePath);
		String loadDataSql = "load data infile '"+filePath+"' into table d_gpdic("+gpDataCol+");";
		String deleteBkInfoSql = "delete from d_gpdic";
		zqiDao.excute(deleteBkInfoSql);
		zqiDao.excute(loadDataSql);
		System.out.println("--------------股票字典更新完毕-----------------");
		
		return 0;
	}

}
