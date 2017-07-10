package com.zqi.strategy;

import java.util.HashMap;
import java.util.Map;

import com.zqi.strategy.period.PeriodTimer;

public class StrategyContext {

	//private HQFinder hqFinder;
	
	private String start = "2016-01-01";
	private String end = "2016-03-01";
	private boolean exSuspended = false;
	private boolean exNew = false;
	private Double capital;
	private Map<String, String> optionMap = new HashMap<String, String>();
	private Map<String, Object> custom ;
	
	//private HQFinder.CACHETYPE cacheType = HQFinder.CACHETYPE.PERIOD;
	private PeriodTimer.TIMERTYPE timerType = PeriodTimer.TIMERTYPE.PERIOD;
	private PeriodTimer.PERIODTYPE periodType = PeriodTimer.PERIODTYPE.DAY;
	private int interval = 1;

	private String benchmark = "0000300";
	//private boolean needAccount= false;
	/*private boolean useHQDataBox = false;
	public boolean isUseHQDataBox() {
		return useHQDataBox;
	}

	public void setUseHQDataBox(boolean useHQDataBox) {
		this.useHQDataBox = useHQDataBox;
	}*/

	//private List<StrategyConstant.DATATYPE> dataTypes = new ArrayList<StrategyConstant.DATATYPE>();
	/*private List<DataMethod> dataMethods = new ArrayList<DataMethod>();
	private int outDateNum = 0;

	public int getOutDateNum() {
		return outDateNum;
	}

	public void setOutDateNum(int outDateNum) {
		this.outDateNum = outDateNum;
	}*/

	private GpPool gppool;
	private StrategyOutProcesser strategyOutProcesser;
	private HQDataHandler hqDataHandler;
	
	//private List<HQDataBox> hqDataBoxs;

	public StrategyContext(HQFinder hqFinder){
		//this.hqFinder = hqFinder;
		this.strategyOutProcesser = new StrategyOutProcesser();
		this.gppool = new GpPool(hqFinder);
		//this.gppool.setStart(start);
		//this.gppool.setEnd(end);
		this.optionMap.put("strat",start);
		this.optionMap.put("end",end);
		this.hqDataHandler = new HQDataHandler();
	}
	
	public GpPool getGppool() {
		return gppool;
	}
	/*public void setGppool(GpPool gppool) {
		this.gppool = gppool;
	}*/
	/*public boolean isNeedAccount() {
		return needAccount;
	}*/
	public void setNeedAccount(boolean needAccount) {
		//this.needAccount = needAccount;
	/*	String outStr = "{xData:'x',trans:'trans',posi:'posi','收益率':'yield','沪深300':'myield'}";
		Gson gson = new Gson();
		Map<String, String> wholeOut = gson.fromJson(outStr, Map.class);
		if(wholeOut==null){
			wholeOut = new HashMap<String, String>();
		}
		Set<String> wholeKeySet = wholeOut.keySet();
		for(String wholeKey : wholeKeySet){
			StrategyOut wholeStrategyOut = new StrategyOut();
			wholeStrategyOut.setName(wholeKey);
			Object paramObj = wholeOut.get(wholeKey);
			String wholeType = null;
			if(paramObj instanceof Map){
				Map<String, Object> paramMap = (Map<String, Object>)paramObj;
				wholeType = paramMap.get("type").toString();
				Boolean accu = (Boolean)paramMap.get("accu");
				wholeStrategyOut.setParamMap(paramMap);
				if(accu!=null){
					wholeStrategyOut.setAccu(accu);
				}
			}else{
				wholeType = paramObj.toString();
			}
			wholeStrategyOut.setType(StrategyOut.OUTTYPE.valueOf(wholeType));
			strategyOutProcesser.addOut(wholeKey, wholeStrategyOut);
		}*/
	}
	
	public String getStart() {
		return start;
	}
	public StrategyContext setStart(String start) {
		this.start = start;
		this.optionMap.put("strat",start);
		return this;
	}
	public String getEnd() {
		return end;
	}
	public StrategyContext setEnd(String end) {
		this.end = end;
		this.optionMap.put("end",end);
		return this;
	}

	public boolean isExSuspended() {
		return exSuspended;
	}

	public StrategyContext setExSuspended(boolean exSuspended) {
		this.exSuspended = exSuspended;
		if(exSuspended){
			this.optionMap.put("exSuspended","1");
		}else{
			this.optionMap.put("exSuspended","0");
		}
		return this;
	}

	public boolean isExNew() {
		return exNew;
	}

	public Double getCapital() {
		return capital;
	}

	public void setCapital(Double capital) {
		this.capital = capital;
	}
	
	public StrategyContext setExNew(boolean exNew) {
		this.exNew = exNew;
		if(exNew){
			this.optionMap.put("exNew","1");
		}else{
			this.optionMap.put("exNew","0");
		}
		return this;
	}
	
	public Map<String, String> getOptionMap() {
		return optionMap;
	}

	
	public Object getCustomParam(String key,Object defaultValue) {
		if(custom.containsKey(key)){
			return custom.get(key);
		}else{
			return defaultValue;
		}
	}

	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
		if(this.custom.containsKey("start")){
			this.start = custom.get("start").toString();
		}
		if(this.custom.containsKey("end")){
			this.end = custom.get("end").toString();
		}
		if(this.custom.containsKey("end")){
			this.end = custom.get("end").toString();
		}
	}
	/*public void setOptionMap(Map<String, String> optionMap) {
		this.optionMap = optionMap;
	}*/
	
	/*public HQFinder.CACHETYPE getCacheType() {
		return cacheType;
	}

	public void setCacheType(HQFinder.CACHETYPE cacheType) {
		this.cacheType = cacheType;
	}*/
	public PeriodTimer.TIMERTYPE getTimerType() {
		return timerType;
	}

	public void setTimerType(PeriodTimer.TIMERTYPE timerType) {
		this.timerType = timerType;
	}
	public PeriodTimer.PERIODTYPE getPeriodType() {
		return periodType;
	}
	public void setPeriodType(PeriodTimer.PERIODTYPE periodType) {
		this.periodType = periodType;
	}
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getBenchmark() {
		return benchmark;
	}
	public void setBenchmark(String benchmark) {
		this.benchmark = benchmark;
	}
	public StrategyOutProcesser getStrategyOutProcesser() {
		return strategyOutProcesser;
	}

	public HQDataHandler getHqDataHandler() {
		return hqDataHandler;
	}

	/*public void setStrategyOutProcesser(StrategyOutProcesser strategyOutProcesser) {
		this.strategyOutProcesser = strategyOutProcesser;
	}*/
	

	/*public void setHqDataBoxs(List<HQDataBox> hqDataBoxs) {
		this.hqDataBoxs = hqDataBoxs;
	}*/
}
