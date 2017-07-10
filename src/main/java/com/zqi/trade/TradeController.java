package com.zqi.trade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.primaryData.fileDataBase.IFileDataBase;
import com.zqi.primaryData.fileDataBase.MyTradeFileDataBase;
import com.zqi.unit.ExcelUtil;

@Controller
@RequestMapping("/trade")
public class TradeController extends BaseController{

	
	@RequestMapping("/tradeMain")
	public String tradeMain(HttpServletRequest request,ModelMap model){
		
		return "trade/tradeMain";
	}
	
	@RequestMapping("/positionList")
	public String positionList(HttpServletRequest request,ModelMap model){
		
		return "trade/positionList";
	}
	
	@ResponseBody
	@RequestMapping("/positionGridList")
	public Map<String, Object> strategyGridList(HttpServletRequest request){
		
		Calendar calendar = Calendar.getInstance();
		IFileDataBase myTradeFileDataBase = new MyTradeFileDataBase(""+calendar.get(Calendar.YEAR));
		List<Map<String, Object>> positionList = myTradeFileDataBase.readList("position");
		resultMap.put("rows", positionList);
		return resultMap;
	}
	
	@RequestMapping("/tradeParse")
	public String tradeParse(HttpServletRequest request,ModelMap model){
		
		return "trade/tradeParseMain";
	}
	
	@RequestMapping("/jgdList")
	public String jdgList(HttpServletRequest request,ModelMap model){
		
		return "trade/jgdList";
	}
	
	@ResponseBody
	@RequestMapping("/jgdGridList")
	public Map<String, Object> jgdGridList(HttpServletRequest request){
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, "select * from i_jgd", filters);
		List<Map<String, Object>> reportData = pagedRequests.getList();
		makeResultMap(pagedRequests);
		return resultMap;
	}
	
	public static byte[] getBytes(InputStream is) throws IOException {
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int len;
	    byte[] data = new byte[100000];
	    while ((len = is.read(data, 0, data.length)) != -1) {
	    buffer.write(data, 0, len);
	    }

	    buffer.flush();
	    return buffer.toByteArray();
	}
	@ResponseBody
	@RequestMapping("/importJgd")
	public Map<String, Object> importJgd(@RequestParam("jgdFile") CommonsMultipartFile[]  files,HttpServletRequest request){
		String[] columns = {"period","businessType","code","name","price","amount","remainder","money","cmoney","cash","stampTex","transferFee","commission","transferFee2","frontFee","entrustCode","tradeCode","clientCode"};
		String insertSql = "insert into i_jgd (";
        String valueSql = "";
        for ( int j = 0; j < columns.length; j++ ) {
            insertSql = insertSql + columns[j] + ",";
            valueSql = valueSql + "?,";
        }
        insertSql = insertSql.substring( 0, insertSql.length() - 1 );
        valueSql = valueSql.substring( 0, valueSql.length() - 1 );
        insertSql = insertSql + ") values (" + valueSql + ")";
        String basePath = "E:/git/zqi/src/main/webapp/home/temporary/";
		for(int f = 0;f<files.length;f++){  
            if(!files[f].isEmpty()){ 
            	CommonsMultipartFile multiFile = files[f];
            	long timeMillis = System.currentTimeMillis();
            	File file = new File(basePath+timeMillis+".xls");
            	try {
					multiFile.transferTo(file);
				} catch (IllegalStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Workbook book = new HSSFWorkbook( new FileInputStream(file) );
					Sheet sheet = book.getSheetAt( 0 );
					for ( int j = 1;; j++ ) {
						ArrayList sqlParaList = new ArrayList();
						Row row = sheet.getRow( j );
			            if ( row == null )
			                break;
			            for ( int i = 0; i < columns.length; i++ ){
			            	Cell cell = row.getCell( i );
			            	if ( cell != null ){
			            		String cellValue = ExcelUtil.getValue(cell);
			            		sqlParaList.add( cellValue );
			            	}else{
			            		sqlParaList.add(null);
			            	}
						}
			            Object[] sqlParamArray = sqlParaList.toArray( new Object[sqlParaList.size()] );
			            zqiDao.getJdbcTemplate().update( insertSql, sqlParamArray );
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (EncryptedDocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
		}
		
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/jgdAnalysis")
	public String jgdAnalysis(HttpServletRequest request,ModelMap model){
		List<Map<String, Object>> jgdList = zqiDao.findAll("select * from i_jgd");
		Map<String, Object> gpMap = new HashMap<String, Object>();
		for(Map<String, Object> jgd : jgdList){
			String businessType = jgd.get("businessType").toString();
			String code = jgd.get("code")==null?"":jgd.get("code").toString();
			Map<String, Object> position = (Map<String, Object>)gpMap.get(code);
			Integer amount = jgd.get("amount")==null?0:Integer.parseInt(jgd.get("amount").toString());
			Double price = jgd.get("price")==null?0:Double.parseDouble(jgd.get("price").toString());
			Double cmoney = jgd.get("cmoney")==null?0:Double.parseDouble(jgd.get("cmoney").toString());
			if("证券买入".equals(businessType)){
				if(position==null){
					position = new HashMap<String, Object>();
					position.put("amount", amount);
					position.put("price", price);
					position.put("cmoney", cmoney);
					gpMap.put(code,position);
				}else{
					Integer pAmmout = (Integer)position.get("amount");
					Double pCmoney = (Double)position.get("cmoney");
					pAmmout += amount;
					pCmoney += cmoney;
					position.put("amount", amount);
					position.put("cmoney", cmoney);
				}
				
			}else if("证券卖出".equals(businessType)){
				if(position!=null){
					Integer pAmmout = (Integer)position.get("amount");
					Double pCmoney = (Double)position.get("cmoney");
					pAmmout -= amount;
					pCmoney += cmoney;
					position.put("amount", amount);
					position.put("cmoney", cmoney);
				}
			}
			
		}
		return "trade/jgdAnalysis";
	}
}
