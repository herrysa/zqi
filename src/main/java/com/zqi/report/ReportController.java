package com.zqi.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.SQLUtil;
import com.zqi.frame.util.TestTimer;
import com.zqi.frame.util.XMLUtil;
import com.zqi.report.model.ReportFunc;
import com.zqi.unit.DateUtil;

@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{

	private SQLUtil sQLUtil = new SQLUtil(new String[]{"code","name","type","dataSource","dsDesc","remark"}, "r_report", "code", "");
	
	@RequestMapping("/reportMain")
	public String reportMain(){
		
		return "report/reportMain";
	}
	
	@RequestMapping("/reportList")
	public String reportList(){
		
		return "report/reportList";
	}
	
	@RequestMapping("/reportForm")
	public String reportForm(HttpServletRequest request,ModelMap model){
		String id = request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			Map<String, Object> report = zqiDao.findFirst("select * from r_report where code='"+id+"'");
			model.put("report", report);
		}
		return "report/reportForm";
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public Map<String, Object> save(HttpServletRequest request){
		super.save(request,sQLUtil);
		resultMap.put("message", "保存成功!");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public Map<String, Object> delete(HttpServletRequest request){
		String id = request.getParameter("code");
		if(id!=null&&!"".equals(id)){
			String updateSql = sQLUtil.sql_delete( id);
			zqiDao.update(updateSql);
		}
		resultMap.put("message", "删除成功!");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/reportGridList")
	public Map<String, Object> reportGridList(HttpServletRequest request){
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, "select * from r_report", filters);
		List<Map<String, Object>> reportData = pagedRequests.getList();
		makeResultMap(pagedRequests);
		return resultMap;
	}
	
	@RequestMapping("/show")
	public String showReport(HttpServletRequest request,ModelMap model){
		String sql = sQLUtil.sql_get(request);
		Map<String, Object> report = zqiDao.findFirst(sql);
		String dataSource = report.get("dataSource").toString();
		String dsDesc = report.get("dsDesc").toString();
		dataSource = dataSource.replaceAll("\n", "");
		dataSource = dataSource.replaceAll("\r", "");
		dsDesc = dsDesc.replaceAll("\n", "");
		dsDesc = dsDesc.replaceAll("\r", "");
		report.put("dataSource", dataSource);
		report.put("dsDesc", dsDesc);
		model.put("report", report);
		return "report/reportShow";
	}
	
	@ResponseBody
	@RequestMapping("/getReportXml")
	public String getReportXml(HttpServletRequest request,HttpServletResponse response){
		String code = request.getParameter("code");
		HttpSession session = request.getSession();
		String reportPath = session.getServletContext().getRealPath("report");
		File report = new File(reportPath+"/user/"+code+".xml");
		String reportXml = null;
		if(!report.exists()){
			report = new File(reportPath+"/blank.xml");
		}
		reportXml = XMLUtil.xmltoString(XMLUtil.read(report, "UTF-8"));
		try {
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/xml;charset=utf-8");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out;
			out = response.getWriter();
			out.write(reportXml.toString());  
			out.flush();  
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
        return null;
	}
	
	@ResponseBody
	@RequestMapping("/saveReportXml")
	public Map<String, Object> saveReportXml(HttpServletRequest request){
		String code = request.getParameter("code");
		String reportXml = request.getParameter("reportXml");
		
		Document document = XMLUtil.stringToXml(reportXml);
		HttpSession session = request.getSession();
		String reportPath = session.getServletContext().getRealPath("report/user");
		try {
			XMLUtil.writDocumentToFile(document, reportPath+"/"+code+".xml", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("message", "1");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/initData")
	public Map<String, Object> initData(HttpServletRequest request){
		String days = request.getParameter("days");
		if(days!=null){
			//int dayCount = Integer.parseInt(days);
			HttpSession session = request.getSession();
			List<Map<String, Object>> periodList = null;
			String sql = "select period from daytable_all where code='0000001' order by period desc limit 0,"+days;
			periodList = zqiDao.findAll(sql);
			session.setAttribute("periodList",periodList);
			String periodStr = "";
			//int d=0;
			for(Map<String, Object> periodMap : periodList){
				periodStr += "'"+periodMap.get("period")+"',";
				/*d++;
				if(d>=dayCount){
					break;
				}*/
			}
			if(!"".equals(periodStr)){
				periodStr = periodStr.substring(0, periodStr.length()-1);
			}else{
				periodStr = "''";
			}
			String daySql = "select * from daytable_all where period in("+periodStr+") order by period desc";
			zqiDao.excute("DROP TABLE report_daytable;");
			zqiDao.excute("create table report_daytable ("+daySql+");");
 			resultMap.put("message", "初始化成功！") ;
		}else{
			resultMap.put("message", "请输入初始化天数！");
		}
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/getDataSourceBySql")
	public List<Map<String, Object>> getDataSourceBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		sql = replaceVari(request,sql);
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests.setPageSize(500);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, sql, filters);
		List<Map<String, Object>> rsList = pagedRequests.getList();
		return rsList;
	}
	
	@ResponseBody
	@RequestMapping("/getDataBySql")
	public Map<String, Object> getDataBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		Map<String, Object> rs = zqiDao.findFirst(sql);
		resultMap.put("rs", rs);
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/getListDataBySql")
	public Map<String, Object> getListDataBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		List<Map<String, Object>> rs = zqiDao.findAll(sql);
		resultMap.put("rs", rs);
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/findPeriodList")
	public Map<String, Object> findPeriodList(HttpServletRequest request){
		List<Map<String, Object>> periodList = null;
		periodList = getPeriodList(request);
		resultMap.put("periodList", periodList);
		return resultMap;
	}
	
	
	public String replaceVari(HttpServletRequest request,String str){
		List<Map<String, Object>> periodList = null;
		periodList = getPeriodList(request);
		str = str.replaceAll("%lastperiod%", "'"+periodList.get(0).get("period").toString()+"'");
		
		return str;
	}
	
	public List<Map<String, Object>> getPeriodList(HttpServletRequest request){
		HttpSession session = request.getSession();
		List<Map<String, Object>> periodList = null;
		periodList = (List<Map<String, Object>>)session.getAttribute("periodList");
		if(periodList==null){
			String sql = "select period from report_daytable where code='0000001' order by period desc";
			periodList = zqiDao.findAll(sql);
			session.setAttribute("periodList",periodList);
		}
		return periodList;
	}
	
	@ResponseBody
	@RequestMapping("/batchFunc")
	public String batchFunc(HttpServletRequest request,HttpServletResponse response){
		TestTimer tt = new TestTimer("batchFunc");
		tt.begin();
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				result += line+"\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, String> funcMap = new HashMap<String, String>();
		funcMap.put("findRData","select changepercent from daytable_all where period=? and code=?");
		List<ReportFunc> funcList = parseFunc(result,funcMap);
		exeFunc(funcList);
		String returnXml = funcToXml(funcList);
		//设置编码  
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("text/xml;charset=utf-8");  
		response.setHeader("Cache-Control", "no-cache");  
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(returnXml);  
			out.flush();  
			out.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		System.out.println(funcList.size());
		tt.done();
		return null;
	}
	
	private List<ReportFunc> parseFunc(String xml,Map<String, String> funcMap){
		List<ReportFunc> funcList = new ArrayList<ReportFunc>();
		Document doc = XMLUtil.stringToXml(xml);
		Element root= doc.getRootElement();
		Element elementFuncs = root.element("Functions");
		Iterator<Element> elementIt = elementFuncs.elementIterator("Function");
		while(elementIt.hasNext()){
			ReportFunc func =new ReportFunc();
			Element element = elementIt.next();
			String funcName = element.attributeValue("name");
			func.setName(funcName);
			String funcBody = funcMap.get(funcName);
			if(funcBody!=null&&!"".equals(funcBody)){
				func.setFunc(funcBody);
				Iterator<Element> paraIt = element.elementIterator("Para");
				while(paraIt.hasNext()){
					Element para = paraIt.next();
					String p = para.getTextTrim();
					if(p==null||"".equals(p)){
						func.setExecute(false);
					}
					func.addPara(p);
				}
				int qmNum = getQuestionMark(func.getFunc());
				if(func.getPara().length!=qmNum){
					func.setExecute(false);
				}
			}else{
				func.setExecute(false);
			}
			funcList.add(func);
		}
		return funcList;
	}
	
	private String funcToXml(List<ReportFunc> funcList){
		Document doc = XMLUtil.createDocument();
		Element root = doc.addElement("Root");
		Element funcs = root.addElement("Functions");
		for(ReportFunc reportFunc : funcList){
			Element func = funcs.addElement("Function");
			String value = reportFunc.getValue();
			if(value!=null){
				func.setText(value);
			}else{
				func.setText("");
			}
			
		}
		return XMLUtil.xmltoString(doc,"UTF-8");
	}
	
	private void exeFunc(List<ReportFunc> funcList){
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		for(ReportFunc func : funcList){
			BathFuncThread bathFuncThread = new BathFuncThread(func);
			fixedThreadPool.execute(bathFuncThread);
		}
		try {
			fixedThreadPool.shutdown();
			while(!fixedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int getQuestionMark(String str){
		String pattern = "\\?";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		int matcherCount = 0;
		while(matcher.find()){
			matcherCount++;
		}
		return matcherCount;
	}
	
	public static void main(String[] args) {
		String funcBody = "aaass?dsd?? dd?";
		String pattern = "\\?";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(funcBody);
		int matcherCount = 0;
		while(matcher.find()){
			matcherCount++;
		}
		System.out.println(matcherCount);
	}
}
