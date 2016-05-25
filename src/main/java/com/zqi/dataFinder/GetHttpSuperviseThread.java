package com.zqi.dataFinder;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class GetHttpSuperviseThread implements Runnable{

	String url;
	Map<String, Object> context;
	
	public GetHttpSuperviseThread(Map<String, Object> context){
		this.url = context.get("url").toString();
		this.context = context;
	}
	
	@Override
	public void run() {
//		while(rs){
//			
//		}
		if(context.get("result")==null){
			String count = context.get("count").toString();
			int c = Integer.parseInt(count);
			Thread thread = (Thread)context.get("thread");
			if(c==0){
				thread = null;
				thread = new Thread(new GetHttpThread(context));
				System.out.println("super0:"+thread.getId());
				context.put("thread",thread);
				thread.start();
			}else if(c>=5){
				thread = null;
				thread = new Thread(new GetHttpThread(context));
				System.out.println("super5:"+thread.getId());
				context.put("thread",thread);
				thread.start();
			}
			c++;
			context.put("count",c);
		}else{
			ScheduledExecutorService schedule = (ScheduledExecutorService)context.get("schedule");
			schedule.shutdownNow();
		}
	}

	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {
			int i=0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.err.println(i);
				i++;
			}
		});
		t1.start();
		t1.start();
	}
}
