package com.zqi.frame.quartz;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.zqi.frame.util.XMLUtil;

@Service("quartzManager")
public class QuartzManager {

	private SchedulerFactoryBean schedulerFactoryBean;
	private Scheduler scheduler ;
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
			scheduler = schedulerFactoryBean.getScheduler();
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
	
	/**
     * 检查调度是否启动
     * @return
     * @throws SchedulerException
     */
	public  boolean isStarted() throws SchedulerException
	{
		return scheduler.isStarted();
	}

    /**
     * 关闭调度信息
     * @throws SchedulerException
     */
	public  void shutdown() throws SchedulerException	{
		scheduler.shutdown();
	}
    /**
     * 添加调度的job信息
     * @param jobdetail
     * @param trigger
     * @return
     * @throws SchedulerException
     */
	public  Date scheduleJob(JobDetail jobdetail, Trigger trigger)
			throws SchedulerException{
				return scheduler.scheduleJob(jobdetail, trigger); 
	}
    /**
     * 添加相关的触发器
     * @param trigger
     * @return
     * @throws SchedulerException
     */
	public  Date scheduleJob(Trigger trigger) throws SchedulerException{
		return scheduler.scheduleJob(trigger);
	}
	 /**
	  * 添加多个job任务
	  * @param triggersAndJobs
	  * @param replace
	  * @throws SchedulerException
	  */
	/* public  void scheduleJobs(Map<JobDetail, Set<Trigger>> triggersAndJobs, boolean replace) throws SchedulerException
	 {
		scheduler.scheduleJobs(triggersAndJobs, replace);
	}*/
    /**
     * 停止调度Job任务
     * @param triggerkey
     * @return
     * @throws SchedulerException
     */
	public  boolean unscheduleJob(TriggerKey triggerkey)
			throws SchedulerException{
		return scheduler.unscheduleJob(triggerkey);
	}

	/**
	 * 停止调度多个触发器相关的job
	 * @param list
	 * @return
	 * @throws SchedulerException
	 */
	public  boolean unscheduleJobs(List<TriggerKey> triggerKeylist) throws SchedulerException{
		return scheduler.unscheduleJobs(triggerKeylist);
	}
	/**
	 * 重新恢复触发器相关的job任务 
	 * @param triggerkey
	 * @param trigger
	 * @return
	 * @throws SchedulerException
	 */
	public  Date rescheduleJob(TriggerKey triggerkey, Trigger trigger)
	throws SchedulerException{
		return scheduler.rescheduleJob(triggerkey, trigger);
	}
	/**
	 * 添加相关的job任务
	 * @param jobdetail
	 * @param flag
	 * @throws SchedulerException
	 */
	public  void addJob(JobDetail jobdetail, boolean flag)
			throws SchedulerException	{
		scheduler.addJob(jobdetail, flag);
	}

	/**
	 * 删除相关的job任务
	 * @param jobkey
	 * @return
	 * @throws SchedulerException
	 */
	public  boolean deleteJob(JobKey jobkey) throws SchedulerException{
		return scheduler.deleteJob(jobkey);
	}

	/**
	 * 删除相关的多个job任务
	 * @param jobKeys
	 * @return
	 * @throws SchedulerException
	 */
	public     boolean deleteJobs(List<JobKey> jobKeys)
    throws SchedulerException{
		return scheduler.deleteJobs(jobKeys);
	}
    /**
     * 
     * @param jobkey
     * @throws SchedulerException
     */
	public  void triggerJob(JobKey jobkey) throws SchedulerException	{
		scheduler.triggerJob(jobkey);
	}
    /**
     * 
     * @param jobkey
     * @param jobdatamap
     * @throws SchedulerException
     */
	public  void triggerJob(JobKey jobkey, JobDataMap jobdatamap)
			throws SchedulerException	{
		scheduler.triggerJob(jobkey, jobdatamap);
	}
    /**
     * 停止一个job任务
     * @param jobkey
     * @throws SchedulerException
     */
	public  void pauseJob(ScheduleJob job) throws SchedulerException	{
		JobKey jobKey2 = JobKey.jobKey(job.getJobName(), job.getJobGroup());
		scheduler.pauseJob(jobKey2);
	}
    /**
     * 停止多个job任务
     * @param groupmatcher
     * @throws SchedulerException
     */
	public  void pauseJobs(GroupMatcher<JobKey> groupmatcher)
			throws SchedulerException	{
		scheduler.pauseJobs(groupmatcher);
	}
    /**
     * 停止使用相关的触发器
     * @param triggerkey
     * @throws SchedulerException
     */
	public  void pauseTrigger(TriggerKey triggerkey)
			throws SchedulerException	{
		scheduler.pauseTrigger(triggerkey);
	}

	public  void pauseTriggers(GroupMatcher<TriggerKey> groupmatcher)
			throws SchedulerException	{
		scheduler.pauseTriggers(groupmatcher);
	}
    /**
     * 恢复相关的job任务
     * @param jobkey
     * @throws SchedulerException
     */
	public  void resumeJob(JobKey jobkey) throws SchedulerException	{
		scheduler.pauseJob(jobkey);
	}
    
	public  void resumeJobs(GroupMatcher<JobKey> matcher)
			throws SchedulerException	{
		scheduler.resumeJobs(matcher);
	}

	public  void resumeTrigger(TriggerKey triggerkey)
			throws SchedulerException	{
		scheduler.resumeTrigger(triggerkey);
	}
   
	public  void resumeTriggers(GroupMatcher<TriggerKey>  groupmatcher)
			throws SchedulerException
	{
		scheduler.resumeTriggers(groupmatcher);	
	}
    /**
     * 暂停调度中所有的job任务
     * @throws SchedulerException
     */
	public  void pauseAll() throws SchedulerException
	{
		scheduler.pauseAll();
	}
    /**
     * 恢复调度中所有的job的任务
     * @throws SchedulerException
     */
	public  void resumeAll() throws SchedulerException
	{
		scheduler.resumeAll();
	}
	
	public static void main(String[] args) {
		QuartzManager q = new QuartzManager();
		String aa = q.getClass().getResource("/").getPath();
		System.out.println(aa);
	}
}
