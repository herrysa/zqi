package com.zqi.strategy.period;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zqi.unit.DateUtil;

public class PeriodTimer {
	
	public enum TIMERTYPE {
		PERIOD,LSIT,NONE
	}
	
	public enum PERIODTYPE {  
		DAY(86400000) , HOUER(3600000) , MINUTE(60000) , SECOND(1000);
		
		private int periodNum;
		
		public int getPeriodNum() {
			return periodNum;
		}

		public void setPeriodNum(int periodNum) {
			this.periodNum = periodNum;
		}

		private PERIODTYPE(int periodNum){
			this.periodNum = periodNum;
		}
	}
	
	private PeriodTimerTask periodTimerTask;
	private PeriodTimerState periodTimerState;

	private String start;
	private String end;
	private TIMERTYPE timerType;
	private PERIODTYPE periodType;
	
	private Long startL;
	private Long endL;
	private int interval;
	
	private List<String> list;
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public PeriodTimer(PeriodTimerTask periodTimerTask , String start , String end , TIMERTYPE timerType , PeriodTimer.PERIODTYPE periodType ,int interval){
		this.periodTimerTask = periodTimerTask;
		this.start = start;
		this.end = end;
		this.timerType = timerType;
		this.periodType = periodType;
		this.interval = interval;
		this.periodTimerState = new PeriodTimerState();
		try {
			if(this.start.length()==10){
				this.start += " 00:00:00";
			}
			if(this.end.length()==10){
				this.end += " 00:00:00";
			}
			Date startDate = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",this.start);
			Date endDate = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",this.end);
			
			startL = startDate.getTime();
			endL = endDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void timer(){
		switch (timerType) {
		case LSIT:
			listTimer();
			break;
		case PERIOD:
			peirodTimer();
			break;
		case NONE:
			periodTimerTask.run(periodTimerState);
			break;
		default:
			break;
		}
	}
	
	public void peirodTimer(){
		periodTimerState.setFirst(true);
		Calendar calendar = Calendar.getInstance();
		while(true){
			if(startL>endL){
				break;
			}
			calendar.setTimeInMillis(startL);
			startL += periodType.getPeriodNum()*interval;
			boolean isWeekend = isWeekend(calendar);
			if(isWeekend){
				continue;
			}
			Date now = calendar.getTime();
			periodTimerState.setYear(calendar.get(Calendar.YEAR));
			periodTimerState.setNow(now);
			periodTimerTask.run(periodTimerState);
			//quant.strategy(hqDataBoxs, account,outProcesser,hqFinder);
			if(periodTimerState.isFirst()){
				periodTimerState.setFirst(false);
			}
			if((startL+periodType.getPeriodNum()*interval)>=endL){
				periodTimerState.setLast(true);
			}
		}
	}
	
	public void listTimer(){
		periodTimerState.setFirst(true);
		while(true){
			if(list==null||list.isEmpty()){
				break;
			}else{
				String period = list.get(0);
				periodTimerState.setPeriod(period);
				periodTimerTask.run(periodTimerState);
				list.remove(0);
				if(periodTimerState.isFirst()){
					periodTimerState.setFirst(false);
				}
				if(list.size()==1){
					periodTimerState.setLast(true);
				}
			}
		}
	}
	
	/*public void timerStrategy(StrategyJava quant,List<HQDataBox> hqDataBoxs,Account account,StrategyOutProcesser outProcesser ,HQFinder hqFinder){
		Calendar calendar = Calendar.getInstance();
		while(true){
			if(startL>=endL){
				break;
			}
			calendar.setTimeInMillis(startL);
			Date now = calendar.getTime();
			account.setNow(now);
			
			quant.strategy(hqDataBoxs, account,outProcesser,hqFinder);
			startL += periodType.getPeriodNum()*interval;
		}
	}*/
	
	/**
	 * 判断是否是周末
	 * @return
	 */
	private boolean isWeekend(Calendar cal){
		int week=cal.get(Calendar.DAY_OF_WEEK)-1;
		if(week ==6 || week==0){//0代表周日，6代表周六
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.MINUTE, 40);
		calendar.set(calendar.SECOND, 0);
		calendar.set(calendar.MILLISECOND, 0);
		Long s = calendar.getTimeInMillis();
		System.out.println(s);
		calendar.set(calendar.MINUTE, 41);
		Long e = calendar.getTimeInMillis();
		
		System.out.println(s-e);
		
	}
}
