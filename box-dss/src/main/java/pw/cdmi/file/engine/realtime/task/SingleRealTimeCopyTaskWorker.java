/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.thrift.realTime.app2dc.ObjectDownloadURL;

import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskException;
import pw.cdmi.file.engine.realtime.manager.RealTimeCopyFileObjectManager;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

@Component("singleRealTimeCopyTaskWorker")
@Scope("prototype")
public class SingleRealTimeCopyTaskWorker extends AbstractRealTimeCopyTaskWorker
{
    
    @Autowired
    protected RealTimeCopyFileObjectManager realTimeCopyFileObjectManager;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Override
    protected boolean executeRealTimeCopyTask() throws FSException, RealTimeCopyTaskException
    {
        ObjectDownloadURL downloadUrl = this.getDownloadUrl();
        
        if (downloadUrl == null)
        {
            String message = "FileObject [ " + this.realTimeCopyTask.getDestObjectId()
                + "] getDownloadUrl failed,with null";
            throw new RealTimeCopyTaskException(message);
        }
        this.realTimeCopyTask.setErrorCode(downloadUrl.getErrorCode());
        
        // 原文件不存在,就删除任务。
        if (downloadUrl.getErrorCode() == RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode())
        {
            realTimeCopyTaskService.deleteTask(realTimeCopyTask);
            String message = "Object Not exists";
            throw new RealTimeCopyTaskException(message);
        }
        // 当网络异常、DSS不可用，则任务失败。
        if (downloadUrl.getErrorCode() == RealTimeCopyTaskError.DSS_UNAVAILABILITY.getErrorCode()
            || downloadUrl.getErrorCode() == RealTimeCopyTaskError.INTERNAL_SERVER_ERROR.getErrorCode())
        {
            String message = "Get DownloadUrl failed,error code:" + downloadUrl.getErrorCode();
            throw new RealTimeCopyTaskException(message);
        }
        
        InputStream inputStream = null;
        FileObject fileObject = null;
        
        try
        {
            inputStream = realTimeClientService.download(downloadUrl.getUrl(), null);
            fileObject = realTimeCopyFileObjectManager.createFileObject(this.realTimeCopyTask.getDestObjectId(),
                realTimeCopyTask.getNodeSize(),
                inputStream);
        }
        catch (ObjectAlreadyExistException e)
        {
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG + "The task [" + realTimeCopyTask.getTaskId()
                + "] object already exist");
            fileObject = fileObjectService.getFileObject(this.realTimeCopyTask.getDestObjectId());
        }
        catch (ServiceException e)
        {
            this.realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.DSS_UNAVAILABILITY.getErrorCode());
            throw new RealTimeCopyTaskException(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        
        if (fileObject == null)
        {
            String message = "The task object [" + realTimeCopyTask.getDestObjectId()
                + "] object not exist and create failed.";
            throw new RealTimeCopyTaskException(message);
        }
        
        RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + "The task [" + this.realTimeCopyTask.getTaskId()
            + "] object [" + this.realTimeCopyTask.getDestObjectId() + "] Sha1 is [" + fileObject.getSha1()
            + "]");
        realTimeCopyTask.setMd5(RealTimeCopyTaskTool.getMD5(fileObject.getSha1()));
        realTimeCopyTask.setBlockMD5(RealTimeCopyTaskTool.getBlockMD5(fileObject.getSha1()));
        realTimeCopyTask.setNodeSize(fileObject.getObjectLength());
        realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
        
        return true;
    }
    
}
