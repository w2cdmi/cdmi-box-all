package com.huawei.sharedrive.app.mirror.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("CopyTaskDeletor")
public class CopyTaskDeletor extends QuartzJobTask
{
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskDeletor.class);
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1)
    {
        List<Integer> list = copyTaskService.selectAllPolicyId();
        if(list.isEmpty())
        {
            LOGGER.info("get policyId from copytask table is null");
            return;
        }
        StringBuffer msg = new StringBuffer();
        for(Integer integer : list)
        {
            if(integer==null)
            {
                continue;
            }
            msg.append(integer.toString()).append(' ');
        }
        LOGGER.info("policyid size is:"+list.size()+",and they are :"+msg.toString());
        
        CopyPolicy copyPolicy = null;
        Integer delPolicyId = null;
        for(Integer integer : list)
        {
            if(integer==null || integer == -1)
            {
                continue;
            }
            copyPolicy=copyPolicyService.getCopyPolicy(integer);
            if(copyPolicy==null)
            {
                LOGGER.info("cannot get copypolicy with policyid:"+integer+",delete these task");
                delPolicyId=integer;
                break;
            }
        }
        if(null != delPolicyId)
        {
            LOGGER.info("start to delete copytask with policyid:"+delPolicyId);
            deleteTaskWithNoPolicyId(delPolicyId);
            LOGGER.info("delete action end");
        }
    }
    
    /**
     * 删除找不到策略id的任务
     */
    private void deleteTaskWithNoPolicyId(int policyId)
    {
        copyTaskService.deleteCopyTaskByPolicy(policyId);
    }
}
