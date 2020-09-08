/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.db.dao.DataSourceInfo;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.model.FSObject;
import pw.cdmi.file.engine.filesystem.model.FileSystem;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;
import pw.cdmi.file.engine.object.domain.FileObjectStatus;
import pw.cdmi.file.engine.object.service.FileObjectDeleteService;

/**
 * 彻底删除文件的集群守护任务<br>
 * <br/>
 * DSS彻底删除文件的整体设计思路:<br/>
 * 1、ufm调用thrift接口发起删除指令，dss收到指令后，将原有fileobject表中的数据，移动到fileobject_delete_task表，只要移动成功，则视为删除成功，则向ufm响应成功；<br>
 * 2、DeleteFileObjectTask作为定时任务，从表fileobject_delete_task中读取任务，进行删除，删除成功后，移动到fileobject_delete_log_0表中，作为日志，永久保存<br>
 * 
 * 定时任务说明：<br>
 * 1、该定时任务作为集群定时任务，每60分钟执行一次，扫描20张fileobject_delete_task表（两个库，每个库10张表），每张表最多获取20条数据，批量执行删除操作。
 * 2、文件删除后，再保留一段时间，在dss才执行彻底删除，避免误删除，删除后的数据，默认保留30天，在定时任务的参数中配置。
 * 
 * 定时任务配置：25 4,34 0-6 * * ?<br>
 * {"tableCount":10,"timeout":60,"retryTimes":10,"reserveTime":30,"maxTimeConsuming":1500000, "limit":10}
 * 
 * @author s90006125
 *
 */
