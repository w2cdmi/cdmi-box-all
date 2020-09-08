package com.huawei.sharedrive.app.files.service.job;

import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.files.dao.WaitingDeleteObjectDAO;
import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 定时删除对象
 * 
 * @author c00110381
 * 
 */
@Component("deleteObjectTimingJob")
public class DeleteObjectTimingJob extends QuartzJobTask
{
    /**
     * 控制并发锁
     */
    private static boolean bTaskRun = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObjectTimingJob.class);
    
    /** 分页查询的每页结果数 */
    private static final int MAX_LENGTH = 1000;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Autowired
    private WaitingDeleteObjectDAO waitingDeleteObjectDAO;
    
    private static boolean isbTaskRun()
    {
        return bTaskRun;
    }
    
    private synchronized static void setbTaskRun(boolean bTaskRun)
    {
        DeleteObjectTimingJob.bTaskRun = bTaskRun;
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        if (isbTaskRun())
        {
            LOGGER.info("DeleteObjectTimingJob Job is run,thread id: " + Thread.currentThread().getId()
                + ",thread name" + Thread.currentThread().getName());
            return;
        }
        
        setbTaskRun(true);
        try
        {
            jobRun();
        }
        catch (Exception e)
        {
            String message = "delete object failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
        finally
        {
            setbTaskRun(false);
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private void deleteWaitingDeleteObject(WaitingDeleteObject object)
    {
        waitingDeleteObjectDAO.delete(object);
    }
    
    private List<WaitingDeleteObject> getWaitingDeleteObject(Limit limit)
    {
        return waitingDeleteObjectDAO.listWaitingDeleteObject(limit);
    }
    
    /**
     * Job执行
     */
    private void jobRun()
    {
        LOGGER.info("DeleteObjectTimingJob begin ");
        Limit limit = new Limit();
        long offset = 0L;
        limit.setLength(MAX_LENGTH);
        limit.setOffset(offset);
        
        List<WaitingDeleteObject> lstObject = null;
        FileObjectThriftServiceClient client = null;
        ResourceGroup group = null;
        String domain = null;
        for (;;)
        {
            lstObject = getWaitingDeleteObject(limit);
            // 如果查询结果为空，则返回
            if (null == lstObject || lstObject.isEmpty())
            {
                break;
            }
            for (WaitingDeleteObject object : lstObject)
            {
                // 负载均衡选择DC
                group = dcManager.getCacheResourceGroup(object.getResourceGroupId());
                domain = dssDomainService.getDomainByDssId(group);
                try
                {
                    client = new FileObjectThriftServiceClient(domain, group.getManagePort());
                    if (client.deleteFileObject(object.getObjectId()))
                    {
                        LOGGER.info("delete succeed,objectid" + object.getObjectId());
                        deleteWaitingDeleteObject(object);
                    }
                    else
                    {
                        LOGGER.warn("failed,delete objectid:" + object.getObjectId());
                    }
                }
                catch (TException e)
                {
                    LOGGER.warn("failed,delete objectid:" + object.getObjectId());
                    LOGGER.warn(e.getMessage(), e);
                }
                finally
                {
                    if (null != client)
                    {
                        client.close();
                    }
                }
            }
            
            // 如果所有的查询结果都处理,则退出任务
            if (lstObject.size() < MAX_LENGTH)
            {
                break;
            }
            
            // 否则设定偏移进行下次查询
            limit.setOffset(limit.getOffset() + limit.getLength());
            
        }
        LOGGER.info("DeleteObjectTimingJob end ");
    }
    
}
