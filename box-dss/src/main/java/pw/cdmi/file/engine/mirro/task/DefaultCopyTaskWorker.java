/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;

import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.exception.CopyTaskErrorCode;
import pw.cdmi.file.engine.mirro.exception.CopyTaskException;
import pw.cdmi.file.engine.mirro.exception.CopyTaskNotExistsException;
import pw.cdmi.file.engine.mirro.manager.CopyFileObjectManager;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.service.FileObjectService;

/**
 * 默认文件复制组件，文件不分片
 * 
 * @author w00186884
 * 
 */
@Component("defaultCopyTaskWorker")
@Scope("prototype")
public class DefaultCopyTaskWorker extends AbstractCopyTaskWorker
{
        
    @Autowired
    protected CopyFileObjectManager copyFileObjectManager;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Override
    protected boolean executeCopyTask() throws CopyTaskException
    {
        ObjectDownloadURL downloadUrl = this.getDownloadUrl();
        
        if (downloadUrl == null)
        {
            String message = "FileObject [ " + this.copyTask.getDestObjectId() + "] getDownloadUrl failed,with null";
            this.copyTask.setRemark(CopyTaskTool.getRemark(message));
            throw new CopyTaskException(message);
        }
        this.copyTask.setErrorCode(downloadUrl.getErrorCode());
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId() + "] obtain download url info,code:[" + downloadUrl.getErrorCode() + "],url:[" + downloadUrl.getUrl() + "],msg:[" + downloadUrl.getMsg() + "]");
        
        // 任务不存在了就认为是成功的，删除任务。
        if (downloadUrl.getErrorCode() == CopyTaskErrorCode.TASK_ID_NOTFOUND.getErrCode() || downloadUrl.getErrorCode() == CopyTaskErrorCode.OBJECT_NOTFOUND.getErrCode())
        {
            this.copyTask.setMd5(CopyTaskTool.NOT_EXISTS_MD5);
            this.copyTask.setBlockMD5(CopyTaskTool.NOT_EXISTS_MD5);
            this.copyTask.setRemark(CopyTaskTool.getRemark(downloadUrl.getMsg()));
            this.copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
            this.copyTask.setErrorCode(CopyTaskErrorCode.TASK_ID_NOTFOUND.getErrCode());
            return false;
        }
        
        // 其他错误码只要不等于CopyTaskErrorCode.TASK_SUCCESS认为全部是失败的。
        if (downloadUrl.getErrorCode() > CopyTaskErrorCode.TASK_SUCCESS.getErrCode())
        {
            String message = "Get DownloadUrl failed,error code:" + downloadUrl.getErrorCode();
            this.copyTask.setRemark(CopyTaskTool.getRemark(message));
            throw new CopyTaskException(message);
        }
        
        InputStream inputStream = null;
        FileObject fileObject = null;
        try
        {
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The copy task [" + copyTask.getTaskId() + "],[size:" +copyTask.getSize()+ "] download begin,currentTimeMilles " + System.currentTimeMillis());
            this.copyExe.setBeginDown(new Date());
            inputStream = dssClientService.download(downloadUrl.getUrl(), null);
            this.copyExe.setBeginWrite(new Date());
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The copy task [" + copyTask.getTaskId() + "],[size:" +copyTask.getSize()+ "] download end,currentTimeMilles " + System.currentTimeMillis());
            fileObject = copyFileObjectManager.createFileObject(this.copyTask.getDestObjectId(), copyTask.getSize(), inputStream);
            this.copyExe.setEndWrite(new Date());
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The copy task [" + copyTask.getTaskId() + "],[size:" +copyTask.getSize()+ "] write To uds end,currentTimeMilles " + System.currentTimeMillis());

        }
        catch (ObjectAlreadyExistException e)
        {
            MirrorPrinter.error(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId() + "] object already exist");
            
            fileObject = fileObjectService.getFileObject(this.copyTask.getDestObjectId());
        }
        catch (CopyTaskNotExistsException e)
        {
            this.copyTask.setMd5(CopyTaskTool.NOT_EXISTS_MD5);
            this.copyTask.setBlockMD5(CopyTaskTool.NOT_EXISTS_MD5);
            this.copyTask.setErrorCode(CopyTaskErrorCode.OBJECT_NOTFOUND.getErrCode());
            this.copyTask.setRemark("Task not exist when download");
            this.copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
            
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId() + "] CopyTaskNotExists.");
            
            return false;
        }
        catch (ServiceException e)
        {
            this.copyTask.setErrorCode(CopyTaskErrorCode.DSS_UNAVAILABILITY.getErrCode());
            this.copyTask.setRemark(CopyTaskTool.getRemark("InnerException," + e.getMessage()));
            throw new CopyTaskException(e.getMessage(),e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        
        if (fileObject == null)
        {
            String message = "The task object [" + copyTask.getDestObjectId() + "] object not exist and create failed.";
            this.copyTask.setRemark(message);
            throw new CopyTaskException(message);
        }
        
        // 扫清一切障碍，只为最后的成功
        MirrorPrinter.info(CopyTaskTool.LOG_TAG +"The task [" + this.copyTask.getTaskId() + "] object [" + this.copyTask.getDestObjectId() + "] Sha1 is [" + fileObject.getSha1() +"]");
        
        copyTask.setMd5(CopyTaskTool.getMD5(fileObject.getSha1()));
        copyTask.setBlockMD5(CopyTaskTool.getBloackMD5(fileObject.getSha1()));
        copyTask.setRemark("success");
        copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        copyTask.setErrorCode(CopyTaskErrorCode.TASK_SUCCESS.getErrCode());
        return true;
    }
}
