package com.zqi.strategy.period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zqi.unit.DateUtil;

public class PeriodFinder {

	public static List<String> getDayPeriod(String start,String end) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		List<String> periodList = new ArrayList<String>();
		for (Date date : lDate){
			String dateStr = sdf.format(date);
			periodList.add(dateStr);
		}
		return periodList;
	}
	
	public static List<Date> findDates(Date dBegin, Date dEnd){
		List<Date> lDate = new ArrayList<Date>();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	 }
	
	public static String findEndPeriod(String start, String end ,int length){
		try {
			Date startDate = DateUtil.convertStringToDate(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DAY_OF_MONTH, length);
			Date endDateTemp = calendar.getTime();
			Date endDate = DateUtil.convertStringToDate(end);
			if(endDate.compareTo(endDateTemp)>0){
				endDate = endDateTemp;
				end = DateUtil.convertDateToString(endDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return end;
	}
	
	public static Integer findPeriodIndex(String start, String end){
		try {
			Date startDate = DateUtil.convertStringToDate(start);
			Date endDate = DateUtil.convertStringToDate(end);
			Long millis = endDate.getTime() - startDate.getTime() ;
			double d = Math.floor(millis/86400000);
			return (int)d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String findBeforePeriod(String period){
		return findBeforePeriod(period,1);
	}
	
	public static String findBeforePeriod(String period,int days){
		String bPeriod = null;
		try {
			Date startDate = DateUtil.convertStringToDate(period);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, -days);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bPeriod;
	}
	
	public static void main(String[] args) {
		try {
			Date startDate = DateUtil.convertStringToDate("2016-01-01");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, -1);
			System.out.println(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
