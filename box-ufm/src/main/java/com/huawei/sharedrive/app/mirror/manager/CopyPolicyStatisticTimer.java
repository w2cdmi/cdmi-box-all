package com.huawei.sharedrive.app.mirror.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.manager.statistic.CopyPolicyStatistics;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.MethodLogAble;


/**
 * 复制策略统计定时器，定时创建统计任务
 * 
 * @author c00287749
 * 
 */
@Component("copyPolicyStatisticTimer")
public class CopyPolicyStatisticTimer extends QuartzJobTask
{
   
    private static final long serialVersionUID = 2248453542593864601L;

    @Autowired
    private CopyPolicyService copyPolicyService;
    
    @Autowired
    private CopyPolicyStatistics copyPolicyStatistics;
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyStatisticTimer.class);
    
    @MethodLogAble
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            if(!copyPolicyStatistics.isMirrorPolicyStatisticEnable())
            {
                LOGGER.error("MirrorPolicyStatisticEnable is false,not enable statistic.");
                return;
            }
            
            //清理超时任务，为了避免用户数据过大，因此在任务统计执行是，需要不停的更新时间，详细见执行任务部分。
            copyPolicyStatistics.clearTimeOutSystemTaskForCopyPolicy();
            
            
            List<CopyPolicy> lstPolicy = copyPolicyService.listCopyPolicy();
            
            for(CopyPolicy policy :lstPolicy)
            {
                copyPolicyStatistics.createSystemTaskForCopyPolicy(policy);
            }
            
            record.setSuccess(true);
   
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(),e);
        }
    }
    
}
