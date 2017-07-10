package com.zqi.strategy.quant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.zqi.frame.exception.ZqiException;
import com.zqi.frame.util.DecimalUtil;
import com.zqi.frame.util.TestTimer;
import com.zqi.strategy.HQDataHandler;
import com.zqi.strategy.StrategyContext;
import com.zqi.strategy.StrategyJava;
import com.zqi.strategy.StrategyOutProcesser;
import com.zqi.strategy.account.Account;
import com.zqi.strategy.account.AccountPosition;
import com.zqi.strategy.period.PeriodTimerState;

@Component("tRevert")
public class TRevert extends StrategyJava {
	
	Double back = 30d;
	@Override
	public void init(StrategyContext context) {
		
		//context.setStart("2016-01-01").setEnd("2016-12-31");
		context.getGppool().addAll();
		
		back = (Double)context.getCustomParam("back", back);
		
		context.setNeedAccount(true);
		//context.getHqDataHandler().addDataMethod("ema","{col:['close_p'],value:[5]}");
		context.getHqDataHandler().addDataMethod("limit","{col:['close'],value:[30]}");
		
		//Map<String, Object> paramMap = new HashMap<String, Object>();
		//paramMap.put("cols", "日期,代码");
		//context.getStrategyOutProcesser().addAccuOut(outName, "table").setParamMap(paramMap);
		
	}

	@Override
	public void strategy(HQDataHandler hqDataHandler, Account account,StrategyOutProcesser outProcesser, PeriodTimerState periodTimerState) {
		String period = periodTimerState.getPeriod();
		TestTimer testTimer = new TestTimer(period);
		testTimer.begin();
		
		//TestTimer testTimerP = new TestTimer("sell");
		//testTimerP.begin();
		List<AccountPosition> positions = account.getPosition();
		for(AccountPosition accountPosition : positions){
			String code = accountPosition.getCode();
			Double price = accountPosition.getClose();
			Double yield = accountPosition.getYield();
			if(yield!=null&&(yield>=profit||yield<=loss)){
				account.order(period, code, -1, price, "");
			}
		}
		//testTimerP.done();
		TestTimer testTimer2 = new TestTimer("r");
		testTimer2.begin();
		List<Map<String, Object>> dataList = hqDataHandler.getHqDataList(period);
		testTimer2.done();
		//Collection<HQDataBox> hqDataBoxs = hqDataHandler.getHqDataBoxs(period);
		int j =0 ;
		for (int i = 0; i < dataList.size(); i++) {
		/*Iterator<HQDataBox> hqDataBoxIt= hqDataBoxs.iterator();
		int i = 0;
		while(hqDataBoxIt.hasNext()){*/
			//HQDataBox hqDataBox = hqDataBoxIt.next();
			//TestTimer testTimer2 = new TestTimer("r");
			//testTimer2.begin();
			Map<String, Object> hqDataMap = dataList.get(i);//hqDataBox.getRHQData();
			//testTimer2.done();
			if(hqDataMap==null||hqDataMap.isEmpty()){
				continue;
			}
			String code = hqDataMap.get("code").toString();
			BigDecimal open = (BigDecimal)hqDataMap.get("open");
			BigDecimal close = (BigDecimal)hqDataMap.get("close");
			BigDecimal high = (BigDecimal)hqDataMap.get("high");
			BigDecimal low = (BigDecimal)hqDataMap.get("low");
			BigDecimal changepercent = (BigDecimal)hqDataMap.get("changepercent");
			if(close==null||close.compareTo(new BigDecimal(0))==0){
				continue;
			}
			try {
				int change_1 = changepercent.compareTo(new BigDecimal(1));
				int h_l = DecimalUtil.comparePercent(high.doubleValue(), low.doubleValue(), 5);
				//int h_l = high.subtract(low).divide(low,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(5));
				int o_c = DecimalUtil.compareAbsPercent(open.doubleValue(), close.doubleValue(), 1);// open.subtract(close).divide(close,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).abs().compareTo(new BigDecimal(1));
				int h_c = DecimalUtil.compareAbsPercent(high.doubleValue(), close.doubleValue(), 1);//high.subtract(close).divide(close,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).abs().compareTo(new BigDecimal(1));
				//Map<String, Object> t = new HashMap<String, Object>();
				if(h_l>0&&o_c<0&&h_c<0&&change_1<0){
					j++;
					//t.put("日期", period);
					//t.put("代码", hqDataBox.getDataName());
					//outProcesser.putValue(outName, t);
					TestTimer testTimerb = new TestTimer("limit");
					testTimerb.begin();
					hqDataMap =  hqDataHandler.getIndexData("limit",code, period);
					//testTimerb.done();
					Object max30 = null;
					if(hqDataMap!=null){
						max30 = hqDataMap.get("close_max_30");
					}
					if(max30!=null){
						Double max30d = (Double)max30;
						//BigDecimal bmax30 = new BigDecimal(max30);
						int max30_20 = DecimalUtil.comparePercent(max30d, close.doubleValue(), back);
						//int max30_20 = bmax30.subtract(close).divide(close,10,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).abs().compareTo(new BigDecimal(30));
						if(max30_20>=0){
							account.order(period, code, ps, close.doubleValue(), "");
							//testTimerb.done();
						}
					}
				}
			} catch (ZqiException e) {
				//e.printStackTrace();
				System.out.println(""+e.getMessage());
			}
			
		}
		System.out.println(j);
		testTimer.done();
	}

}
