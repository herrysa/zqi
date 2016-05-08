package com.zqi.charts.echarts;

import java.util.Map;

public class Title {

	public Boolean show;
	public Integer zlevel;
	public Integer z;
	public String text;			//主标题文本，'\n'指定换行
	public String link;			//主标题文本超链接
	public String target;		//指定窗口打开主标题超链接，支持'self' | 'blank'，不指定等同为'blank'（新窗口）
	public String subtext;		//副标题文本，'\n'指定换行
	public String sublink;
	public String subtarget;
	public String x;
	public String y;
	public String textAlign;
	public String backgroundColor;
	public String borderColor;
	public Integer borderWidth;
	public Integer[] padding;
	public Integer itemGap;
	public Map textStyle;
	public Map subtextStyle;
}
