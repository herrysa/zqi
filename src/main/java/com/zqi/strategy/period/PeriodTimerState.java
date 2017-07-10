package com.zqi.strategy.period;

import java.util.Date;
import java.util.Map;

import com.zqi.unit.DateUtil;

public class PeriodTimerState {

	private Integer year;
	private Date now;
	private String period;
	private boolean first;
	private boolean last;
	
	private Map<String, Object> param;
	
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
		this.period = DateUtil.convertDateToString(now);
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
}
