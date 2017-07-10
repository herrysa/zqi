package com.zqi.strategy.quant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zqi.frame.exception.ZqiException;
import com.zqi.frame.util.TestTimer;
import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.QuantContext;
import com.zqi.strategy.StrategyContext;
import com.zqi.strategy.StrategyJava;
import com.zqi.strategy.StrategyOutProcesser;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.filter.F_KDownBreak;
import com.zqi.strategy.filter.F_KUpBreak;
import com.zqi.strategy.filter.QFilter;
import com.zqi.strategy.period.PeriodTimerState;
import com.zqi.strategy.signal.QSignal;
import com.zqi.strategy.signal.ma.S_MACross;

@Component("ema")
@Scope("prototype")
public class EMA extends StrategyJava {
	
	//List<Map<String, Object>> dataListTemp = new ArrayList<Map<String,Object>>();
	Map<String, List<Map<String, Object>>> dataMapTemp = new HashMap<String, List<Map<String,Object>>>();
	
	@Override
	public void init(StrategyContext context) {
		
		context.getGppool().addAll().findCodeArr();
		
		//context.setQuantName("ema.c>10>60");
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void strategy(HQDataHandler hqDataHandler, Account account,StrategyOutProcesser outProcesser, PeriodTimerState periodTimerState) {
		String period = periodTimerState.getPeriod();
		TestTimer testTimer = new TestTimer(period);
		testTimer.begin();
		
		try {
			//卖出策略
			//profitSell(account,period ,-1d);
		
			//数据库取数
			//List<Map<String, Object>> dataList = hqDataHandler.getHqDataList(period);
			List<Map<String, Object>> dataList = hqDataHandler.getAllHqDataList(period);
			if(dataList==null){
				return ;
			}
			account.updatePosition(dataList);
			//String[] codeArr = hqDataHandler.getGpPool().getCodeArr();
			
			Set<Map<String, Object>> dataSet = dealDataList(dataList);
			
			QuantContext quantContext  = account.getQuantContext();
			
			//Set<Map<String, Object>> gzSet = (Set<Map<String, Object>>)quantContext.getContext("gzSet");
			
			QSignal qSignal = new S_MACross(hqDataHandler,quantContext,10,30);
			qSignal.signal(dataSet);
			
			
			Set<Map<String, Object>> buySet = qSignal.getBuySet();
			Set<Map<String, Object>> sellSet = qSignal.getSellSet();
			
			/*for(Map<String, Object> t : dataSet){
				for(Map<String, Object> tt :buySet){
					String tCode = t.get("code").toString();
					String ttCode = tt.get("code").toString();
					if(ttCode.equals(tCode)){
						
					}
				}
			}*/

			quantContext.addNumToContext("upcrossSize", buySet.size());
			quantContext.addNumToContext("downcrossSize", sellSet.size());
			
			/*if(gzSet!=null){
				buySet.addAll(gzSet);
			}*/
			
		/*	QFilter kupBreak = new F_KUpBreak(hqDataHandler, quantContext, 30);
			buySet = kupBreak.filter(buySet);
			quantContext.addNumToContext("upcrossAndkupBreakSize", buySet.size());
			
			QFilter kdownBreak = new F_KDownBreak(hqDataHandler, quantContext, 60);
			sellSet.addAll(kdownBreak.filter(dataSet));
			quantContext.addNumToContext("upcrossAndkdownBreakSize", sellSet.size());*/
			
			if(periodTimerState.isLast()){
				System.out.println("upcrossSize:"+quantContext.getContext("upcrossSize"));
				System.out.println("upcrossAndkupBreakSize:"+quantContext.getContext("upcrossAndkupBreakSize"));
				System.out.println("downcrossSize:"+quantContext.getContext("downcrossSize"));
				System.out.println("upcrossAndkdownBreakSize:"+quantContext.getContext("upcrossAndkdownBreakSize"));
			}
			
			/*for(Map<String,Object> sellData : sellSet){
				String code = sellData.get("code").toString();
				Double close = (Double)sellData.get("close");
				account.order(period, code, -1, close.doubleValue(), "");
				AccountPosition accountPosition = account.getPosition(code);
				if(accountPosition!=null){
					accountPosition.setClose(close);
					Double yield = accountPosition.getYield();
					if(yield<0){
						Integer sf = (Integer)periodTimerState.getContext(code+"_sf");
						if(sf!=null){
							if(sf==1){
								periodTimerState.setContext(code+"_s", 5);
								periodTimerState.setContext(code+"_sf", 2);
							}else if(sf==2){
								periodTimerState.setContext(code+"_s", 8);
								periodTimerState.setContext(code+"_sf", 3);
							}else{
								periodTimerState.setContext(code+"_s", -1);
							}
						}else{
							periodTimerState.setContext(code+"_s", 2);
							periodTimerState.setContext(code+"_sf", 1);
						}
					}
				}
			}
			
			for(Map<String,Object> buyData : buySet){
				String code = buyData.get("code").toString();
				Double close = (Double)buyData.get("close");
				Integer s = (Integer)periodTimerState.getContext(code+"_s");
				if(s==null||s==0){
					account.order(period, code, ps, close.doubleValue(), "");
				}else if(s>0){
					s--;
					periodTimerState.setContext(code+"_s", s);
				}
			}*/
			
			this.sell(account, period, sellSet);
			this.buy(account, period, buySet);
			
			/*List<AccountPosition> positions = account.getPosition();
			for(AccountPosition accountPosition : positions){
				String code = accountPosition.getCode();
				Double price = accountPosition.getClose();
				Double yield = accountPosition.getYield();
				if(yield!=null&&yield<=loss){
					boolean suc = account.order(period, code, -1, price, "");
				}
			}*/
				/*List<Map<String, Object>> codeList = dataMapTemp.get(code);
				if(codeList==null){
					codeList = new ArrayList<Map<String,Object>>();
					dataMapTemp.put(code, codeList);
				}else{
					if(codeList.size()>=5){
						codeList.remove(0);
					}
				}
				codeList.add(hqDataMap);*/
				
				/*if(hqDataMap==null){
					continue;
				}*/
				
				
				
				/*if("002533".equals(code)&&"2015-06-30".equals(period)){
					System.out.println();
				}*/
				
					
							//Map<String, Object> aa = dataListTemp.get(i-5);
							//String p = PeriodFinder.findBeforePeriod(period, 5);
							//List<Map<String, Object>> aaList = hqDataHandler.beforeDatas(code, period, 5);
							/*Map<String , Object> pre5Data = codeList.get(0);
							Double pre5_ema_10 = (Double)pre5Data.get("close_ema_10");
							Double pre5_ema_60 = (Double)pre5Data.get("close_ema_60");
							if(pre5_ema_10!=null&&pre5_ema_10<=close_eam_1&&pre5_ema_60!=null&&pre5_ema_60<=close_eam_2){
								boolean suc = account.order(period, code, ps, close.doubleValue(), "");
								if(suc){
									periodTimerState.setContext(code, "1");
								}
								
							}*/
		} catch (ZqiException e) {
			//e.printStackTrace();
			System.out.println(""+e.getMessage());
		}
		//System.out.println(j);
		testTimer.done();
	}

}
