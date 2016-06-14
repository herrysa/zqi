package com.zqi.frame.quartz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.zqi.frame.dao.impl.ZqiDao;
import com.zqi.frame.util.XMLUtil;

@Service("quartzManager")
public class QuartzManager {

	private SchedulerFactoryBean schedulerFactoryBean;
	private ZqiDao zqiDao;
	public SchedulerFactoryBean getSchedulerFactoryBean() {
		return schedulerFactoryBean;
	}
	@Autowired
	public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
		this.schedulerFactoryBean = schedulerFactoryBean;
	}
	
	public void start(){
		
		try {
			//schedulerFactoryBean 由spring创建注入
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			//这里获取任务信息数据
			List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
			String classPath = this.getClass().getResource("/").getPath();
			File jobXml = new File(classPath+"job.xml");
			Document jobDoc = XMLUtil.read(jobXml, "UTF-8");
			Element root = jobDoc.getRootElement();
			List<Element> jobs = root.elements("JOB");
			for(Element jobElement : jobs){
				Element jobId = jobElement.element("JOBID");
				Element jobName = jobElement.element("JOBNAME");
				Element jobGroup = jobElement.element("JOBGROUP");
				Element cronExpression = jobElement.element("JOBCRON");
				Element desc = jobElement.element("JOBDESC");
				
				Element jobClass = jobElement.element("JOBCLASS");
				Element jobMethod = jobElement.element("JOBMETHOD");
				Element param1 = jobElement.element("PARAM1");
				Element param2 = jobElement.element("PARAM2");
				
				ScheduleJob jobTemp = new ScheduleJob();
				jobTemp.setJobId(jobId.getText());
				jobTemp.setJobName(jobName.getText());
				jobTemp.setJobGroup(jobGroup.getText());
				jobTemp.setCronExpression(cronExpression.getText());
				jobTemp.setDesc(desc.getText());
				jobTemp.setJobClass(jobClass.getText());
				jobTemp.setParam1(param1.getText());
				jobTemp.setParam2(param2.getText());
				jobList.add(jobTemp);
			}
			for (ScheduleJob job : jobList) {
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
				//获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
				CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
				//不存在，创建一个
				if (null == trigger) {
					JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
							.withIdentity(job.getJobName(), job.getJobGroup()).build();
					jobDetail.getJobDataMap().put("scheduleJob", job);
					//表达式调度构建器
					CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
							.getCronExpression());
					//按新的cronExpression表达式构建一个新的trigger
					trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
					scheduler.scheduleJob(jobDetail, trigger);
				} else {
					// Trigger已存在，那么更新相应的定时设置
					//表达式调度构建器
					CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
							.getCronExpression());
					//按新的cronExpression表达式重新构建trigger
					trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
							.withSchedule(scheduleBuilder).build();
					//按新的trigger重新设置job执行
					scheduler.rescheduleJob(triggerKey, trigger);
				}
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		QuartzManager q = new QuartzManager();
		String aa = q.getClass().getResource("/").getPath();
		System.out.println(aa);
	}
}
