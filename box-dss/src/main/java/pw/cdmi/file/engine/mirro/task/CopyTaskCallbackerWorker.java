package pw.cdmi.file.engine.mirro.task;

import org.apache.thrift.transport.TTransportException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;
import com.huawei.sharedrive.thrift.mirror.app2dc.TBusinessException;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.exception.CopyTaskErrorCode;
import pw.cdmi.file.engine.mirro.monitor.CopyTaskMonitor;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.thirft.DCMirrorThriftServiceClient;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

public class CopyTaskCallbackerWorker implements Runnable
{
    
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    private CopyTaskService copyTaskService;
    
    private CopyTask copyTask;
    
    public CopyTaskCallbackerWorker(CopyTask copyTask)
    {
        this.copyTask = copyTask;
    }
    
    private void initBean()
    {
        copyTaskService = (CopyTaskService) SpringContextUtil.getBean("copyTaskService");
        ufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("ufmThriftClientProxyFactory");
        szufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("szufmThriftClientProxyFactory");
        dgufmThriftClientProxyFactory = (ThriftClientProxyFactory) SpringContextUtil.getBean("dgufmThriftClientProxyFactory");
    }
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        initBean();
        
        if (null == copyTask)
        {
            MirrorPrinter.info("the copytask is null");
            return;
        }
        reportTask(copyTask);
    }
    
    public void reportTask(CopyTask copyTask)
    {
        try
        {
            reportSingleReault(copyTask);
        }
        catch (Exception e)
        {
            MirrorPrinter.error("CopyTaskCallbacker Job execute failed," + e.getMessage(), e);
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportSingleReault(CopyTask copyTask)
    {
        CopyTaskExeResult exeResult = new CopyTaskExeResult();
        exeResult.setTaskId(copyTask.getTaskId());
        exeResult.setSrcObjectId(copyTask.getSrcObjectId());
        exeResult.setDestObjectId(copyTask.getDestObjectId());
        exeResult.setSize(copyTask.getSize());
        exeResult.setMd5(copyTask.getMd5());
        exeResult.setDataBlockMd5(copyTask.getBlockMD5());
        exeResult.setResult(copyTask.getErrorCode());
        
        try
        {
            DCMirrorThriftServiceClient client = getClient(copyTask, exeResult);
            
            client.reportCopyTaskExeResult(exeResult);
            
            MirrorPrinter.info("reportCopyTaskExeResult succeed," + exeResult.getTaskId());
        }
        catch (TBusinessException e)
        {
            MirrorPrinter.error("CopyTaskCallbacker execute with TBusinessException,Code :" + e.getStatus()
                + " status :" + e.getMessage());
            
            copyTask.setErrorCode(e.getStatus());
            
            // 当返回对象已经不存在或者任务已经不存在了，则将任务删除。
            if (CopyTaskErrorCode.OBJECT_NOTFOUND.getErrCode() == e.getStatus()
                || CopyTaskErrorCode.TASK_ID_NOTFOUND.getErrCode() == e.getStatus())
            {
                copyTask.setCopyStatus(CopyTaskStatus.CALLBACKSUCCESS.getCode());
                
                copyTask.setRemark(CopyTaskTool.getRemark(e.getMessage()));
                
                copyTaskService.updateTaskStatus(copyTask);
                
                CopyTaskMonitor.getCallbackSuccessTask().addAndGet(1);
                
            }
            // 当内容错误时则将任务重置下次重新下载（主要是在分片处理时可能发生的意外异常）
            else if (CopyTaskErrorCode.CONTENT_ERROR.getErrCode() == e.getStatus())
            {
                copyTask.setCopyStatus(CopyTaskStatus.CALLBACKCONTENTFAILED.getCode());
                copyTask.setRemark(CopyTaskTool.getRemark(e.getMessage()));
                copyTask.setRetryNum(0);
                copyTaskService.updateTaskStatus(copyTask);
                
                CopyTaskMonitor.getCallCackFailedTask().addAndGet(1);
                
            }
            else
            {
                copyTask.setCopyStatus(CopyTaskStatus.CALLBACKFAILED.getCode());
                
                copyTask.setRemark(CopyTaskTool.getRemark(e.getMessage()));
                
                copyTask.setRetryNum(copyTask.getRetryNum() + 1);
                copyTaskService.updateTaskStatus(copyTask);
                
                CopyTaskMonitor.getCallCackFailedTask().addAndGet(1);
                
            }
        }
        catch (Exception e)
        {
            MirrorPrinter.error("CopyTaskCallbacker Job execute failed," + e.getMessage(), e);
            
            copyTask.setErrorCode(500);
            copyTask.setCopyStatus(CopyTaskStatus.CALLBACKFAILED.getCode());
            copyTask.setRemark("Callback failed," + e.getMessage());
            copyTask.setRetryNum(copyTask.getRetryNum() + 1);
            copyTaskService.updateTaskStatus(copyTask);
            
            CopyTaskMonitor.getCallCackFailedTask().addAndGet(1);
            
        }
        
        copyTask.setRemark(null);
        copyTask.setErrorCode(200);
        copyTask.setCopyStatus(CopyTaskStatus.CALLBACKSUCCESS.getCode());
        copyTaskService.updateTaskStatus(copyTask);
        
        CopyTaskMonitor.getCallbackSuccessTask().addAndGet(1);
        
    }
    
    public CopyTask getCopyTask()
    {
        return copyTask;
    }
    
    public void setCopyTask(CopyTask copyTask)
    {
        this.copyTask = copyTask;
    }
    
    private DCMirrorThriftServiceClient getClient(CopyTask copyTaskObj, CopyTaskExeResult exeResult) throws TTransportException
    {
        DCMirrorThriftServiceClient client = null;
        String uasRegionName = CopyTaskTool.getUasRegionName(copyTaskObj);
        exeResult.setSrcObjectId(copyTaskObj.getSrcObjectId());
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.SZ_UAS_REGION_NAME);
                client = szufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.DG_UAS_REGION_NAME);
                client = dgufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
            default:
            {
                MirrorPrinter.info("The checkTokenAuthVaild uas region name is default");
                client = ufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
        }
        return client;
    }
}
