/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.thrift.realTime.app2dc.RealTimeCopyTaskExeResult;
import com.huawei.sharedrive.thrift.realTime.app2dc.TBusinessException;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.thrift.DCRealTimeThriftServiceClient;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

public class RealTimeCallbackWorker implements Runnable
{
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    private RealTimeCopyTask task;
    
    private long realTimePartSize = SystemConfigContainer.getLong(SystemConfigKeys.REALTIME_DEFAULT_SPLIT_SIZE,
        RealTimeCopyTaskTool.DEFAULT_SPLIT_SIZE);
    
    public RealTimeCallbackWorker(RealTimeCopyTask task)
    {
        this.task = task;
    }
    
    private void initBean()
    {
        realTimeCopyTaskService = (RealTimeCopyTaskService) SpringContextUtil.getBean("realTimeCopyTaskService");
        ufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("ufmThriftClientProxyFactory");
        szufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("szufmThriftClientProxyFactory");
        dgufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("dgufmThriftClientProxyFactory");
    }
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        if (null == task)
        {
            RealTimePrinter.info("RealTime callback failed, the copytask is null");
            return;
        }
        
        initBean();
        
        reportTask(task);
    }
    
    private void reportTask(RealTimeCopyTask realTimeCopyTask)
    {
        try
        {
            reportSingleReault(realTimeCopyTask);
        }
        catch (Exception e)
        {
            RealTimePrinter.error("RealTimeCallbackWorker Job execute failed," + e.getMessage(), e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private void reportSingleReault(RealTimeCopyTask realTimeCopyTask)
    {
        RealTimeCopyTaskExeResult exeResult = new RealTimeCopyTaskExeResult();
        exeResult.setTaskId(realTimeCopyTask.getTaskId());
        exeResult.setSrcObjectId(realTimeCopyTask.getSrcObjectId());
        exeResult.setDestObjectId(realTimeCopyTask.getDestObjectId());
        exeResult.setSize(realTimeCopyTask.getNodeSize());
        exeResult.setMd5(realTimeCopyTask.getMd5());
        exeResult.setDataBlockMd5(realTimeCopyTask.getBlockMD5());
        exeResult.setResult(realTimeCopyTask.getErrorCode());
        
        try
        {
            DCRealTimeThriftServiceClient client = getClient(realTimeCopyTask, exeResult);
            client.reportCopyTaskExeResult(exeResult);
            
            RealTimePrinter.info("RealTime copy task reportCopyTaskExeResult succeed, task id is: "
                + exeResult.getTaskId());
        }
        catch (TBusinessException e)
        {
            
            RealTimePrinter.error("RealTimeCopyTaskCallbacker execute with TBusinessException, Code :"
                + e.getStatus() + " status :" + e.getMessage() + ", task id is "
                + realTimeCopyTask.getTaskId());
            
            // 设置error code
            realTimeCopyTask.setErrorCode(e.getStatus());
            
            if (RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode() == e.getStatus()
                || RealTimeCopyTaskError.TASK_ID_NOT_FOUND.getErrorCode() == e.getStatus())
            {
                realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.CALLBACK_SUCCESSED.getCode());
            }
            else
            {
                realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.CALLBACK_FAILED.getCode());
                realTimeCopyTask.setRetryNum(realTimeCopyTask.getRetryNum() + 1);
            }
            
            realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
            
            return;
        }
        catch (Exception e)
        {
            // 网络异常
            RealTimePrinter.error("RealTimeCopyTaskCallbacker job execute failed, may be network error, taskId is "
                + realTimeCopyTask.getTaskId() + e.getMessage(),
                e);
            
            realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.INTERNAL_SERVER_ERROR.getErrorCode());
            realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.CALLBACK_FAILED.getCode());
            realTimeCopyTask.setRetryNum(realTimeCopyTask.getRetryNum() + 1);
            realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
            
            return;
        }
        if (realTimeCopyTask.getNodeSize() > realTimePartSize
            && realTimeCopyTask.getErrorCode() == RealTimeCopyTaskError.FAIL_MANY_TIME.getErrorCode())
        {
            // 用于删除分片情况，因发生异常多次任务失败但fileobject未被删除的情况
            FileObject fileObject = fileObjectService.getFileObject(realTimeCopyTask.getDestObjectId());
            if (fileObject != null)
            {
                fileObjectService.deleteFileObject(fileObject);
            }
        }
        realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
        realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.CALLBACK_SUCCESSED.getCode());
        realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
        
    }
    
    public RealTimeCopyTask getTask()
    {
        return task;
    }
    
    public void setTask(RealTimeCopyTask task)
    {
        this.task = task;
    }
    
    private DCRealTimeThriftServiceClient getClient(RealTimeCopyTask realTimeCopyTask,
        RealTimeCopyTaskExeResult exeResult) throws TTransportException
    {
        DCRealTimeThriftServiceClient client = null;
        String uasRegionName = RealTimeCopyTaskTool.getUasRegionName(realTimeCopyTask);
        exeResult.setSrcObjectId(realTimeCopyTask.getSrcObjectId());
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                RealTimePrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.SZ_UAS_REGION_NAME);
                client = szufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                RealTimePrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.DG_UAS_REGION_NAME);
                client = dgufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
            default:
            {
                RealTimePrinter.info("The checkTokenAuthVaild uas region name is default");
                client = ufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
        }
        
        return client;
    }
}
