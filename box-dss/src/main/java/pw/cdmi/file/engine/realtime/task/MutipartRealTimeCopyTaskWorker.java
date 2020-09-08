/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.thrift.realTime.app2dc.ObjectDownloadURL;

import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.manager.MultipartFileCopyFileObjectManager;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.exception.PreconditionFailedException;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskPart;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskException;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

@Component("multipartRealTimeCopyTaskWorker")
@Scope("prototype")
public class MutipartRealTimeCopyTaskWorker extends AbstractRealTimeCopyTaskWorker
{
    
    @Autowired
    protected MultipartFileCopyFileObjectManager multipartFileCopyFileObjectManager;
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    private long realTimePartSize = SystemConfigContainer.getLong(SystemConfigKeys.REALTIME_DEFAULT_SPLIT_SIZE,
        RealTimeCopyTaskTool.DEFAULT_SPLIT_SIZE);
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void beforeRealTimeCopyTask()
    {
        List<RealTimeCopyTaskPart> partsFromDb = this.buildTaskPartsAndGetFromDb();
        this.realTimeCopyTask.setTaskPart(partsFromDb);
        
        // 先更新状态
        for (RealTimeCopyTaskPart part : partsFromDb)
        {
            if (part.getStatus() == RealTimeCopyTaskStatus.WAITING.getCode()
                || part.getStatus() == RealTimeCopyTaskStatus.FAILED.getCode())
            {
                part.setStatus(RealTimeCopyTaskStatus.RUNNING.getCode());
            }
        }
        
        realTimeCopyTaskPartService.updateTaskPartsStatus(realTimeCopyTask.getTaskPart());
    }
    
    @Override
    protected boolean executeRealTimeCopyTask() throws FSException, RealTimeCopyTaskException
    {
        
        String callbackKey = this.realTimeCopyTask.getTaskId() + RealTimeCopyTaskTool.CALLBACKKEY_SUFIX;
        
        MultipartFileObject multipartFileObject = null;
        
        List<RealTimeCopyTaskPart> partsFromDb = this.realTimeCopyTask.getTaskPart();
        
        try
        {
            multipartFileObject = multipartFileCopyFileObjectManager.initMultipartUpload(this.realTimeCopyTask.getDestObjectId(),
                callbackKey);
        }
        catch (ObjectAlreadyExistException e)
        {
            multipartFileObject = executeByObjectAlreadyExist();
        }
        // 生成fileobject已经存在，就认为任务成功
        if (null == multipartFileObject)
        {
            RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG
                + "multipartFileObject for realTimeCopyTask [ {} ] is null."
                + this.realTimeCopyTask.toString());
            return true;
        }
        // 开始执行任务
        boolean taskPartFinished = false;
        
        for (RealTimeCopyTaskPart part : partsFromDb)
        {
            // 已经成功过的就跳过
            if (this.hasSuccess(part))
            {
                continue;
            }
            // 处理单个分片
            taskPartFinished = this.processPartTask(part);
            // 如果有一个分片下载失败，就不进行以后的分片下载
            if (!taskPartFinished)
            {
                break;
            }
        }
        
        realTimeCopyTaskPartService.updateTaskPartsStatus(partsFromDb);
        
        // 任务结束
        boolean allPartsSuccess = realTimeCopyTaskPartService.allPartsSuccess(this.realTimeCopyTask.getTaskId());
        
