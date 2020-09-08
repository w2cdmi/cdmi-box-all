/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.io.IOException;
import java.util.Date;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobActiveUtils;
import pw.cdmi.common.job.daemon.DaemonJobTask;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.filesystem.FSMultipartPart;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.io.MD5DigestInputStream;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.service.CallBackService;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * 
 * @author s90006125
 * 
 */
@Service("multipartFileObjectMergeTask")
public class MultipartFileObjectMergeTask extends DaemonJobTask<MultipartFileObject>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileObjectMergeTask.class);
    
    @Autowired
    private CallBackService callBackService;
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    @Autowired
    private DaemonJobActiveUtils daemonJobActiveUtils;
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    private MergeConfig mergeConfig = null;
    
    public void activeJob() throws JobException
    {
        daemonJobActiveUtils.activeJob(this.getJobDefinition().getJobName());
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record,
        MultipartFileObject multipartFileObject)
    {
        try
        {
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(multipartFileObject.getStoragePath());
            
            FileObject fileObject = fileObjectService.getFileObject(multipartFileObject.getObjectID());
            
            LOGGER.info("fileObject : {}", fileObject.logFormat());
            
            if(fileObject.getStatus()==FileObjectStatus.UPLOADING)
            {
                LOGGER.info("fileObject status is : {}", FileObjectStatus.UPLOADING);
                return;
            }
            
            multipartFileObject.setSha1(fileObject.getSha1());
            multipartFileObject.setObjectLength(fileObject.getObjectLength());
            
            if (this.getMergeConfig().needMerge(fileSystem.getFSManager().getDefinition()))
            {
                handleMerge(record, multipartFileObject, fileSystem);
            }
            else
            {
                LOGGER.info("object [ {} ] not need merge", multipartFileObject.getObjectID());
                if (StringUtils.isBlank(fileObject.getSha1()))
                {
                    onlyCalcSha1(fileSystem, multipartFileObject);
                    tryCallBack(multipartFileObject);
                }
                multipartFileObject.setStatus(FileObjectStatus.COMPLETED);
            }
            
            // 分片合并完后，删除分片对象
            if (FileObjectStatus.WAITCLEAR == multipartFileObject.getStatus())
            {
                multipartFileObjectService.updateMultipartUpload(multipartFileObject);
                // 更新fileobject表中的状态
                fileObjectService.updateFileObject(multipartFileObject);
            }
            else if(FileObjectStatus.COMPLETED == multipartFileObject.getStatus())
            {
                // 如果执行完成，则删除该数据
                multipartFileObjectService.clearMultipartUpload(multipartFileObject);
            }
            
            if (StringUtils.isNotBlank(record.getOutput()) && record.isSuccess())
            {
                String message = "object [ " + multipartFileObject.getObjectID() + ", "
                    + multipartFileObject.getUploadID() + " ] merge success.";
                LOGGER.info(message);
                record.setOutput(message);
            }
        }
        catch (RuntimeException e)
        {
            String message = "object [ " + multipartFileObject.getObjectID() + ", "
                + multipartFileObject.getUploadID() + " ] merge failed.";
            LOGGER.error(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw new InnerException(message, e);
        }
        catch (Exception e)
        {
            String message = "object [ " + multipartFileObject.getObjectID() + ", "
                + multipartFileObject.getUploadID() + " ] merge failed.";
            LOGGER.error(message, e);
            record.setSuccess(Boolean.FALSE);
            record.setOutput(message);
            throw new InnerException(message, e);
        }
    }
    
    private void handleMerge(JobExecuteRecord record, MultipartFileObject multipartFileObject,
        FileSystem<FSObject> fileSystem) throws FSException, IOException
    {
        MD5DigestInputStream inputStream = null;
        try
        {
            FSObject fsObject = fileSystem.transToFSObject(multipartFileObject.getStoragePath());
            
            SortedSet<FSMultipartPart<FSObject>> parts = fileSystem.multipartListParts(fsObject,
                multipartFileObject.getUploadID());
            
            int partSize = 0;
            if (null != parts)
            {
                partSize = parts.size();
            }
            
            fsObject = fileSystem.getObject(fsObject);
            if(null == fsObject)
            {
                LOGGER.warn("down load fsObject [ {} ] failed.", multipartFileObject.getStoragePath());
                return;
            }
            
            inputStream = new MD5DigestInputStream(fsObject.getInputStream());
            
            if (partSize < this.getMergeConfig().getMinPartSizeForMerge())
            {
                // 如果分片数小于配置的分片数，就不做合并，老的分片文件也不需要删除，计算完后，直接将状态设置为COMPLETED（2）
                onlyCalcSha1(inputStream, multipartFileObject);
                multipartFileObject.setStatus(FileObjectStatus.COMPLETED);
            }
            else
            {
                try
                {
                    FSObject origFsObject = (FSObject) fsObject.clone();
                    // 合并成新文件
                    if (!mergeFile(record,
                        multipartFileObject,
                        fileSystem,
                        inputStream,
                        fsObject,
                        origFsObject))
                    {
                        LOGGER.warn("merge file failed.");
                        return;
                    }
                    multipartFileObject.setStatus(FileObjectStatus.WAITCLEAR);
                }
                catch (CloneNotSupportedException e)
                {
                    String message = "fsObject [ " + fsObject.getPath() + " ] clone failed.";
                    LOGGER.warn(message);
                    record.setOutput(message);
                    record.setSuccess(Boolean.FALSE);
                    throw new InnerException(message, e);
                }
                finally
                {
                    IOUtils.closeQuietly(inputStream);
                }
            }
            
            // 处理成功
            multipartFileObject.setSha1(generalMD5(inputStream));
            tryCallBack(multipartFileObject);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private void tryCallBack(MultipartFileObject multipartFileObject)
    {
        try
        {
            callBackService.updateFileObject(multipartFileObject);
        }
        catch (Exception e)
        {
            // 回调失败不作处理
            LOGGER.error("call back for object [ {} ] failed.", multipartFileObject.getObjectID(), e);
        }
    }
    
    private void onlyCalcSha1(FileSystem<FSObject> fileSystem, MultipartFileObject multipartFileObject)
        throws IOException, FSException
    {
        MD5DigestInputStream inputStream = null;
        try
        {
            FSObject fsObject = fileSystem.transToFSObject(multipartFileObject.getStoragePath());
            
            fsObject = fileSystem.getObject(fsObject);
            if(null == fsObject)
            {
                LOGGER.warn("down load fsObject [ {} ] failed.", multipartFileObject.getStoragePath());
                return;
            }
            
            inputStream = new MD5DigestInputStream(fsObject.getInputStream());
            
            onlyCalcSha1(inputStream, multipartFileObject);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private void onlyCalcSha1(MD5DigestInputStream inputStream, MultipartFileObject multipartFileObject)
        throws IOException
    {
        byte[] buffer = new byte[1024 * 8];
        int len = -1;
        do
        {
            len = inputStream.read(buffer);
        } while (len != -1);
        
        // multipartFileObject.setObjectLength(inputStream.getLength());
        multipartFileObject.setStatus(FileObjectStatus.COMPLETED);
        multipartFileObject.setSha1(generalMD5(inputStream));
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private boolean mergeFile(JobExecuteRecord record, MultipartFileObject multipartFileObject,
        FileSystem<FSObject> fileSystem, MD5DigestInputStream inputStream, FSObject fsObject,
        FSObject origFsObject) throws FSException
    {
        fsObject.setObjectKey(fsObject.getObjectKey() + "_temp_merger");
        
        if (fileSystem.checkObjectExist(fsObject))
        {
            // 首先删除以前老的临时文件
            if (!fileSystem.doDelete(fsObject))
            {
                String message = "delete invalid temp file for object [ " + multipartFileObject.getObjectID()
                    + " ] on path [ " + fsObject.getPath() + " ] failed.";
                LOGGER.warn(message);
                record.setOutput(message);
                record.setSuccess(Boolean.FALSE);
                return false;
            }
        }
        
        // 生成临时的整体文件
        FSObject mergeObject = fileSystem.doPut(fsObject, inputStream);
        
        multipartFileObject.setObjectLength(mergeObject.getLength());
        
        // 将整体文件重名为正式的objectKey
        if (!fileSystem.renameTo(fsObject, origFsObject))
        {
            String message = "rename temp file for object [ " + multipartFileObject.getObjectID()
                + " ] on path [ " + fsObject.getPath() + " ] failed.";
            LOGGER.warn(message);
            record.setOutput(message);
            record.setSuccess(Boolean.FALSE);
            return false;
        }
        
        return true;
    }
    
    @Override
    public MultipartFileObject takeData()
    {
        // 2、获取一个 state=FileObjectStatus.COMMITTING and mergeAt > 超时时间 or mergeTimes < 最大合并次数的任务
        MultipartFileObject object = multipartFileObjectService.selectCommittingMultipartFileObject(this.getMergeConfig()
            .getTimeout(),
            this.getMergeConfig().getMaxMergeTimes());
        if (null == object)
        {
            LOGGER.warn("no object need meger.");
            return null;
        }
        
        // 3、更新mergeAt和mergeTimes
        Date now = new Date();
        object.setMergeAt(now);
        object.setMergeTimes(object.getMergeTimes() + 1);
        multipartFileObjectService.updateMultipartUpload(object);
        LOGGER.info("take object [ {} ]", object.logFormat());
        return object;
    }
    
    /**
     * 获取文件MD5值及文件片段MD5值, 格式"MD5:xxx;BlockMD5:yyy"
     * 
     * @param inputStream
     * @return
     */
    private String generalMD5(MD5DigestInputStream inputStream)
    {
        if (null == inputStream)
        {
            throw new InnerException("input stream is null");
        }
        StringBuffer buffer = new StringBuffer("MD5:");
        buffer.append(inputStream.getMd5()).append(";BlockMD5:").append(inputStream.getSamplingMD5());
        return buffer.toString();
    }
    
    @Override
    public boolean available(MultipartFileObject objects)
    {
        return null != objects;
    }
    
    private MergeConfig getMergeConfig()
    {
        if (null == this.mergeConfig)
        {
            mergeConfig = jsonMapper.fromJson(this.getParameter(), MergeConfig.class);
        }
        
        return mergeConfig;
    }
}
