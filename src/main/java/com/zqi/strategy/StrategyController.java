package com.zqi.strategy;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.github.abel533.echarts.AxisPointer;
import com.github.abel533.echarts.DataZoom;
import com.github.abel533.echarts.Grid;
import com.github.abel533.echarts.Legend;
import com.github.abel533.echarts.Tooltip;
import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitArea;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.DataZoomType;
import com.github.abel533.echarts.code.PointerType;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.github.abel533.echarts.style.TextStyle;
import com.google.gson.Gson;
import com.zqi.frame.controller.BaseController;
import com.zqi.frame.controller.filter.PropertyFilter;
import com.zqi.frame.controller.pagers.JQueryPager;
import com.zqi.frame.controller.pagers.PagerFactory;
import com.zqi.frame.util.DecimalUtil;
import com.zqi.frame.util.EhCacheUtils;
import com.zqi.frame.util.JedisUtils;
import com.zqi.frame.util.PropertiesUtil;
import com.zqi.frame.util.TestTimer;
import com.zqi.primaryData.fileDataBase.FileDataBase;
import com.zqi.strategy.period.PeriodFinder;
import com.zqi.unit.SpringContextHelper;

@Controller
@RequestMapping("/strategy")
public class StrategyController extends BaseController{

	
	@SuppressWarnings("unchecked")
	@RequestMapping("/strategyList")
	public String strategyList(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("gpCode");
		String period = request.getParameter("period");
		model.addAttribute("gpCode", code);
		model.put("period", period);
		List<Map<String, Object>> transCodes = zqiDao.findAll("SELECT accountCode FROM ac_account GROUP BY accountCode ORDER BY accountCode DESC");
		model.put("accountCodes",transCodes);
		return "strategy/strategyList";
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/strategyGridList")
	public Map<String, Object> strategyGridList(HttpServletRequest request){
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		List<StrategyTitle> strategies = new ArrayList<StrategyTitle>();
		String basePath = request.getSession().getServletContext().getRealPath("/");
		File parentFile = new File(basePath+"/strategy/loopbacktest");
		String[] files = parentFile.list();
		for(String fileName : files){
			String fileFullPath = basePath+"strategy/"+fileName;
			StrategyJsFactoy strategyFactoy = (StrategyJsFactoy)SpringContextHelper.getBean("strategyFactoy");
			IStrategy strategy = strategyFactoy.getStrategy(fileFullPath);
			strategies.add(strategy.getTitle());
		}
		Gson gson = new Gson();
		String strategyPath = this .getClass().getResource( "" ).getPath();
		Properties properties = PropertiesUtil.getProperties(strategyPath+"strategy.properties");
		String quantListStr = properties.getProperty("quantList");
		List<String> quantList = gson.fromJson(quantListStr, List.class);
		for(String quantCode : quantList){
			String quantStr = properties.getProperty(quantCode);
			StrategyTitle quant = gson.fromJson(quantStr, StrategyTitle.class);
			strategies.add(quant);
		}
		pagedRequests.setList(strategies);
		//pagedRequests = zqiDao.findWithFilter(pagedRequests, dayDataSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/strategyResult")
	public String strategyResult(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		String accountCode = request.getParameter("accountCode");
		String type = request.getParameter("type");
		String param = request.getParameter("param");
		model.put("code", code);
		IStrategyQuarz quarz = null;
		if("java".equals(type)){
			quarz = (IStrategyQuarz)SpringContextHelper.getBean("strategyQuarzJava");
			quarz.init(code);
			quarz.setCustom(param);
			quarz.setAccountCode(accountCode);
			Gson gson = new Gson();
			String strategyPath = this .getClass().getResource( "" ).getPath();
			Properties properties = PropertiesUtil.getProperties(strategyPath+"strategy.properties");
			String quantStr = properties.getProperty(code);
			StrategyTitle quantTitle = gson.fromJson(quantStr, StrategyTitle.class);
			quantTitle.setParam(param);
			quantStr = gson.toJson(quantTitle);
			PropertiesUtil.updateProperties(code, quantStr, strategyPath+"strategy.properties");
		}else{
			quarz = (IStrategyQuarz)SpringContextHelper.getBean("strategyQuarzJs");
			quarz.init("loopbacktest/"+code+".js");
		}
		
		IStrategy strategy = quarz.run();
		List<Object> categoryData = new ArrayList<Object>();
		List<String> indiLegendList = new ArrayList<String>();
		List<Series> indiSeries = new ArrayList<Series>();
		List<StrategyOut> outList = strategy.getOutList();
		
		Map<String, Object> tableMap = new HashMap<String, Object>();
		for(StrategyOut strategyOut :outList){
			String outNname = strategyOut.getName();
			StrategyOut.OUTTYPE outType = strategyOut.getType();
			List<Object> values = strategyOut.getValues();
			if(outType==StrategyOut.OUTTYPE.x){
				categoryData = values;
			}else if(outType==StrategyOut.OUTTYPE.table){
				Map<String, Object> tableObj = new HashMap<String, Object>();
				Map<String, Object> paramMap = strategyOut.getParamMap();
				Object tableCols = paramMap.get("cols");
				if(tableCols!=null){
					tableObj.put("tableCol",tableCols);
					//model.put("tableCol",tableCols);
				}else{
					if(values.size()>0){
						Map<String, Object> value = (Map<String, Object>)values.get(0);
						Set<String> colSet = value.keySet();
						//model.put("tableCol",colSet);
						tableObj.put("tableCol",colSet);
					}
				}
				//model.put("tableName",outNname);
				//model.put("tableData",tableJson);
				tableObj.put("tableData",values);
				tableMap.put(outNname,tableObj);
			}else if(outType==StrategyOut.OUTTYPE.line){
				String lineName = outNname;
				indiLegendList.add(lineName);
				Line line = new Line();
				line.setSmooth(true);
				line.setName(lineName);
				line.setData(values);
				indiSeries.add(line);
			}else if(outType==StrategyOut.OUTTYPE.bar){
				
			}else if(outType==StrategyOut.OUTTYPE.txt){
				String outTxt = "";
				if(!values.isEmpty()){
					outTxt = values.get(0).toString();
					model.put("wholeIndex",outTxt);
				}
			}
		}
		//model.put("account",quarz.isNeedAccount());
		model.put("account",true);
		if(!indiLegendList.isEmpty()){
			StrategyTitle title = strategy.getTitle();
			String name = title.getName();
			if(name==null){
				name = title.getCode();
			}
			GsonOption strategyOption = getKCahrtOption(name,indiLegendList,categoryData);
			strategyOption.series(indiSeries);
			model.put("chartName",name);
			model.put("strategyOption", strategyOption.toString());
		}
		Gson gson = new Gson();
		String tableJson = gson.toJson(tableMap);
		tableJson = tableJson.replaceAll("\"", "\\\"");
		model.put("table",tableJson);
		return "strategy/strategyResult";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/strategyForm")
	public String strategyForm(HttpServletRequest request,ModelMap model){
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		Gson gson = new Gson();
		String strategyPath = this .getClass().getResource( "" ).getPath();
		Properties properties = PropertiesUtil.getProperties(strategyPath+"strategy.properties");
		String quantStr = properties.getProperty(code);
		StrategyTitle quant = gson.fromJson(quantStr, StrategyTitle.class);
		String param = quant.getParam();
		Map<String, Object> paramMap = gson.fromJson(param, Map.class);
		paramMap.put("code",code);
		paramMap.put("type",type);
		model.put("paramMap", paramMap);
		List<Map<String, Object>> transCodes = zqiDao.findAll("SELECT accountCode FROM ac_account GROUP BY accountCode ORDER BY accountCode DESC");
		model.put("accountCodes",transCodes);
		
		return "strategy/strategyForm";
	}
	
	private String listMapToStr(List<Object> values){
		String str = "[";
		for(Object o : values){
			JSONObject jsonObject = JSONObject.fromObject(o);
			str += jsonObject.toString()+",";
		}
		if(!"[".equals(str)){
			str = str.substring(0,str.length()-1);
		}
		str += "]";
		return str;
	}
	private GsonOption getKCahrtOption(String name,List<String> legendList,List<Object> categoryList){
		GsonOption option = new GsonOption();
		
		option.title().text(name).x(X.left).left(0).setTextStyle(new TextStyle().fontSize(14));;
		
		Tooltip tooltip = option.tooltip();
		AxisPointer axisPointer = tooltip.axisPointer();
		axisPointer.setType(PointerType.line);
		tooltip.setAxisPointer(axisPointer);
		tooltip.setTrigger(Trigger.axis);
		option.setTooltip(tooltip);
		
		Legend legend = option.legend();
		legend.setData(legendList);
		
		Grid grid = option.grid();
		grid.left("30").right("30").bottom("65");
		
		option.yAxis(new ValueAxis().scale(true).splitArea(new SplitArea().show(true)));
		
		CategoryAxis categoryAxis = new CategoryAxis()
		.scale(true)
		.boundaryGap(false)
		.min("dataMin")
		.max("dataMax")
        .splitLine(new SplitLine().show(false))
        .axisLine(new AxisLine().onZero(false));
		
		categoryAxis.setData(categoryList);
		
		option.xAxis(categoryAxis);
		
		List<DataZoom> dataZooms = new ArrayList<DataZoom>();
		DataZoom dataZoomInside = new DataZoom();
		dataZoomInside.setType(DataZoomType.inside);
		dataZoomInside.start(50);
		dataZoomInside.end(100);
		dataZooms.add(dataZoomInside);
		DataZoom dataZoomSlider = new DataZoom();
		dataZoomSlider.show(true);
		dataZoomSlider.setType(DataZoomType.slider);
		dataZoomSlider.y("90%");
		dataZoomSlider.start(50);
		dataZoomSlider.end(100);
		dataZooms.add(dataZoomSlider);
		
		option.setDataZoom(dataZooms);
		return option;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cacheHQData")
	public Map<String, Object> cacheHQData(HttpServletRequest request){
		TestTimer testTimer = new TestTimer("mysqlall");
		testTimer.begin();
		List<Object> dics = zqiDao.findAll("SELECT * from d_gpdic where type in (0,1) order by code asc");
		dics.addAll(zqiDao.findAll("SELECT * from d_gpdic where code in ('0000001','0000300','1399001','1399005','1399006') order by code asc"));
		int i = 0;
		for(Object gpObj : dics){
			Map<String, Object> gp = (Map<String, Object>)gpObj;
			String code = gp.get("code").toString();
			String daytable = gp.get("daytable").toString();
			List<Map<String, Object>> lists = zqiDao.findAll("SELECT * from 2013_"+daytable+" where code='"+code+"' order by period asc");
			Map<String, Object> perData = null;
			for(Map<String, Object> data : lists){
				String period = data.get("period").toString();
				/*BigDecimal changepercent = (BigDecimal)data.get("changepercent");
				if(){
					
				}*/
				if(perData==null){
					perData = data;
				}else{
					data.put("prePeriod", perData.get("period"));
					perData = data;
				}
				EhCacheUtils.put(code+":"+period, data);
				i++;
				/*String code = data.get("code").toString();
				String period = data.get("period").toString();
				JedisUtils.setObject(code+":"+period, data, 0);*/
			}
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/cacheHQData1")
	public Map<String, Object> cacheHQData1(HttpServletRequest request){
		//String code = request.getParameter("gpCode");
		//JedisUtils.set("ppp", "111", 0);
		TestTimer testTimer = new TestTimer("mysqlall");
		testTimer.begin();
		//JedisUtils.flushdb();
		List<Object> dics = new ArrayList<Object>();//zqiDao.findAll("SELECT * from d_gpdic where type in (0,1) order by code asc");
		//JedisUtils.setObjectList("gpDic", dics, 0);
		//dics.addAll(zqiDao.findAll("SELECT * from d_gpdic where code in ('0000001','0000300','1399001','1399005','1399006') order by code asc"));
		Jedis jedis = JedisUtils.getResource();
		Pipeline pipeline = jedis.pipelined();
		
		Map<String, Object> RHQYears = zqiDao.findFirst("SELECT * from _log where id='RHQYears'");
		String years = RHQYears.get("info")==null?null:RHQYears.get("info").toString();
		String[] yearArr = years.split(",");
		List<String> periodList = new ArrayList<String>();
		for(String year : yearArr){
			List<Map<String, Object>> szlists = zqiDao.findAll("SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_daytable0_1 where code='0000001' order by period asc");
			for(Map<String, Object> data : szlists){
				//String code = gp.get("code").toString();
				String period = data.get("period").toString();
				JedisUtils.setObject(pipeline, "0000001:"+period, data, 0);
				periodList.add(period);
			}
		}
		
		for(Object gpObj : dics){
			Map<String, Object> gp = (Map<String, Object>)gpObj;
			String code = gp.get("code").toString();
			String daytable = gp.get("daytable").toString();
			for(String year : yearArr){
				List<Map<String, Object>> lists = zqiDao.findAll("SELECT period,settlement,open,high,low,close,volume,amount,changeprice,changepercent,isNew,isFh from "+year+"_"+daytable+" where code='"+code+"' order by period asc");
				
				for(int i=0;i< periodList.size();i++){
					String date = periodList.get(i);
					Map<String, Object> data = lists.get(i);
					String codePeriod = data.get("period").toString();
					if(date.compareTo(codePeriod)>0){
						
					}
				}
			}
			Map<String, Object> perData = null;
			/*for(Map<String, Object> data : lists){
				String period = data.get("period").toString();
				BigDecimal changepercent = (BigDecimal)data.get("changepercent");
				if(){
					
				}
				if(perData==null){
					perData = data;
				}else{
					data.put("prePeriod", perData.get("period"));
					perData = data;
				}
				JedisUtils.setObject(pipeline, code+":"+period, data, 0);
				String code = data.get("code").toString();
				String period = data.get("period").toString();
				JedisUtils.setObject(code+":"+period, data, 0);
			}*/
		}
		List<Map<String, Object>> fhList = zqiDao.findAll("SELECT * from i_gpfh where fhYear not like '%(预*)'");
		for(Map<String, Object> fhMap : fhList){
			String code = fhMap.get("code").toString();
			Object fhObj = fhMap.get("fh");
			Object zzObj = fhMap.get("zz");
			Object sgObj = fhMap.get("sg");
			
			if(fhObj!=null){
				String cqDate = fhMap.get("cqDate").toString();
				BigDecimal fh = (BigDecimal)fhObj;
				JedisUtils.setObject(pipeline,"fh:"+code+":"+cqDate, fh, 0);
			}
			
			if(zzObj!=null){
				String cqDate = fhMap.get("zzdz").toString();
				BigDecimal zz = (BigDecimal)zzObj;
				JedisUtils.setObject(pipeline,"zz:"+code+":"+cqDate, zz, 0);
			}

			if(sgObj!=null){
				String cqDate = fhMap.get("sgdz").toString();
				BigDecimal sg = (BigDecimal)sgObj;
				JedisUtils.setObject(pipeline,"sg:"+code+":"+cqDate, sg, 0);
			}
		}
		pipeline.sync();
		JedisUtils.returnResource(jedis);
		testTimer.done();
		/*HQRData hqrData = new HQRData();
		hqrData.setClose(0d);
		hqrData.setCode("111");
		hqrData.setName("fff");
		testTimer = new TestTimer("mysql");
		Map<String, Object> data = null;
		testTimer.begin();
		data = zqiDao.findFirst("SELECT * from daytable0_1 where code='2015-01-05' and period ='0000001'");
		testTimer.done();
		JedisUtils.setObject("ppp", hqrData, 0);
		testTimer = new TestTimer("redis");
		testTimer.begin();
		hqrData = (HQRData)JedisUtils.getObject("ppp");
		testTimer.done();
		System.out.println(hqrData.getCode());*/
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/transList")
	public String transList(HttpServletRequest request,ModelMap model){
		List<Map<String, Object>> transCodes = zqiDao.findAll("SELECT transCode FROM i_jgd GROUP BY transCode ORDER BY transCode DESC");
		model.put("transCodes",transCodes);
		return "strategy/transList";
	}
	
	@ResponseBody
	@RequestMapping("/transGridList")
	public Map<String, Object> transGridList(HttpServletRequest request){
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		String dataSql = "select * from i_trans where 1=1 ";
		pagedRequests = zqiDao.findWithFilter(pagedRequests, dataSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/parseTrans")
	public Map<String, Object> parseTrans(HttpServletRequest request){
		String accountCode = request.getParameter("accountCode");
		List<Map<String, Object>> transList = zqiDao.findAll("select * from ac_jgd where accountCode='"+accountCode+"'");
		Map<String, Map<String , Object>> transMap = new HashMap<String, Map<String,Object>>();
		//List<String> sqlList = new ArrayList<String>();
		FileDataBase transDb = new FileDataBase("temp/trans");
		transDb.deleteDataBase();
		StringBuilder transBuilder = new StringBuilder();
		for(Map<String, Object> jgd : transList){
			String tradeCode = jgd.get("tradeCode").toString();
			String tradeCode2 = jgd.get("tradeCode2").toString();
			String code = jgd.get("code").toString();
			String period = jgd.get("period").toString();
			BigDecimal price = (BigDecimal)jgd.get("price");
			Integer amount = (Integer)jgd.get("amount");
			BigDecimal money = (BigDecimal)jgd.get("money");
			BigDecimal cash = (BigDecimal)jgd.get("cash");
			BigDecimal cost = (BigDecimal)jgd.get("cost");
			if(amount<0){
				Map<String , Object> trans = transMap.get(tradeCode2);
				Integer pamount = (Integer)trans.get("amount");
				Double pprice = (Double)trans.get("price");
				Double pcash = (Double)trans.get("cash");
				Double pmoney = (Double)trans.get("money");
				Double pcost = (Double)trans.get("cost");
				String pperiod = trans.get("period").toString();
				//String sql = null;
				//String cols = "(tradeCode,transCode,code,period,price,amount,money,cost,speriod,sprice,sremainder,smoney,scash,scost,profit,days)";
				String values = null;
				Double sremainder = DecimalUtil.scale(pamount+amount);
				double profit = DecimalUtil.percent(-amount*price.doubleValue()-cost.doubleValue(),pamount*pprice+pcost);
				int days = PeriodFinder.findPeriodIndex(pperiod, period);
				//values = "('"+tradeCode+"','"+transCode+"','"+code+"','"+pperiod+"','"+pprice+"','"+pamount+"','"+pmoney+"','"+pcost+"','"+period+"','"+price+"','"+sremainder+"','"+money+"','"+cash+"','"+cost+"','"+profit+"','"+days+"');";
				values = tradeCode+"	"+accountCode+"	"+code+"	"+pperiod+"	"+pprice+"	"+pamount+"	"+pmoney+"	"+pcost+"	"+period+"	"+price+"	"+sremainder+"	"+money+"	"+cash+"	"+cost+"	"+profit+"	"+days+"\n";
				transBuilder.append(values);
				//sql = "insert into i_trans "+cols+" values "+values;
				//sqlList.add(sql);
				transMap.remove(tradeCode);
			}else{
				Map<String , Object> trans = new HashMap<String, Object>();
				trans.put("tradeCode",tradeCode);
				trans.put("code",code);
				trans.put("period",period);
				trans.put("price",price.doubleValue());
				trans.put("amount",amount);
				trans.put("money",money.doubleValue());
				trans.put("cash",cash.doubleValue());
				trans.put("cost",cost.doubleValue());
				transMap.put(tradeCode , trans);
			}
		}
		transDb.writeStr("trans", transBuilder.toString(), 0);
		String cols = "tradeCode,accountCode,code,period,price,amount,money,cost,speriod,sprice,sremainder,smoney,scash,scost,profit,days";
		String loadDataSql = "load data infile '"+transDb.getFilePath("trans")+"' into table ac_trans("+cols+");";
		zqiDao.excute(loadDataSql);
		//String[] sqls = new String[sqlList.size()];
		//sqls = sqlList.toArray(sqls);
		//zqiDao.bathUpdate(sqls);
		setMessage("调仓分析成功！");
		return resultMap;
	}
	
	@RequestMapping("/transParse")
	public String transParse(HttpServletRequest request,ModelMap model){
		return "strategy/transParse";
	}
	
	@ResponseBody
	@RequestMapping("/delTransData")
	public Map<String, Object> delTransData(HttpServletRequest request){
		String transCode = request.getParameter("transCode");
		zqiDao.excute("delete from i_trans where transCode='"+transCode+"'");
		setMessage("删除调仓信息成功！");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/delAccountData")
	public Map<String, Object> delAccountData(HttpServletRequest request){
		String accountCodeTemp = request.getParameter("accountCode");
		String[] accountCodeArr = accountCodeTemp.split(",");
		for(String accountCode : accountCodeArr){
			zqiDao.excute("delete from ac_account where accountCode='"+accountCode+"'");
			zqiDao.excute("delete from ac_jgd where accountCode='"+accountCode+"'");
			zqiDao.excute("delete from ac_position where accountCode='"+accountCode+"'");
			zqiDao.excute("delete from ac_profit where accountCode='"+accountCode+"'");
			zqiDao.excute("delete from ac_trans where accountCode='"+accountCode+"'");
		}
		setMessage("删除全部回测信息成功！");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping("/delJgdData")
	public Map<String, Object> delJgdData(HttpServletRequest request){
		String transCode = request.getParameter("transCode");
		zqiDao.excute("delete from i_jgd where transCode='"+transCode+"'");
		setMessage("删除交易信息成功！");
		return resultMap;
	}
	
	
	@RequestMapping("/hisStrategyList")
	public String hisStrategyList(HttpServletRequest request,ModelMap model){
		return "strategy/hisStrategyList";
	}
	
	@ResponseBody
	@RequestMapping("/hisStrategyGridList")
	public Map<String, Object> hisStrategyList(HttpServletRequest request){
		
		List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(request);
		JQueryPager pagedRequests = null;
		pagedRequests = (JQueryPager) pagerFactory.getPager(
				PagerFactory.JQUERYTYPE, request);
		String dataSql = "select * from ac_account where 1=1 ";
		pagedRequests = zqiDao.findWithFilter(pagedRequests, dataSql, filters);
		resultMap.put("page", pagedRequests.getPageNumber());
		resultMap.put("records", pagedRequests.getTotalNumberOfRows());
		resultMap.put("rows", pagedRequests.getList());
		resultMap.put("total", pagedRequests.getTotalNumberOfPages());
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/hisStrategy")
	public String hisStrategy(HttpServletRequest request,ModelMap model){
		String accountCode = request.getParameter("accountCode");
		Map<String, Object> account = zqiDao.findFirst("select * from ac_account where accountCode='"+accountCode+"'");
		if(account!=null){
			String snapCode = account.get("snapCode").toString();
			String quantCode = account.get("quantCode").toString();
			String benchmark = account.get("benchmark").toString();
			String benchmarkName = account.get("benchmarkName").toString();
			Long baseCapital = (Long)account.get("baseCapital");
			BigDecimal markBase = (BigDecimal)account.get("markBase");
			List<Object> categoryData = new ArrayList<Object>();
			List<String> indiLegendList = new ArrayList<String>();
			List<Series> indiSeries = new ArrayList<Series>();
			String strategyPath = this .getClass().getResource( "" ).getPath();
			Gson gson = new Gson();
			Properties properties = PropertiesUtil.getProperties(strategyPath+"strategy.properties");
			String quantStr = properties.getProperty(quantCode);
			StrategyTitle quantTitle = gson.fromJson(quantStr, StrategyTitle.class);
			String quantName = quantTitle.getName();
			if(quantName==null){
				quantName = quantTitle.getCode();
			}
			
			List<Map<String, Object>> markProfitList = zqiDao.findAll("select * from ac_profit where accountCode='"+benchmark+snapCode+"' order by period asc");
			indiLegendList.add(benchmarkName);
			Line markLine = new Line();
			markLine.setSmooth(true);
			markLine.setName(benchmarkName);
			List<Object> markprofitValues = new ArrayList<Object>();
			for(Map<String, Object> profit : markProfitList){
				BigDecimal cash = (BigDecimal)profit.get("cash");
				Double profitPercent = DecimalUtil.percent(cash.doubleValue(), markBase.doubleValue());
				markprofitValues.add(profitPercent);
			}
			markLine.setData(markprofitValues);
			indiSeries.add(markLine);
			
			List<Map<String, Object>> profitList = zqiDao.findAll("select * from ac_profit where accountCode='"+accountCode+"' order by period asc");
			indiLegendList.add(quantName);
			Line line = new Line();
			line.setSmooth(true);
			line.setName(quantName);
			List<Object> profitValues = new ArrayList<Object>();
			for(Map<String, Object> profit : profitList){
				String period = profit.get("period").toString();
				BigDecimal cap = (BigDecimal)profit.get("cap");
				BigDecimal cash = (BigDecimal)profit.get("cash");
				Double profitPercent = DecimalUtil.percent(cap.doubleValue()+cash.doubleValue(), baseCapital.doubleValue());
				categoryData.add(period);
				profitValues.add(profitPercent);
			}
			line.setData(profitValues);
			indiSeries.add(line);
			
			
			GsonOption strategyOption = getKCahrtOption(quantName,indiLegendList,categoryData);
			strategyOption.series(indiSeries);
			model.put("chartName",quantName);
			model.put("strategyOption", strategyOption.toString());
			model.put("account",true);
		}
		
		
		return "strategy/strategyResult";
	}
	@ResponseBody
	@RequestMapping("/saveRemark")
	public Map<String, Object> saveRemark(HttpServletRequest request){
		String id = request.getParameter("id");
		String remark = request.getParameter("mycell");
		zqiDao.excute("update ac_account set remark='"+remark+"' where accountCode='"+id+"'");
		return resultMap;
	}
}
