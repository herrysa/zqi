package com.zqi.frame.quartz;

import javax.servlet.http.HttpServlet;

import com.zqi.unit.SpringContextHelper;

public class QuartzServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9064592137639863112L;

	public void init(){
		QuartzManager quartzManager = (QuartzManager)SpringContextHelper.getBean("quartzManager");
		quartzManager.start();
	}
}
