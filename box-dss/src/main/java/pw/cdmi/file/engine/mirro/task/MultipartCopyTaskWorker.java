/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;

import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.exception.CopyTaskErrorCode;
import pw.cdmi.file.engine.mirro.exception.CopyTaskException;
import pw.cdmi.file.engine.mirro.exception.CopyTaskNotExistsException;
import pw.cdmi.file.engine.mirro.manager.MultipartFileCopyFileObjectManager;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.exception.PreconditionFailedException;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * @author w00186884
 * 
 */
@Component("multipartCopyTaskWorker")
@Scope("prototype")
public class MultipartCopyTaskWorker extends AbstractCopyTaskWorker
{
    
    @Autowired
    protected MultipartFileCopyFileObjectManager multipartFileCopyFileObjectManager;
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Value("${mirro.part.size}")
    private long mirroPartSize = CopyTaskTool.DEFAULT_PART_SIZE;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void beforeCopyTask()
    {
        List<CopyTaskPart> partsFromDb = this.buildTaskPartsAndGetFromDb();
        this.copyTask.setTaskPart(partsFromDb);
        
        // 先更新一下状态，便于观察进度
        for (CopyTaskPart part : partsFromDb)
        {
            if (part.getCopyStatus() == CopyTaskStatus.INPUT.getCode()
                || part.getCopyStatus() == CopyTaskStatus.FAILED.getCode())
            {
                part.setCopyStatus(CopyTaskStatus.RUNNING.getCode());
            }
        }
        
        copyTaskPartService.updateTaskPartsStatus(copyTask.getTaskPart());
    }
    
    @Override
    protected boolean executeCopyTask() throws FSException, CopyTaskException
    {
        // 先创建对象
        String callbackKey = this.copyTask.getTaskId() + CopyTaskTool.CALLBACKKEY_SUFIX;
        
        MultipartFileObject multipartFileObject = null;
        
        try
        {
            multipartFileObject = multipartFileCopyFileObjectManager.initMultipartUpload(this.copyTask.getDestObjectId(),
                callbackKey);
        }
        catch (ObjectAlreadyExistException e)
        {
            multipartFileObject = executeByObjectAlreadyExist();
        }
        
        if (null == multipartFileObject)
        {
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "multipartFileObject for copyTask [ {} ] is null."
                + this.copyTask.toString());
            return true;
        }
        
        List<CopyTaskPart> partsFromDb = this.copyTask.getTaskPart();
        // 开始执行任务
        boolean taskFinshed = false;
        
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        for (CopyTaskPart part : partsFromDb)
        {
            // 已经成功过的就跳过啦
            if (this.hasSuccess(part))
            {
                continue;
            }
            // 处理单个分片,三种情况：任务已经不存在、成功、失败
            taskFinshed = this.processPartTask(part);
            if (taskFinshed)
            {
                this.finishTaskBecaseSrcObjectNotExists();
                return false;
            }
            
            if (part.getCopyStatus() == CopyTaskStatus.FAILED.getCode())
            {
                part.setRetryNum(part.getRetryNum() + 1);
            }
            
            // 如果单次执行任务超过了1个小时，则先将任务刷新一下.
            endTime = System.currentTimeMillis();
            if ((endTime - startTime) > 1 * 60 * 60 * 1000)
            {
                copyTaskService.updateTaskStatus(this.copyTask);
            }
        }
        
        copyTaskPartService.updateTaskPartsStatus(partsFromDb);
        
        // 任务结束
        boolean allPartsSuccess = copyTaskPartService.allPartsSuccess(this.copyTask.getTaskId());
        
