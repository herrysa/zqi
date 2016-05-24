package com.zqi.dataFinder;

import java.util.Map;

import com.zqi.frame.util.Tools;

public class GetHttpThread implements Runnable{
	
	String url;
	Map<String, String> rs;
	
	public GetHttpThread(String url,Map<String, String> rs){
		this.url = url;
		this.rs = rs;
	}

	@Override
	public void run() {
		String result = Tools.getByHttpUrl(url);
		rs.put("result", result);
	}

}
