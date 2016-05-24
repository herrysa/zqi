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

@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{

	private SQLUtil sQLUtil = new SQLUtil(new String[]{"code","name","type","remark"}, "r_report", "code", "");
	
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
			Map<String, Object> report = zqiDao.findFirst("select * from report where code='"+id+"'");
			model.put("report", report);
		}
		return "report/reportForm";
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public String save(HttpServletRequest request){
		String id = request.getParameter("id");
		Map<String, String> entityMap = getSaveMap(request,sQLUtil.columns);
		if(id!=null&&!"".equals(id)){
			String updateSql = sQLUtil.sql_update(entityMap, id);
			zqiDao.update(updateSql);
		}else{
			String inseartSql = sQLUtil.sql_inseart(entityMap);
			zqiDao.excute(inseartSql);
		}
		return "保存成功!";
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
		String code = request.getParameter("code");
		model.put("code", code);
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
	
	public Map<String, String> getSaveMap(HttpServletRequest request,String[] columns){
		Map<String, String> saveMap = new HashMap<String, String>();
		for(String column : columns){
			String value = request.getParameter(column);
			saveMap.put(column, value);
		}
		return saveMap;
	}
	
	public String replaceVari(HttpServletRequest request,String str){
		List<Map<String, Object>> periodList = null;
		periodList = getPeriodList(request);
		str = str.replaceAll("%lastperiod%", periodList.get(0).get("period").toString());
		
		return str;
	}
	
	public List<Map<String, Object>> getPeriodList(HttpServletRequest request){
		HttpSession session = request.getSession();
		List<Map<String, Object>> periodList = null;
		periodList = (List<Map<String, Object>>)session.getAttribute("periodList");
		if(periodList==null){
			String sql = "select period from daytable_all where code='0000001' order by period desc";
			periodList = zqiDao.findAll(sql);
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
		funcMap.put("sourcepayinSum", "select sum(amount) from v_sourcepayin where checkPeriod BETWEEN ? and ? and kdDeptId=?");
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
			func.setFunc(funcBody);
			Iterator<Element> paraIt = element.elementIterator("Para");
			while(paraIt.hasNext()){
				Element para = paraIt.next();
				String p = para.getTextTrim();
				func.addPara(p);
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
		int thredNum = 10;
		double eachLength = Math.ceil(funcList.size()/10);
		List<Thread> threads = new ArrayList<Thread>();
		for(int i=0;i<thredNum;i++){
			BathFuncThread bathFuncThread = new BathFuncThread((int)(i*eachLength), (int)((i+1)*eachLength-1), funcList);
			Thread thread = new Thread(bathFuncThread);
			thread.start();
			threads.add(thread);
		}
		for(Thread thread : threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