        // 只有全部是成功的，认为是成功的，否则认为是失败的。
        if (allPartsSuccess)
        {
            executeByAllPartsSuccess(partsFromDb);
            return true;
        }
        executeByNotAllPartsSuccess(partsFromDb);
        return false;
    }
    
    private void executeByNotAllPartsSuccess(List<CopyTaskPart> partsFromDb) throws CopyTaskException
    {
        int success = 0;
        for (CopyTaskPart part : partsFromDb)
        {
            if (part.getCopyStatus() == CopyTaskStatus.SUCCESS.getCode())
            {
                success++;
            }
        }
        String message = "The task [" + this.copyTask.getTaskId() + "] failed,total:" + partsFromDb.size()
            + ",success:" + success;
        this.copyTask.setRemark(message);
        throw new CopyTaskException(message);
    }
    
    private void executeByAllPartsSuccess(List<CopyTaskPart> partsFromDb) throws CopyTaskException
    {
        MultipartFileObject multipartFileObject;
        TreeSet<MultipartPart> parts = (TreeSet<MultipartPart>) coventToMultipart(partsFromDb);
        try
        {
            multipartFileObject = multipartFileCopyFileObjectManager.completeMultipartUpload(this.copyTask.getDestObjectId(),
                parts);
        }
        catch (PreconditionFailedException e)
        {
            // 已经上传成功或者是正在提交状态
            
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId()
                + "] object exist and not in uploading");
            multipartFileObject = multipartFileObjectService.getMultipartUpload(this.copyTask.getDestObjectId());
        }
        
        if (multipartFileObject == null)
        {
            FileObject fileObject = fileObjectService.getFileObject(this.copyTask.getDestObjectId());
            if (fileObject != null)
            {
                this.finishTaskBecaseObjectExists(fileObject);
                return;
            }
            
            String message = "Complete MultipartFileObject [" + this.copyTask.getDestObjectId()
                + "] object [" + this.copyTask.getDestObjectId() + "]not exists,invalid status";
            copyTask.setRemark(CopyTaskTool.getRemark(message));
            throw new CopyTaskException(message);
        }
        
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + " Task [" + this.copyTask.getTaskId()
            + "] multipartFileObjectSha1:" + multipartFileObject.getSha1());
        copyTask.setRemark("success");
        copyTask.setMd5(CopyTaskTool.getMD5(multipartFileObject.getSha1()));
        copyTask.setBlockMD5(CopyTaskTool.getBloackMD5(multipartFileObject.getSha1()));
        copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        copyTask.setErrorCode(CopyTaskErrorCode.TASK_SUCCESS.getErrCode());
        
    }
    
    private MultipartFileObject executeByObjectAlreadyExist() throws CopyTaskException
    {
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId() + "] object ["
            + this.copyTask.getDestObjectId() + "] exists");
        // 如果对象已经存在认为是成功的
        MultipartFileObject multipartFileObject = multipartFileObjectService.getMultipartUpload(this.copyTask.getDestObjectId());
        if (null != multipartFileObject)
        {
            this.copyTask.setRemark(CopyTaskTool.getRemark("The task [" + copyTask.getTaskId() + "] object ["
                + this.copyTask.getDestObjectId() + "] multipartFileObject exists"));
            return multipartFileObject;
        }
        
        // 有可能multipartFileObject已经被删除,看看fileObject
        FileObject fileObject = fileObjectService.getFileObject(this.copyTask.getDestObjectId());
        if (fileObject == null)
        {
            String message = "Init Task [" + this.copyTask.getTaskId() + "] object ["
                + this.copyTask.getDestObjectId() + "] not exists,invalid status";
            this.copyTask.setRemark(CopyTaskTool.getRemark(message));
            throw new CopyTaskException(message);
        }
        
        this.finishTaskBecaseObjectExists(fileObject);
        
        return null;
    }
    
    private void finishTaskBecaseSrcObjectNotExists()
    {
        this.copyTask.setMd5(CopyTaskTool.NOT_EXISTS_MD5);
        this.copyTask.setBlockMD5(CopyTaskTool.NOT_EXISTS_MD5);
        this.copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        this.copyTask.setErrorCode(CopyTaskErrorCode.TASK_ID_NOTFOUND.getErrCode());
        this.copyTask.setRemark("Task or src object not exist,change status to successfully.");
    }
    
    private void finishTaskBecaseObjectExists(FileObject fileObject) throws CopyTaskException
    {
        if (StringUtils.isBlank(fileObject.getSha1()))
        {
            String message = CopyTaskTool.LOG_TAG + " Task [" + this.copyTask.getTaskId()
                + "] object exists but sha1 is null";
            this.copyTask.setErrorCode(CopyTaskErrorCode.DEFAULT.getErrCode());
            this.copyTask.setRemark(message);
            throw new CopyTaskException(message);
        }
        
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + " Task [" + this.copyTask.getTaskId() + "] objectSha1:"
            + fileObject.getSha1());
        this.copyTask.setMd5(CopyTaskTool.getMD5(fileObject.getSha1()));
        this.copyTask.setBlockMD5(CopyTaskTool.getBloackMD5(fileObject.getSha1()));
        this.copyTask.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        this.copyTask.setErrorCode(CopyTaskErrorCode.TASK_SUCCESS.getErrCode());
        this.copyTask.setRemark("File Object Exists");
        
    }
    
    /**
     * 返回结果表示任务是否终结。表示当前整体文件任务，并非当前分片任务
     * 
     * @param part
     * @return
     * @throws FSException
     * @throws NumberFormatException
     */
    private boolean processPartTask(CopyTaskPart part) throws CopyTaskException, NumberFormatException,
        FSException
    {
        ObjectDownloadURL downloadUrl = this.getDownloadUrl();
        
        if (downloadUrl == null)
        {
            String message = "multipartFileObject [ " + this.copyTask.getDestObjectId() + "] part ["
                + part.getPartId() + "] getDownloadUrl failed,with null";
            
            MirrorPrinter.warn(CopyTaskTool.LOG_TAG + message);
            
            part.setRemark(CopyTaskTool.getRemark(message));
            part.setCopyStatus(CopyTaskStatus.FAILED.getCode());
            return false;
        }
        
        MirrorPrinter.info(CopyTaskTool.LOG_TAG + "The task [" + copyTask.getTaskId() + "] part ["
            + part.getPartId() + "] obtain download url info,code:[" + downloadUrl.getErrorCode() + "],url:["
            + downloadUrl.getUrl() + "],msg:[" + downloadUrl.getMsg() + "]");
        
        // 不可用就抛出异常,下次再来
        // 任务不存在了就认为是成功的，删除任务。
        if (downloadUrl.getErrorCode() == CopyTaskErrorCode.TASK_ID_NOTFOUND.getErrCode()
            || downloadUrl.getErrorCode() == CopyTaskErrorCode.OBJECT_NOTFOUND.getErrCode())
        {
            part.setRemark("Task not exist when getDownloadUrl");
            return true;
        }
        
        if (downloadUrl.getErrorCode() > CopyTaskErrorCode.TASK_SUCCESS.getErrCode())
        {
            String message = "Get DownloadUrl failed,error code:" + downloadUrl.getErrorCode();
            MirrorPrinter.warn(CopyTaskTool.LOG_TAG + message);
            
            part.setRemark(CopyTaskTool.getRemark(message));
            part.setCopyStatus(CopyTaskStatus.FAILED.getCode());
        }
        
        // 通过上面的过滤，downloadURL肯定是成功的
        try
        {
            MultipartPart multiPart = this.executeCopyTaskPart(downloadUrl, part);
            
            MirrorPrinter.info(CopyTaskTool.LOG_TAG + "ths task [" + this.copyTask.getTaskId() + "] part ["
                + multiPart.getPartId() + "] execute successfully.");
        }
        catch (CopyTaskNotExistsException e)
        {
            // 返回True代表任务结束
            MirrorPrinter.warn(CopyTaskTool.LOG_TAG + "ths task [" + this.copyTask.getTaskId()
                + "] is not exists.");
            return true;
        }
        catch (CopyTaskException e)
        {
            String message = CopyTaskTool.LOG_TAG + "ths task [" + this.copyTask.getTaskId()
                + "] down part occur error.";
            MirrorPrinter.error(message, e);
            
            // 此时继续下一片已经没多大意义。
            this.copyTask.setRemark(CopyTaskTool.getRemark(message));
            throw e;
        }
        
        part.setRemark("success");
        part.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
        
        return false;
    }
    
    /**
     * 执行分片上传
     * 
     * @return
     * @throws CopyTaskNotExistsException
     * @throws FSException
     * @throws NumberFormatException
     */
    private MultipartPart executeCopyTaskPart(ObjectDownloadURL downloadUrl, CopyTaskPart part)
        throws CopyTaskNotExistsException, CopyTaskException, NumberFormatException, FSException
    {
        InputStream inputStream = null;
        try
        {
            Map<String, String> headers = new HashMap<String, String>(1);
            headers.put(RequestConstants.REQUEST_OBJECT_RANGE, "bytes=" + part.getPartRange());
            inputStream = dssClientService.download(downloadUrl.getUrl(), headers);
            
            String callbackKey = this.copyTask.getTaskId() + ';' + part.getPartId();
            
            return multipartFileCopyFileObjectManager.createMultipartPart(this.copyTask.getDestObjectId(),
                part.getSize(),
                callbackKey,
                Integer.parseInt(part.getPartId()),
                inputStream);
        }
        catch (CopyTaskNotExistsException e)
        {
            MirrorPrinter.error("Download not exists with part : [" + this.copyTask.getTaskId() + ' '
                + part.getPartId() + " ] ");
            throw e;
        }
        // 前置条件不满足可能两种情况，1.文件已经提交；2.在非上传过程中。说明已经提交成功了
        catch (PreconditionFailedException e)
        {
            // 已经提交成功了
            part.setRemark("submited");
            part.setCopyStatus(CopyTaskStatus.SUCCESS.getCode());
            return new MultipartPart(Integer.parseInt(part.getPartId()));
        }
        catch (ServiceException e)
        {
            String message = "Get Post Part InputStream Failed. [" + this.copyTask.getTaskId() + ' '
                + part.getPartId() + " ] ";
            MirrorPrinter.warn(CopyTaskTool.LOG_TAG + message, e);
            
            part.setRemark(CopyTaskTool.getRemark(e.getMessage()));
            part.setCopyStatus(CopyTaskStatus.FAILED.getCode());
            
            // 这种情况下再进行下一片已经没多大意义。
            throw new CopyTaskException(e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private Set<MultipartPart> coventToMultipart(List<CopyTaskPart> partsFromDb)
    {
        TreeSet<MultipartPart> partSet = new TreeSet<MultipartPart>();
        MultipartPart mp = null;
        for (CopyTaskPart part : partsFromDb)
        {
            mp = new MultipartPart(Integer.parseInt(part.getPartId()), part.getSize(),null, null);
            partSet.add(mp);
        }
        return partSet;
    }
    
    private boolean hasSuccess(CopyTaskPart part)
    {
        if (part.getCopyStatus() == CopyTaskStatus.SUCCESS.getCode())
        {
            return true;
        }
        if (part.getCopyStatus() == CopyTaskStatus.CALLBACKFAILED.getCode())
        {
            return true;
        }
        if (part.getCopyStatus() == CopyTaskStatus.CALLBACKSUCCESS.getCode())
        {
            return true;
        }
        return false;
    }
    
    /**
     * 1.是否已经记录分片任务 2.如果没有创建就创建分片任务 3.默认分片大小 10M = 10*1024*1024 字节
     * 
     * @return
     */
    private List<CopyTaskPart> buildTaskPartsAndGetFromDb()
    {
        long defaultSize = mirroPartSize;
        boolean hasCopyTaskpart = copyTaskPartService.hasCopyTaskPart(this.copyTask.getTaskId());
        if (!hasCopyTaskpart)
        {
            long mod = this.copyTask.getSize() / defaultSize;
            List<CopyTaskPart> parts = new ArrayList<CopyTaskPart>(1);
            long lastPart = this.copyTask.getSize();
            
            long startRange = 0;
            long endRange = 0;
            for (int i = 0; i < mod; i++)
            {
                startRange = i * defaultSize;
                endRange = (i + 1) * defaultSize - 1;
                parts.add(newCopyTaskPart(i, defaultSize, startRange, endRange));
                lastPart -= defaultSize;
            }
            if (lastPart > 0)
            {
                startRange = mod * defaultSize;
                endRange = this.copyTask.getSize();
                parts.add(newCopyTaskPart(Integer.parseInt(mod + ""), lastPart, startRange, endRange));
            }
            copyTaskPartService.batchInsertOrReplace(parts);
        }
        
        return copyTaskPartService.listCopyTaskPartByTaskId(this.copyTask.getTaskId());
    }
    
    private CopyTaskPart newCopyTaskPart(int partId, long partSize, long start, long end)
    {
        CopyTaskPart part = new CopyTaskPart();
        part.setTaskId(this.copyTask.getTaskId());
        part.setPartId(String.valueOf(partId + 1));
        part.setSize(partSize);
        Date now = new Date();
        part.setCreatedAt(now);
        part.setModifiedAt(now);
        part.setCopyStatus(CopyTaskStatus.INPUT.getCode());
        part.setPartRange(String.valueOf(start) + '-' + end);
        return part;
    }
}
