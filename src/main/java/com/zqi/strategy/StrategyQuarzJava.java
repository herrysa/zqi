package com.zqi.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.zqi.frame.util.PropertiesUtil;
import com.zqi.frame.util.TestTimer;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.period.PeriodTimer;
import com.zqi.strategy.period.PeriodTimerStrategyTask;
import com.zqi.strategy.period.PeriodTimerTask;
import com.zqi.unit.SpringContextHelper;

@Component("strategyQuarzJava")
@Scope("prototype")
public class StrategyQuarzJava implements IStrategyQuarz{

	private StrategyJava quant;
	private Account mainAccount;
	private StrategyContext strategyContext;
	private HQFinder hqFinder;
	private boolean needAccount;
	private Map<String, Object> custom;
	
	public void init(String quantCode){
		try {
			hqFinder = (HQFinder)SpringContextHelper.getBean("hQFinder");
			quant = (StrategyJava)SpringContextHelper.getBean(quantCode);
			String strategyPath = this .getClass().getResource( "" ).getPath();
			Gson gson = new Gson();
			Properties properties = PropertiesUtil.getProperties(strategyPath+"strategy.properties");
			String quantStr = properties.getProperty(quantCode);
			StrategyTitle quantTitle = gson.fromJson(quantStr, StrategyTitle.class);
			quant.setTitle(quantTitle);
			mainAccount = new Account(hqFinder);
			mainAccount.setQuantCode(quantCode);
			mainAccount.setQuantName(quantCode);
			strategyContext = new StrategyContext(hqFinder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setCustom(String custom) {
		if(StringUtils.isEmpty(custom)){
			strategyContext.setCustom(new HashMap<String, Object>());
		}else{
			Gson gson = new Gson();
			this.custom = gson.fromJson(custom, Map.class);
			if(this.custom!=null){
				strategyContext.setCustom(this.custom);
				quant.profit = (Double)strategyContext.getCustomParam("profit", quant.profit);
				quant.loss = (Double)strategyContext.getCustomParam("loss", quant.loss);
				quant.ps = (Double)strategyContext.getCustomParam("ps", quant.ps);
				quant.quantName = strategyContext.getCustomParam("quantName", quant.quantName).toString();
			}
		}
		
	}
	
	public void setAccountCode(String accountCode){
		mainAccount.setAccountCode(accountCode);
	}
	
	public StrategyJava run(){
		TestTimer tt = new TestTimer(quant.getTitle().getName());
		tt.begin();
		quant.init(strategyContext);
		Double capital = strategyContext.getCapital();
		if(capital!=null){
			mainAccount.setCapital_base(capital);
		}
		String quantName = quant.getQuantName();
		if(StringUtils.isNotEmpty(quantName)){
			mainAccount.setQuantName(quantName);
		}
		//needAccount = strategyContext.isNeedAccount();
		String start = strategyContext.getStart();
		String end = strategyContext.getEnd();
		String benchmark = strategyContext.getBenchmark();
		mainAccount.setBenchmark(benchmark);
		//mainAccount.setNeedBalance(needAccount);
		mainAccount.setStart(start);
		mainAccount.setEnd(end);
		mainAccount.prepare();
		
		GpPool codeGpPool = strategyContext.getGppool();
		Map<String, String> optionMap = strategyContext.getOptionMap();
		int interval = strategyContext.getInterval();
		StrategyOutProcesser outProcesser = strategyContext.getStrategyOutProcesser();
		PeriodTimer.TIMERTYPE timerType = strategyContext.getTimerType();
		PeriodTimer.PERIODTYPE periodType = strategyContext.getPeriodType();
		//boolean needAccount = strategyContext.isNeedAccount();
		HQDataHandler hqDataHandler = strategyContext.getHqDataHandler();
		hqDataHandler.setGpPool(codeGpPool);
		hqDataHandler.setHqFinder(hqFinder);
		hqDataHandler.setOptionMap(optionMap);
		//hqDataHandler = new HQDataHandler(codeGpPool , optionMap , hqFinder);
		//hqDataHandler.setDataMethods(strategyContext.getDataMethods());
		//hqDataHandler.setOutDateNum(strategyContext.getOutDateNum());
		/*if(strategyContext.isUseHQDataBox()){
			hqDataHandler.initHqDataBoxs();
		}*/
		System.out.println("策略：<"+quant.getTitle().getName()+">开始执行！ start:"+start+" end:"+end);
		try {
			
			PeriodTimerTask periodTimerTask = new PeriodTimerStrategyTask(quant, hqDataHandler, mainAccount, outProcesser);
			PeriodTimer periodTimer = new PeriodTimer(periodTimerTask,start, end, timerType, periodType, interval);
			if(timerType==PeriodTimer.TIMERTYPE.LSIT){
				List<String> codeList = new ArrayList<String>();
				Collections.addAll(codeList,codeGpPool.getCodeArr());
				periodTimer.setList(codeList);
			}
			periodTimer.timer();
			TestTimer ttA = new TestTimer("Account");
			ttA.begin();
			//PeriodTimerTask periodTimerAccountTask = new PeriodTimerAccountTask(mainAccount);
			//PeriodTimer periodTimerAccount = new PeriodTimer(periodTimerAccountTask,start, end, PeriodTimer.TIMERTYPE.PERIOD, PeriodTimer.PERIODTYPE.DAY, 1);
			//mainAccount.setDataBox(hqFinder.getHQDataBox(benchmark, optionMap));
			//mainAccount.balanceInit();
			//periodTimerAccount.timer();
			mainAccount.balanceAfter();
			//GpPool markGpPool = new GpPool(hqFinder);
			//markGpPool.add(benchmark);
			//markGpPool.setStart(start);
			//markGpPool.setEnd(end);
			//mainAccount.setMarkData(markGpPool.getGpHq().getDataMap());
			//mainAccount.balance();
			outProcesser.dealAccountOut(mainAccount);
			ttA.done();
			if(needAccount){
			}
			List<StrategyOut> outList = outProcesser.dealOut();
			quant.setOutList(outList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tt.done();
	return quant;
	}


	@Override
	public boolean isNeedAccount() {
		return needAccount;
	}

	@Override
	public void setNeedAccount(boolean needAccount) {
		this.needAccount = needAccount;
	}
}
