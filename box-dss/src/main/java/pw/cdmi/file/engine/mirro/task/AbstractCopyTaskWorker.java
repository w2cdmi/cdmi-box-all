/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.thrift.exception.ThriftPoolException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.domain.CopyExeTime;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.exception.CopyTaskErrorCode;
import pw.cdmi.file.engine.mirro.exception.CopyTaskException;
import pw.cdmi.file.engine.mirro.httpclient.CopyHttpClient;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskPartService;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.thirft.DCMirrorThriftServiceClient;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

/**
 * @author w00186884
 * 
 */
public abstract class AbstractCopyTaskWorker implements CopyTaskWorker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCopyTaskWorker.class);
    
    @Autowired
    protected CopyHttpClient dssClientService;
    
    @Autowired
    protected CopyTaskService copyTaskService;
    
    @Autowired
    protected CopyTaskPartService copyTaskPartService;
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    
    protected CopyTask copyTask;
    
    protected CopyExeTime copyExe;
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        
        if (this.copyTask == null)
        {
            LOGGER.warn(CopyTaskTool.LOG_TAG + "The Copy task is null,currnet task thread worker stoped");
            return;
        }
        
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The copy task [" + copyTask.getTaskId() + "],[size:"
            + copyTask.getSize() + "], [srcObject [" + copyTask.getSrcObjectId() + "],destObject [ "
            + copyTask.getDestObjectId() + "] start running");
        
        copyExe = new CopyExeTime();
        copyExe.setCopyTaskId(copyTask.getTaskId());
        copyExe.setExeAgent(copyTask.getExeAgent());
        copyExe.setSize(copyTask.getSize());
        copyExe.setStartTime(new Date());
        long startTime = System.currentTimeMillis();
        
        boolean result = this.executeTask();
        
        long usedTime = (System.currentTimeMillis() - startTime) / 1000;
        
        copyExe.setEndTime(new Date());
        
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The copy task [" + copyTask.toString()
            + "] execute complete,result [" + result + "],Used Time[" + usedTime + "]");
        // 去掉测试性能分析数据
        //copyExeTimeService.inserCopyExeRecord(copyExe);
    }
    
    private boolean executeTask()
    {
        try
        {
            this.beforeCopyTask();
            // 三种返回结果 true/false/exception
            // true 代表任务正常结束 false代表其他原因结束，但是任务是可以继续执行，比如404.异常则抛出
            boolean result = this.executeCopyTask();
            
            if (result)
            {
                this.successCopyTask();
            }
            else
            {
                failedCopyTask();
            }
            
            return result;
        }
        catch (ThriftPoolException e)
        {
            copyTask.setRemark(CopyTaskTool.getRemark(e.getMessage()));
            
            this.failedCopyTask();
            
            LOGGER.error(CopyTaskTool.LOG_TAG + "The copy task thirft client init failed," + e.getMessage());
            
            return false;
        }
        catch (Exception e)
        {
            this.failedCopyTask();
            
            String message = "CopyTaskWorker execute Failed. taskid [" + this.copyTask.getTaskId()
                + " ] ,objectId:[" + this.copyTask.getSrcObjectId() + "],";
            
            LOGGER.error(CopyTaskTool.LOG_TAG + message + e.getMessage(), e);
            
            return false;
        }
        finally
        {
            this.completeCopyTask();
        }
    }
    
    protected ObjectDownloadURL getDownloadUrl()
    {
        ObjectDownloadURL downloadUrl = null;
        int i = 0;
        DCMirrorThriftServiceClient client;
        do
        {
            try
            {
                
                client = getClient();
                
                downloadUrl = client.getDownloadUrl(copyTask.getTaskId(), copyTask.getSrcObjectId());
            }
            catch (TException e)
            {
                LOGGER.warn(CopyTaskTool.LOG_TAG + "getDownloadUrl error," + e.getMessage());
            }
            
            if (downloadUrl == null
                || downloadUrl.getErrorCode() == CopyTaskErrorCode.DSS_UNAVAILABILITY.getErrCode())
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
            LOGGER.warn(CopyTaskTool.LOG_TAG
                + "The CopyTaskWorker Thread sleep occur error when obtain download url");
        }
    }
    
    /**
     * 实现该方法以完成文件的下载和写入操作。
     * 
     * @return
     * @throws Exception
     */
    protected abstract boolean executeCopyTask() throws FSException, CopyTaskException;
    
    @Override
    public void beforeCopyTask()
    {
        // 默认的任务状态已经在分发时做了处理。
        // do nothing
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void successCopyTask()
    {
        copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        // 将重试次数清空，换做回调重试计数
        copyTask.setRetryNum(0);
        copyTaskService.updateTaskStatus(copyTask);
        copyTaskPartService.updateTaskPartsStatus(copyTask.getTaskPart());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void failedCopyTask()
    {
        copyTask.setRetryNum(copyTask.getRetryNum() + 1);
        copyTask.setCopyStatus(CopyTaskStatus.FAILED.getCode());
        copyTaskService.updateTaskStatus(copyTask);
        copyTaskPartService.updateTaskPartsStatus(copyTask.getTaskPart());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void completeCopyTask()
    {
        if (this.copyTask.getCopyStatus() == CopyTaskStatus.RUNNING.getCode())
        {
            LOGGER.warn(CopyTaskTool.LOG_TAG
                + "The task failed with unkonwn exception,maybe the thread stoped");
            copyTask.setCopyStatus(CopyTaskStatus.FAILED.getCode());
        }
        
        if (CollectionUtils.isNotEmpty(copyTask.getTaskPart()))
        {
            for (CopyTaskPart part : copyTask.getTaskPart())
            {
                if (part.getCopyStatus() == CopyTaskStatus.RUNNING.getCode())
                {
                    LOGGER.warn(CopyTaskTool.LOG_TAG
                        + "The task failed with unkonwn exception,maybe the thread stoped");
                    part.setCopyStatus(CopyTaskStatus.FAILED.getCode());
                }
            }
        }
        copyTaskService.updateTaskStatus(copyTask);
        copyTaskPartService.updateTaskPartsStatus(copyTask.getTaskPart());
    }
    
    @Override
    public CopyTask getCopyTask()
    {
        return copyTask;
    }
    
    @Override
    public void setCopyTask(CopyTask copyTask)
    {
        this.copyTask = copyTask;
    }
    
    private DCMirrorThriftServiceClient getClient() throws TTransportException
    {
        DCMirrorThriftServiceClient client = null;
        String uasRegionName = CopyTaskTool.getUasRegionName(copyTask);
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.SZ_UAS_REGION_NAME);
                client = szufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is "
                    + SystemConfigKeys.DG_UAS_REGION_NAME);
                client = dgufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
            default:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is default");
                client = ufmThriftClientProxyFactory.getProxy(DCMirrorThriftServiceClient.class);
                break;
            }
        }
        return client;
        
    }
}