@Service("deleteFileObjectTask")
public class DeleteFileObjectTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileObjectTask.class);
    
    @Autowired
    private DataSourceInfo dataSourceInfo;
    
    @Autowired
    private FileObjectDeleteService fileObjectDeleteService;
    
    @Autowired
    private FileObjectAttachmentManager fileObjectAttachmentManager;
    
    private FileObjectDeleteConfig deleteConfig;
    
    private JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        // 任务开始时间
        long start = System.currentTimeMillis();

        FileObjectDeleteConfig config = getDeleteConfig();
        LOGGER.info("delete config is : {}", config.toString());
        
        resumeFailedTask(config);
        
        // config.getMaxTimeConsuming()，定义了一次定时任务执行，最大的耗时时常
        List<FileObjectDeleteTask> tasks = null;
        while((System.currentTimeMillis() - start) < config.getMaxTimeConsuming())
        {
            // 列举待删除数据
            tasks = listTask(config);
            if(null == tasks 
                || tasks.isEmpty())
            {
                LOGGER.info("no data wait for delete.");
                break;
            }
            
            for(FileObjectDeleteTask task : tasks)
            {
                try
                {
                    doDelete(task, config);
                }
                catch(Exception e)
                {
                    LOGGER.error("delete fileobject [{}] failed.", task.logFormat(), e);
                }
            }
            
            // 执行一轮删除后，等待一段时间，避免存储的删除压力过大
            trySleep(config);
        }
    }
    
    @SuppressWarnings("boxing")
    private void trySleep(FileObjectDeleteConfig config)
    {
        if(config.getDeleteWait() <= 0)
        {
            return;
        }
        try
        {
            Thread.sleep(config.getDeleteWait());
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("delete wait failed. {} ", config.getDeleteWait(), e);
        }
    }
    
    /**
     * 恢复删除失败的任务 以及 删除超时的任务
     * @param config
     */
    @SuppressWarnings("boxing")
    private void resumeFailedTask(final FileObjectDeleteConfig config)
    {
        LOGGER.info("start resume failed task");
        
        for(int i=1; i<=dataSourceInfo.getDbCount(); i++)
        {
            for(int j=0; j<config.getTableCount(); j++)
            {
                LOGGER.debug("resumeFailedTask in db : {}, table : {}", i, j);
                fileObjectDeleteService.resumeFailedFileObjectDeleteTask(i, j, config.getTimeout(), config.getRetryTimes());
            }
        }
        
        LOGGER.info("end resume failed task");
    }
    
    /**
     * 执行删除任务
     * @param task
     * @param config
     */
    private void doDelete(FileObjectDeleteTask task, FileObjectDeleteConfig config)
    {
        LOGGER.info("start real delete fileobject {}.", task.logFormat());
        
        try
        {
            FileSystem<FSObject> fileSystem = FileSystemFactory.getInstance(task.getStoragePath());
            FSObject srcFsObject = fileSystem.transToFSObject(task.getStoragePath());
            if(fileSystem.deleteObject(srcFsObject))
            {
                task.setStatus(FileObjectStatus.DELETESUCCESS);
            }
            else
            {
                task.setStatus(FileObjectStatus.DELETEFAILE);
            }
            
            fileObjectAttachmentManager.deleteFileObjectAttachment(task);
            
            LOGGER.info("delete fileobject [{}] success.", task.logFormat());
        }
        catch(FSException e)
        {
            LOGGER.warn("delete object [{}] failed. ", task.logFormat(), e);
            task.setStatus(FileObjectStatus.DELETEFAILE);
        }
        catch(Exception e)
        {
            LOGGER.warn("delete object [{}] failed. ", task.logFormat(), e);
            task.setStatus(FileObjectStatus.DELETEFAILE);
        }
        
        if(FileObjectStatus.DELETESUCCESS == task.getStatus()
            || task.getDeleteTimes() >= config.getRetryTimes())
        {
            // 删除成功，或者删除次数达到上限，则将删除任务移动到log表中
            fileObjectDeleteService.completeFileObjectDeleteTask(task);
        }
        else
        {
            // 删除失败，则只更新state
            fileObjectDeleteService.updateFileObjectDeleteTask(task);
        }
    }
    
    /**
     * 获取待删除的数据（状态为4，FileObjectStatus.WAITDELETE 的数据），并将列举的数据状态更新为（5，FileObjectStatus.STARTDELETE）
     * @param deleteConfig
     * @return
     */
    @SuppressWarnings("boxing")
    private List<FileObjectDeleteTask> listTask(final FileObjectDeleteConfig config)
    {
        LOGGER.info("start list task");
        List<FileObjectDeleteTask> tasks = new ArrayList<FileObjectDeleteTask>(config.getLimit() * dataSourceInfo.getDbCount() * config.getTableCount());
        List<FileObjectDeleteTask> temp = null;
        for(int i=1; i<=dataSourceInfo.getDbCount(); i++)
        {
            for(int j=0; j<config.getTableCount(); j++)
            {
                temp = fileObjectDeleteService.listFileObjectDeleteTask(i, j, config.getRetryTimes(), config.getReserveTime(), config.getLimit());
                if(null != temp)
                {
                    tasks.addAll(temp);
                }
                LOGGER.info("list delete task in db : {}, table : {}, tasks is : {}", i, j, tasks.size());
            }
        }
        
        List<FileObjectDeleteTask> result = markDeleteTasks(tasks);
        LOGGER.info("end list task, size: {}", null == result ? 0 : result.size());
        return result;
    }
    
    /**
     * 标记任务，将任务标记为正在执行状态，state=5(FileObjectStatus.STARTDELETE)，只有标记成功的，才执行删除
     * @param task
     * @return
     */
    private List<FileObjectDeleteTask> markDeleteTasks(List<FileObjectDeleteTask> tasks)
    {
        if(null == tasks
            || tasks.isEmpty())
        {
            return null;
        }
        
        List<FileObjectDeleteTask> result = new ArrayList<FileObjectDeleteTask>(tasks.size());
        
        Date modified = new Date();
        
        for(FileObjectDeleteTask task : tasks)
        {
            task.setStatus(FileObjectStatus.STARTDELETE);
            // 任务次数 +1
            task.setDeleteTimes(task.getDeleteTimes() + 1);
            task.setModified(modified);
            
            if(fileObjectDeleteService.markFileObjectDeleteTask(task))
            {
                result.add(task);
            }
        }
        
        return result;
    }
    
    /**
     * 获取任务配置信息
     * @return
     */
    private FileObjectDeleteConfig getDeleteConfig()
    {
        if (null == this.deleteConfig)
        {
            deleteConfig = jsonMapper.fromJson(this.getParameter(), FileObjectDeleteConfig.class);
        }
        
        return deleteConfig;
    }
}
