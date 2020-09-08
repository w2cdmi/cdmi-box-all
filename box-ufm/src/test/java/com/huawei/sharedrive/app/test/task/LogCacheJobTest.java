package com.huawei.sharedrive.app.test.task;

import java.util.Date;
import java.util.Random;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.log.service.impl.LoggerCacheWriter;
import com.huawei.sharedrive.app.log.service.impl.WriteLogDbJob;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public class LogCacheJobTest
{
    

    
    public static final String TEST_CRON = "0 */2 * * * ?";
    
    
    public static void main(String[] args)  throws Exception
    {
//        startTimedTask();
        writeData();
    }
    
    public static void startTimedTask() throws Exception
    {
//        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); 
//        JobDetail jobDetail = JobBuilder.newJob(WriteLogDbJob.class).withIdentity("readerjob").build();
//        /*简单触发器，相当于java timetask，CronTrigger 是更为发展的触发器，有cron表达式*/      
//        Trigger trigger = TriggerBuilder.newTrigger()
//            .withIdentity("triger1").forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(TEST_CRON)).build();
//        scheduler.scheduleJob(jobDetail, trigger);  
//        scheduler.start();  
    }
    
    private static long writeCounts = 0;
    
    public static void writeData() throws Exception
    {
        Event event = null;
        long begin = System.currentTimeMillis();
        Random random = new Random();
        for(int i = 1 ;i <= Long.MAX_VALUE; i++)
        {
//            Thread.currentThread().sleep(2 * 1000);
            UserToken userToken = new UserToken();
            userToken.setAppId("i" + i);
            event = new Event(userToken);
            Thread.currentThread().sleep(random.nextInt(20) * 100);
            INode dest = new INode();
            dest.setOwnedBy(i);
            dest.setId((long)i);
            event.setDest(dest);
            event.setSource(dest);
            event.setCreatedAt(new Date());
            event.setCreatedBy(i);
            LoggerCacheWriter.writeLog(event);
            writeCounts++;
            if(i % 100 == 0)
            {
                System.out.println("current write data is " + writeCounts );
            }
        }
    }
    

    
}
