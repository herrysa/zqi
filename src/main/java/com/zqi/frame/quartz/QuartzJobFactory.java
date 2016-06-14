package com.zqi.frame.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zqi.unit.SpringContextHelper;

/**
 * 定时任务运行工厂类
 * 
 * User: liyd
 * Date: 14-1-3
 * Time: 上午10:11
 */
public class QuartzJobFactory implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("任务成功运行");
        ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
        System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]");
        String className = scheduleJob.getJobClass();
        Zjob zjob = (Zjob)SpringContextHelper.getBean(className);
        zjob.execute();
    }
}
