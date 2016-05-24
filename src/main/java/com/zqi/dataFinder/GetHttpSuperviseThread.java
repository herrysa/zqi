package com.zqi.dataFinder;

import java.util.Map;

public class GetHttpSuperviseThread implements Runnable{

	String url;
	Map<String, String> rs;
	
	public GetHttpSuperviseThread(String url,Map<String, String> rs){
		this.url = url;
		this.rs = rs;
	}
	
	@Override
	public void run() {
//		while(rs){
//			
//		}
		
	}

}
