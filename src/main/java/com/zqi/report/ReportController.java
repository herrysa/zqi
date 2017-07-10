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

import org.apache.commons.lang.StringUtils;
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
import com.zqi.unit.SupcanUtil;

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
			zqiDao.excute("DROP TABLE IF EXISTS report_daytable;");
			zqiDao.excute("create table report_daytable ("+daySql+");");
 			resultMap.put("message", "初始化成功！") ;
		}else{
			resultMap.put("message", "请输入初始化天数！");
		}
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getDataSourceBySql")
	public List<Map<String, Object>> getDataSourceBySql(HttpServletRequest request){
		String sql = request.getParameter("sql");
		//sql = replaceVari(request,sql);
		/*List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		pagedRequests.setPageSize(500);
		pagedRequests = zqiDao.findWithFilter(pagedRequests, sql, filters);
		List<Map<String, Object>> rsList = pagedRequests.getList();*/
		List<Map<String, Object>> rsList = zqiDao.findAll(sql);
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
		str = str.replaceAll("%lastperiod%", periodList.get(0).get("period").toString());
		
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
		
		String findFuncs =  "select * from r_reportFunc";
		List<Map<String, Object>> funcsList = zqiDao.findAll(findFuncs);
		Map<String, Map<String, Object>> funcMap = new HashMap<String, Map<String, Object>>();
		for(Map<String, Object> func : funcsList){
			funcMap.put(func.get("code").toString(), func);
		}
		
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
		//System.out.println(funcList.size());
		tt.done();
		return null;
	}
	
	private List<ReportFunc> parseFunc(String xml,Map<String, Map<String, Object>> funcMap){
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
			
			Map<String, Object> reprotFunc = funcMap.get(funcName);
			if(reprotFunc==null){
				continue;
			}
			String funcBody = reprotFunc.get("funcSql").toString();
			if(funcBody!=null&&!"".equals(funcBody)){
				String funcType = reprotFunc.get("type").toString();
				String rsType = reprotFunc.get("rsType").toString();
				String returnType = reprotFunc.get("returnType").toString();
				func.setType(funcType);
				func.setRsType(rsType);
				func.setReturnType(returnType);
				String funcParam = reprotFunc.get("params").toString();
				String[] paramArr = funcParam.split(";");
				List<String> paramNameList = new ArrayList<String>();
				for(String p : paramArr){
					String[] pArr = p.split(":");
					paramNameList.add(pArr[0]);
				}
				
				Map<String, String> paramMap = new HashMap<String, String>();
				Iterator<Element> paraIt = element.elementIterator("Para");
				int pIndex = 0;
				while(paraIt.hasNext()&&pIndex<paramNameList.size()){
					Element para = paraIt.next();
					String paramName = paramNameList.get(pIndex);
					String p = para.getTextTrim();
					if(p==null||"".equals(p)){
						paramMap.put(paramName, null);
					}else{
						paramMap.put(paramName, p);
					}
					pIndex++;
				}
				//List<String> variList = getVariStr(funcBody);
				//List<String> filterList = getFilterStr(funcBody);
				
				for(String paramName : paramNameList){
					if(funcBody.contains("%"+paramName+"%")){
						String filterStr = getFilterStr(funcBody,paramName);
						String paramValue = paramMap.get(paramName);
						/*if(paramValue==null){
							paramValue = "";
						}*/
						if(!"".equals(filterStr)){
							if(paramValue==null){
								funcBody.replace(filterStr, "");
							}else{
								funcBody.replace(filterStr, filterStr.substring(1, filterStr.length()-1));
							}
						}
						if(paramValue==null){
							paramValue = "";
						}
						funcBody = funcBody.replaceAll("%"+paramName+"%", paramValue);
					}
				}
				func.setFunc(funcBody);
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
	
	private List<String> getFilterStr(String sql){
		String pattern = "(?<=\\{)[^\\}]+";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(sql);
		List<String> filterList = new ArrayList<String>();
		while(matcher.find()){
			filterList.add(matcher.group());
		}
		return filterList;
	}
	
	private String getFilterStr(String sql,String vari){
		String pattern = "(?<=\\{).*"+vari+"{1}[^\\}]+";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(sql);
		String filterStr = "";
		while(matcher.find()){
			vari = matcher.group();
		}
		return filterStr;
	}
	
	private List<String> getVariStr(String sql){
		String pattern = "%(.*?)%";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(sql);
		List<String> filterList = new ArrayList<String>();
		while(matcher.find()){
			filterList.add(matcher.group());
		}
		return filterList;
	}
	
	public static void main(String[] args) {
		String funcBody = "aaass{ds%abc%d}{?}{? d}{d?";
		//String funcBody = "aaa)ss%ds%d%)(%{?){? d}{d";
		//String pattern = "\\{.*\\}";
		String pattern = "(?<=\\{).*abc{1}[^\\}]+";
		//String pattern = "%(.*?)%";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(funcBody);
		int matcherCount = 0;
		while(matcher.find()){
			System.out.println(matcher.group());
			matcherCount++;
		}
		System.out.println(matcherCount);
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getReportFunctionXml")
	public String getReportFunctionXml(HttpServletRequest request,HttpServletResponse response){
		try {
			Document document = XMLUtil.createDocument();
			Element root = document.addElement("root");
			Element functionsE = root.addElement("functions");
			List<Map<String, Object>> functions = zqiDao.findAll("select * from r_reportfunc");
			//Map<String,List<ReportFunction>> funcCategoryMap = new HashMap<String, List<ReportFunction>>();
			Map<String, Element> categoryMap = new HashMap<String, Element>();
			/*Element categoryESy = functionsE.addElement("category");
			categoryESy.addAttribute("name", "系统函数");
			categoryMap.put("系统函数", categoryESy);
			Element functionESv = categoryESy.addElement("function");
			functionESv.addAttribute("name", "sv");
			Element paraESv = functionESv.addElement("para");
			paraESv.setText("含有系统变量的字符串");
			Element returnDatatypeESv = functionESv.addElement("returnDatatype");
			returnDatatypeESv.setText("string");
			Element runAtESv = functionESv.addElement("runAt");
			runAtESv.setText("Local");*/
			
			for(Map<String, Object> function : functions){
				String code = function.get("code").toString();
				//String name = function.get("name").toString();
				Object category = function.get("category");
				if(category==null||"".equals(category.toString())){
					category = "其他函数";
				}
				if(!categoryMap.containsKey(category)){
					Element categoryE = functionsE.addElement("category");
					categoryE.addAttribute("name", category.toString());
					categoryMap.put(category.toString(), categoryE);
				}
				Element categoryE = categoryMap.get(category);
				Element functionE = categoryE.addElement("function");
				functionE.addAttribute("name", code);
				String params = function.get("params").toString();
				if(params!=null&&!"".equals(params)){
					String[] paramArr = params.split(",");
					for(String param : paramArr){
						Element para = functionE.addElement("para");
						para.setText(param);
					}
				}
				Element returnDatatype = functionE.addElement("returnDatatype");
				returnDatatype.setText(function.get("returnType")==null?"string":function.get("returnType").toString());
			}
			
			String funcXml = XMLUtil.xmltoString(document);
			//设置编码  
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/xml;charset=utf-8");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out = response.getWriter();  
			out.write(funcXml);  
			out.flush();  
			out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("/getXml")
	public String getXml(HttpServletRequest request,HttpServletResponse response){
		String xmlStr = null;
		try {
			String xmlPath = request.getParameter("xmlPath");
			if(StringUtils.isNotEmpty(xmlPath)){
	        	HttpSession session = request.getSession();
	        	String xmlFilePath = session.getServletContext().getRealPath("/report/"+xmlPath);
				File file = new File(xmlFilePath);
				Document document = XMLUtil.read(file);
				xmlStr = XMLUtil.xmltoString(document);
			}
			//设置编码  
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/xml;charset=utf-8");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out = response.getWriter();  
			out.write(xmlStr);  
			out.flush();  
			out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getDataXml")
	public String getDataXml(HttpServletRequest request,HttpServletResponse response){
		try {
			String dataType = request.getParameter("dataType");
			String sql = request.getParameter("sql");
			String xml = "";
			if(StringUtils.isNotEmpty(sql)){
				List<Map<String, Object>>datas = zqiDao.findAll(sql);
				if("item".equals(dataType)){
					xml = SupcanUtil.makeItemDataXml(datas);
				}else if("col".equals(dataType)){
					xml = SupcanUtil.makeColsXml(datas);
				}else{
					xml = SupcanUtil.makeDataXml(datas);
				}
			}
			//设置编码  
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/xml;charset=utf-8");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out = response.getWriter();  
			out.write(xml);  
			out.flush();  
			out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