        // 只有全部是成功的，认为是成功的，否则认为是失败的。
        if (allPartsSuccess)
        {
            executeByAllPartsSuccess(partsFromDb);
            return true;
        }
        executeByNotAllPartsSuccess(partsFromDb);
        return false;
    }
    
    private void executeByNotAllPartsSuccess(List<RealTimeCopyTaskPart> partsFromDb)
        throws RealTimeCopyTaskException
    {
        int success = 0;
        for (RealTimeCopyTaskPart part : partsFromDb)
        {
            if (part.getStatus() == RealTimeCopyTaskStatus.SUCCESS.getCode())
            {
                success++;
            }
        }
        String message = "The task [" + this.realTimeCopyTask.getTaskId() + "] failed,total:"
            + partsFromDb.size() + ",success:" + success;
        throw new RealTimeCopyTaskException(message);
    }
    
    private void executeByAllPartsSuccess(List<RealTimeCopyTaskPart> partsFromDb)
        throws RealTimeCopyTaskException
    {
        MultipartFileObject multipartFileObject;
        TreeSet<MultipartPart> parts = (TreeSet<MultipartPart>) coventToMultipart(partsFromDb);
        try
        {
            multipartFileObject = multipartFileCopyFileObjectManager.completeMultipartUpload(this.realTimeCopyTask.getDestObjectId(),
                parts);
        }
        catch (PreconditionFailedException e)
        {
            // 已经上传成功或者是正在提交状态
            
            RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + "The task [" + realTimeCopyTask.getTaskId()
                + "] object exist and not in uploading");
            multipartFileObject = multipartFileObjectService.getMultipartUpload(this.realTimeCopyTask.getDestObjectId());
        }
        
        if (multipartFileObject == null)
        {
            FileObject fileObject = fileObjectService.getFileObject(this.realTimeCopyTask.getDestObjectId());
            if (fileObject != null)
            {
                this.finishTaskBecaseObjectExists(fileObject);
                return;
            }
            
            String message = "Complete MultipartFileObject [" + this.realTimeCopyTask.getDestObjectId()
                + "] object [" + this.realTimeCopyTask.getDestObjectId() + "]not exists,invalid status";
            throw new RealTimeCopyTaskException(message);
        }
        
        RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + " Task [" + this.realTimeCopyTask.getTaskId()
            + "] multipartFileObjectSha1:" + multipartFileObject.getSha1());
        realTimeCopyTask.setMd5(RealTimeCopyTaskTool.getMD5(multipartFileObject.getSha1()));
        realTimeCopyTask.setBlockMD5(RealTimeCopyTaskTool.getBlockMD5(multipartFileObject.getSha1()));
        realTimeCopyTask.setNodeSize(multipartFileObject.getObjectLength());
        realTimeCopyTask.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
        realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
        
    }
    
    /**
     * 1.是否已经记录分片任务 2.如果没有创建就创建分片任务 3.默认分片大小 20M = 20*1024*1024 字节
     * 
     * @return
     */
    private List<RealTimeCopyTaskPart> buildTaskPartsAndGetFromDb()
    {
        boolean hasCopyTaskpart = realTimeCopyTaskPartService.hasRealTimeCopyTaskPart(this.realTimeCopyTask.getTaskId());
        if (!hasCopyTaskpart)
        {
            long mod = this.realTimeCopyTask.getNodeSize() / realTimePartSize;
            List<RealTimeCopyTaskPart> parts = new ArrayList<RealTimeCopyTaskPart>(1);
            long lastPart = this.realTimeCopyTask.getNodeSize();
            
            long startRange = 0;
            long endRange = 0;
            for (int i = 0; i < mod; i++)
            {
                startRange = i * realTimePartSize;
                endRange = (i + 1) * realTimePartSize - 1;
                parts.add(newRealTimeCopyTaskPart(i, realTimePartSize, startRange, endRange));
                lastPart -= realTimePartSize;
            }
            if (lastPart > 0)
            {
                startRange = mod * realTimePartSize;
                endRange = this.realTimeCopyTask.getNodeSize();
                parts.add(newRealTimeCopyTaskPart(Integer.parseInt(mod + ""), lastPart, startRange, endRange));
            }
            realTimeCopyTaskPartService.batchInsertOrReplace(parts);
        }
        
        return realTimeCopyTaskPartService.listRealTimeCopyTaskPartByTaskId(this.realTimeCopyTask.getTaskId());
    }
    
    private RealTimeCopyTaskPart newRealTimeCopyTaskPart(int partId, long partSize, long start, long end)
    {
        RealTimeCopyTaskPart part = new RealTimeCopyTaskPart();
        part.setTaskId(this.realTimeCopyTask.getTaskId());
        part.setPartId(String.valueOf(partId + 1));
        part.setSize(partSize);
        Date now = new Date();
        part.setCreateTime(now);
        part.setModifyTime(now);
        part.setStatus(RealTimeCopyTaskStatus.WAITING.getCode());
        part.setPartRange(String.valueOf(start) + '-' + end);
        return part;
    }
    
    private Set<MultipartPart> coventToMultipart(List<RealTimeCopyTaskPart> partsFromDb)
    {
        TreeSet<MultipartPart> partSet = new TreeSet<MultipartPart>();
        MultipartPart mp = null;
        for (RealTimeCopyTaskPart part : partsFromDb)
        {
            mp = new MultipartPart(Integer.parseInt(part.getPartId()), part.getSize(), null, null);
            partSet.add(mp);
        }
        return partSet;
    }
    
    private boolean hasSuccess(RealTimeCopyTaskPart part)
    {
        if (part.getStatus() == RealTimeCopyTaskStatus.SUCCESS.getCode())
        {
            return true;
        }
        return false;
    }
    
    private void finishTaskBecaseObjectExists(FileObject fileObject) throws RealTimeCopyTaskException
    {
        if (StringUtils.isBlank(fileObject.getSha1()))
        {
            String message = RealTimeCopyTaskTool.LOG_TAG + " Task [" + this.realTimeCopyTask.getTaskId()
                + "] object exists but sha1 is null";
            this.realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.DEFAULT.getErrorCode());
            throw new RealTimeCopyTaskException(message);
        }
        
        RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + " Task [" + this.realTimeCopyTask.getTaskId()
            + "] objectSha1:" + fileObject.getSha1());
        this.realTimeCopyTask.setMd5(RealTimeCopyTaskTool.getMD5(fileObject.getSha1()));
        this.realTimeCopyTask.setBlockMD5(RealTimeCopyTaskTool.getBlockMD5(fileObject.getSha1()));
        this.realTimeCopyTask.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
    }
    
    private boolean processPartTask(RealTimeCopyTaskPart part) throws NumberFormatException, FSException,
        RealTimeCopyTaskException
    {
        ObjectDownloadURL downloadUrl = this.getDownloadUrl();
        
        if (downloadUrl == null)
        {
            String message = "FileObject [ " + this.realTimeCopyTask.getDestObjectId()
                + "] getDownloadUrl failed,with null";
            part.setStatus(RealTimeCopyTaskStatus.FAILED.getCode());
            throw new RealTimeCopyTaskException(message);
        }
        this.realTimeCopyTask.setErrorCode(downloadUrl.getErrorCode());
        
        // 原文件不存在,就删除任务。
        if (downloadUrl.getErrorCode() == RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode())
        {
            String message = "Object Not exists";
            throw new RealTimeCopyTaskException(message);
        }
        // 当网络异常、DSS不可用，则任务失败。
        if (downloadUrl.getErrorCode() == RealTimeCopyTaskError.DSS_UNAVAILABILITY.getErrorCode()
            || downloadUrl.getErrorCode() == RealTimeCopyTaskError.INTERNAL_SERVER_ERROR.getErrorCode())
        {
            String message = "Get DownloadUrl failed,error code:" + downloadUrl.getErrorCode();
            part.setStatus(RealTimeCopyTaskStatus.FAILED.getCode());
            throw new RealTimeCopyTaskException(message);
        }
        try
        {
            MultipartPart multiPart = this.executeCopyTaskPart(part, downloadUrl);
            
            RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + "ths task ["
                + this.realTimeCopyTask.getTaskId() + "] part [" + multiPart.getPartId()
                + "] execute successfully.");
        }
        catch (RealTimeCopyTaskException e)
        {
            String message = RealTimeCopyTaskTool.LOG_TAG + "ths task [" + this.realTimeCopyTask.getTaskId()
                + "] down part occur error.";
            RealTimePrinter.error(message, e);
            part.setStatus(RealTimeCopyTaskStatus.FAILED.getCode());
            return false;
        }
        
        part.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
        
        return true;
    }
    
    /**
     * 执行分片上传
     * 
     * @param downloadUrl
     * 
     * @return
     * @throws FSException
     * @throws NumberFormatException
     */
    private MultipartPart executeCopyTaskPart(RealTimeCopyTaskPart part, ObjectDownloadURL downloadUrl)
        throws RealTimeCopyTaskException, NumberFormatException, FSException
    {
        InputStream inputStream = null;
        try
        {
            Map<String, String> headers = new HashMap<String, String>(1);
            headers.put(RequestConstants.REQUEST_OBJECT_RANGE, "bytes=" + part.getPartRange());
            inputStream = realTimeClientService.download(downloadUrl.getUrl(), headers);
            
            String callbackKey = this.realTimeCopyTask.getTaskId() + ';' + part.getPartId();
            
            return multipartFileCopyFileObjectManager.createMultipartPart(this.realTimeCopyTask.getDestObjectId(),
                part.getSize(),
                callbackKey,
                Integer.parseInt(part.getPartId()),
                inputStream);
        }
        // 前置条件不满足可能两种情况，1.文件已经提交；2.在非上传过程中。说明已经提交成功了
        catch (PreconditionFailedException e)
        {
            // 已经提交成功了
            part.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
            return new MultipartPart(Integer.parseInt(part.getPartId()));
        }
        catch (ServiceException e)
        {
            String message = "Get Post Part InputStream Failed. [" + this.realTimeCopyTask.getTaskId() + ' '
                + part.getPartId() + " ] ";
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG + message, e);
            
            part.setStatus(RealTimeCopyTaskStatus.FAILED.getCode());
            
            // 这种情况下再进行下一片已经没多大意义。
            throw new RealTimeCopyTaskException(e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private MultipartFileObject executeByObjectAlreadyExist() throws RealTimeCopyTaskException
    {
        MirrorPrinter.info(RealTimeCopyTaskTool.LOG_TAG + "The task [" + realTimeCopyTask.getTaskId()
            + "] object [" + this.realTimeCopyTask.getDestObjectId() + "] exists");
        // 如果对象已经存在认为是成功的
        MultipartFileObject multipartFileObject = multipartFileObjectService.getMultipartUpload(this.realTimeCopyTask.getDestObjectId());
        if (null != multipartFileObject)
        {
            return multipartFileObject;
        }
        
        // 有可能multipartFileObject已经被删除,看看fileObject
        FileObject fileObject = fileObjectService.getFileObject(this.realTimeCopyTask.getDestObjectId());
        if (fileObject == null)
        {
            String message = "Init Task [" + this.realTimeCopyTask.getTaskId() + "] object ["
                + this.realTimeCopyTask.getDestObjectId() + "] not exists,invalid status";
            throw new RealTimeCopyTaskException(message);
        }
        
        this.finishTaskBecaseObjectExists(fileObject);
        
        return null;
    }
    
}
