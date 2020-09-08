/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.service.FileObjectService;
import pw.cdmi.file.engine.object.service.MultipartFileObjectService;

/**
 * 分片任务清理定时任务，该定时任务执行两个事情：<br>
 * 1、清理掉无法完成合并的任务：删除，mergeAt > 超时时间 && mergeTimes >=最大合并次数的任务，或者state=FileObjectStatus.COMPLETED的任务，可以删除
 * 2、清理已经合并完成的分片（删除合并前的老文件）
 * 
 * {"timeout":72,"maxMergeTimes":3, "reserveTime":1440,"maxTimeConsuming":1500000,"limit":200}
 * 
 * 25 6,36 1-5 * * ?
 * 
 * @author s90006125
 *
 */
@Service("multipartFileObjectClearTask")
public class MultipartFileObjectClearTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileObjectClearTask.class);
    
    @Autowired
    private MultipartFileObjectService multipartFileObjectService;
    
    @Autowired
    private FileObjectService fileObjectService;
    
    private MultipartFileObjectClearConfig clearConfig;
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    @SuppressWarnings("boxing")
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        // 任务开始时间
        long start = System.currentTimeMillis();
        
        MultipartFileObjectClearConfig config = getClearConfig();
        LOGGER.info("clear config is : {}", config.toString());
        
        // 删除已经失效的分片任务（包括合并任务 + 清理任务）
        // 合并失败：mergeAt > 超时时间 && mergeTimes >=最大合并次数的任务 and state=FileObjectStatus.COMMITTING 或者state=FileObjectStatus.COMPLETED的任务，可以删除
        // 清理失败：clearTimes >= 最大清理次数 且 state=FileObjectStatus.WAITCLEAR 的任务
        multipartFileObjectService.deleteInvalidData(config.getTimeout(),
            config.getMaxMergeTimes(), config.getMaxClearTimes());
        
        List<MultipartFileObject> multipartFileObjects = null;
        
        while((System.currentTimeMillis() - start) < config.getMaxTimeConsuming())
        {
            multipartFileObjects = listWaitClearMultipartFileObject(config);
            if(null == multipartFileObjects
                || multipartFileObjects.isEmpty())
            {
                LOGGER.info("no data wait for delete.");
                break;
            }
            
            LOGGER.info("list {} data to clear.", multipartFileObjects.size());
            
            for(MultipartFileObject multipartFileObject : multipartFileObjects)
            {
                deleteRealMultipartObject(multipartFileObject);
            }
            
            // 等待一段时间，避免存储删除压力过大
            trySleep(config);
        }
    }
    
    @SuppressWarnings("boxing")
    private void trySleep(MultipartFileObjectClearConfig config)
    {
        if(config.getClearWait() <= 0)
        {
            return;
        }
        try
        {
            Thread.sleep(config.getClearWait());
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("clear wait failed. {} ", config.getClearWait(), e);
        }
    }
    
    /**
     * 删除合并前得分片文件
     * 
     * @param multipartFileObject
     */
    private void deleteRealMultipartObject(MultipartFileObject multipartFileObject)
    {
        // 更新清理次数，以及最后清理时间
        multipartFileObject.setClearTimes(multipartFileObject.getClearTimes() + 1);
        multipartFileObjectService.updateMultipartUpload(multipartFileObject);
        
        FileObject fileObject = fileObjectService.getFileObject(multipartFileObject.getObjectID());
        if(null == fileObject)
        {
            LOGGER.warn("fileobject : {} not exists.", multipartFileObject.getObjectID());
            return;
        }
        
        if(FileObjectStatus.WAITCLEAR != fileObject.getStatus())
        {
            LOGGER.warn("fileobject state is not WAITCLEAR(3).", fileObject.logFormat());
            return;
        }
        
        LOGGER.info("start clear multipartFileObject : {}", fileObject.logFormat());
        boolean clearResult = false;
        try
        {
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(multipartFileObject.getStoragePath());
            
            FSObject fsObject = fileSystem.transToFSObject(multipartFileObject.getStoragePath());
            
            if (fileSystem.deleteRealMultipartObject(fsObject))
            {
                LOGGER.info("clear multipart fileobject success : {} ", fileObject.logFormat());
                clearResult = true;
            }
            else
            {
                LOGGER.warn("clear multipart fileobject failed : {} ", fileObject.logFormat());
            }
        }
        catch(FSException e)
        {
            LOGGER.warn("clear multipart fileobject failed : {} ", fileObject.logFormat(), e);
        }
        catch(Exception e)
        {
            LOGGER.warn("clear multipart fileobject failed : {} ", fileObject.logFormat(), e);
        }
        
        if(clearResult)
        {
            // 如果清理成功，或者超过了最大的分片清理次数，则认为清理成功，删除分片元数据
            multipartFileObject.setSha1(fileObject.getSha1());
            multipartFileObject.setObjectLength(fileObject.getObjectLength());
            multipartFileObjectService.clearMultipartUpload(multipartFileObject);
        }
    }
    
    /**
     * 列举待清理的任务（已经合并完成，等待删除老的分片的任务）
     * @param config
     * @return
     */
    private List<MultipartFileObject> listWaitClearMultipartFileObject(MultipartFileObjectClearConfig config)
    {
        return multipartFileObjectService.selectWaitClearMultipartFileObject(config.getReserveTime(), config.getMaxClearTimes(), config.getLimit());
    }
    
    /**
     * 获取任务配置信息
     * @return
     */
    private MultipartFileObjectClearConfig getClearConfig()
    {
        if (null == this.clearConfig)
        {
            clearConfig = jsonMapper.fromJson(this.getParameter(), MultipartFileObjectClearConfig.class);
        }
        
        return clearConfig;
    }
}
