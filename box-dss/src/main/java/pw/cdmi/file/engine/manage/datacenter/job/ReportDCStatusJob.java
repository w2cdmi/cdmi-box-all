/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.file.engine.manage.datacenter.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 上报DC状态的任务<br>
 * 为集群任务，每个时间点只有一个节点执行该任务
 * 
 * @author s90006125
 * 
 */
@Service("reportDCStatusJob")
public class ReportDCStatusJob extends QuartzJobTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDCStatusJob.class);
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Start Report DC Status.");
        }
    }
    
}
