package com.huawei.sharedrive.app.statistics.job;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.statistics.dao.SysConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 节点定时统计任务，按天统计每日Node和对象数量和增量
 * 
 * @author l90003768
 * 
 */
@Component("concStatisticsJob")
public class ConcStatisticsJob extends QuartzJobTask
{
    /**
     * 
     */
    @Autowired
    private TempConcStatisticsDAO  tempConcStatisticsDAO;
    
    @Autowired
    private SysConcStatisticsDAO sysConcStatisticsDAO;
    
    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcStatisticsJob.class);
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        LOGGER.info("[statisticsLog] begin to statistics the node" + context.getJobDefinition());
        Calendar now = Calendar.getInstance();
        int day = StatisticsDateUtils.getDay(now);
        ConcStatisticsGather concStatisticsGather = new ConcStatisticsGather(tempConcStatisticsDAO,
            sysConcStatisticsDAO);
        concStatisticsGather.gatherTempObjectData(day);
        if(now.get(Calendar.HOUR) < 2)
        {
            concStatisticsGather.gatherTempObjectData(StatisticsDateUtils.getLastDay(now));
        }
    }

}
