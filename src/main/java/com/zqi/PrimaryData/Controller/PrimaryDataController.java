package com.zqi.PrimaryData.Controller;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jdesktop.jdic.browser.WebBrowser;
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
	
	public String findBlockInfo(){
		try {
			Parser parser = new Parser( (HttpURLConnection) (new URL("http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0")).openConnection() );
			NodeFilter frameNodeFilter = new NodeFilter() {  
                @Override  
                public boolean accept(Node node) {  
                    if (node.getText().startsWith("div")) {  
                        return true;  
                    } else {  
                        return false;  
                    }  
                }  
            };
            NodeList nodeList = parser.extractAllNodesThatMatch(frameNodeFilter); 
            for(int i = 0; i<nodeList.size();i++){  
                Node node = nodeList.elementAt(i);  
            	System.out.println(node.getText());
            }
			
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static void main(String[] args) {
		 WebBrowser browser = new WebBrowser();
	        try {
				browser.setURL(new URL("http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        JFrame frame = new JFrame("Browser Test");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(browser);
	        frame.pack();
	        frame.setSize(500,500);
	        frame.setVisible(true);
//		JFrame frame;  
//	    JPanel panel_name=new JPanel();  
//		WebBrowser webBrowser = new WebBrowser();
//		panel_name.add(webBrowser, BorderLayout.CENTER); 
//		frame = new JFrame("Browser Test");  
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
//        frame.getContentPane().add(webBrowser);  
//        frame.pack();  
//        frame.setSize(900,500);  
//        frame.setLocation((int)(100*Math.random()), (int)(100*Math.random()));  
//        frame.setVisible(true);
//		try {
//			webBrowser.setURL(new URL("https://www.baidu.com"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String jscript =   "function getAllHtml() {"+  
//				"var a='';" +  
//				"a = '<html><head><title>';" +  
//				"a += document.title;"+       
//				"a += '</title></head>';"+  
//				"a += document.body.outerHTML;"+  
//				"a += '</html>';"+  
//				"return a;"+  
//				"}"+  
//				"getAllHtml();"; 
//		String result = webBrowser.executeScript(jscript);  
//		System.out.println(result);
//		try {
////			URL realUrl = new URL("http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0");
////			HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
////			connection.setRequestProperty("accept", "*/*");
////			connection.setRequestProperty("connection", "Keep-Alive");
////			connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
////			Parser parser = new Parser( connection );
////			parser.setEncoding("GB2312");
////			NodeFilter innerFilter = new TagNameFilter ("table");
////			NodeIterator ni = parser.elements();
////			while(ni.hasMoreNodes()){
////				Node node = ni.nextNode();
////				System.out.println(node.toHtml());
////			}
////			NodeFilter filter = new HasAttributeFilter( "id", "bklist" );
////            NodeList nodeList = parser.extractAllNodesThatMatch(innerFilter); 
////            for(int i = 0; i<nodeList.size();i++){  
////                Node node = nodeList.elementAt(i);
////                NodeList childrenNodeList = node.getChildren();
////                System.out.println(node.toHtml());
////                for(int c = 0; c<childrenNodeList.size();c++){ 
////                	Node childNode = childrenNodeList.elementAt(c);
////                	System.out.println(childNode.getText());
////                }
////            }
//			
//		} catch (ParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		/*Document doc;
		try {
			doc = Jsoup.connect("http://quote.hexun.com/default.htm#stock").timeout(0).get();
			
			Element el = doc.getElementById("listview");
			System.out.println(el.html());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
