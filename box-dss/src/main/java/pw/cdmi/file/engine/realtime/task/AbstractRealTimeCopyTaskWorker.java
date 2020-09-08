/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.thrift.realTime.app2dc.ObjectDownloadURL;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.thrift.exception.ThriftPoolException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskException;
import pw.cdmi.file.engine.realtime.httpclient.RealTimeCopyHttpClient;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskCallbacker;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskPartService;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.thrift.DCRealTimeThriftServiceClient;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

public abstract class AbstractRealTimeCopyTaskWorker implements RealTimeCopyTaskWorker
{
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private RealTimeCopyTaskCallbacker realTimeCopyTaskCallbacker;
    
    @Autowired
    protected RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Autowired
    protected RealTimeCopyTaskPartService realTimeCopyTaskPartService;
    
    @Autowired
    protected RealTimeCopyHttpClient realTimeClientService;
    
    protected RealTimeCopyTask realTimeCopyTask;
    
    @Autowired
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        
        if (this.realTimeCopyTask == null)
        {
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG
                + "The Copy task is null,currnet task thread stoped");
            return;
        }
        
        this.executeTask();
        
    }
    
    private void executeTask()
    {
        try
        {
            RealTimePrinter.info("excute the copy task by thread with id : " + realTimeCopyTask.getTaskId()
                + " srcObjectId:" + realTimeCopyTask.getSrcObjectId() + "now date:"
                + System.currentTimeMillis());
            this.beforeRealTimeCopyTask();
            
            boolean result = this.executeRealTimeCopyTask();
            
            RealTimePrinter.info("excute end the copy task by thread with id : "
                + realTimeCopyTask.getTaskId() + " srcObjectId:" + realTimeCopyTask.getSrcObjectId()
                + "now date:" + System.currentTimeMillis());
            if (result)
            {
                this.successRealTimeCopyTask();
            }
            
        }
        catch (ThriftPoolException e)
        {
            
            this.failedRealTimeCopyTask();
            
            RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG
                + "The real time copy task thirft client init failed," + e.getMessage());
            
        }
        catch (Exception e)
        {
            this.failedRealTimeCopyTask();
            
            String message = "RealTimeCopyTaskWorker execute Failed. taskid ["
                + this.realTimeCopyTask.getTaskId() + " ] ,objectId:["
                + this.realTimeCopyTask.getSrcObjectId() + "],";
            
            RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG + message + e.getMessage(), e);
            
        }
    }
    
    protected ObjectDownloadURL getDownloadUrl()
    {
        int i = 0;
        ObjectDownloadURL downloadUrl = null;
        DCRealTimeThriftServiceClient client;
        do
        {
            try
            {
                
                client = getClient(realTimeCopyTask);
                
                downloadUrl = client.getDownloadUrl(realTimeCopyTask.getTaskId(),
                    realTimeCopyTask.getSrcObjectId());
            }
            catch (TException e)
            {
                RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG + "getDownloadUrl error," + e.getMessage());
            }
            
            if (downloadUrl == null
                || downloadUrl.getErrorCode() == RealTimeCopyTaskError.DSS_UNAVAILABILITY.getErrorCode())
            {
                trySleep();
            }
            else
            {
                break;
            }
            i++;
        } while (i < CopyTaskTool.GET_URL_TRYNUM);
        
        return downloadUrl;
    }
    
    private void trySleep()
    {
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG
                + "The RealTimeCopyTaskWorker Thread sleep occur error when obtain download url");
        }
    }
    
    @Override
    public RealTimeCopyTask getRealTimeCopyTask()
    {
        return realTimeCopyTask;
    }
    
    @Override
    public void setRealTimeCopyTask(RealTimeCopyTask realTimeCopyTask)
    {
        this.realTimeCopyTask = realTimeCopyTask;
        
    }
    
    protected abstract boolean executeRealTimeCopyTask() throws FSException, RealTimeCopyTaskException;
    
    @Override
    public void beforeRealTimeCopyTask() throws RealTimeCopyTaskException
    {
    }
    
    @Override
    public void successRealTimeCopyTask()
    {
        realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
        // 将重试次数清空，换做回调重试计数
        realTimeCopyTask.setRetryNum(0);
        realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
        realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
        realTimeCopyTaskPartService.updateTaskPartsStatus(realTimeCopyTask.getTaskPart());
        realTimeCopyTaskCallbacker.taskCallback(realTimeCopyTask);
        
    }
    
    @Override
    public void failedRealTimeCopyTask()
    {
        realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.FAILED.getCode());
        realTimeCopyTask.setRetryNum(realTimeCopyTask.getRetryNum() + 1);
        realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
        realTimeCopyTaskPartService.updateTaskPartsStatus(realTimeCopyTask.getTaskPart());
    }
    
    private DCRealTimeThriftServiceClient getClient(RealTimeCopyTask copyTaskObj) throws TTransportException
    {
        DCRealTimeThriftServiceClient client = null;
        String uasRegionName = RealTimeCopyTaskTool.getUasRegionName(copyTaskObj);
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.SZ_UAS_REGION_NAME);
                client = szufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.DG_UAS_REGION_NAME);
                client = dgufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
            default:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is default");
                client = ufmThriftClientProxyFactory.getProxy(DCRealTimeThriftServiceClient.class);
                break;
            }
        }
        return client;
    }
    
}
