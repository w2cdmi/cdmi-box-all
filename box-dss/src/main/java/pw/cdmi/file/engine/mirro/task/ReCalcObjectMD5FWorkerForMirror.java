package pw.cdmi.file.engine.mirro.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.manager.FileObjectManager;

/**
 * 重新计算MD5
 * 
 * @author c00287749
 * 
 */
public class ReCalcObjectMD5FWorkerForMirror implements Runnable
{
    private CopyTaskService copyTaskService;
    
    private FileObjectManager fileObjectManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReCalcObjectMD5FWorkerForMirror.class);
    
    private CopyTask task;
    
    private void initBean()
    {
        copyTaskService = (CopyTaskService) SpringContextUtil.getBean("copyTaskService");
        fileObjectManager = (FileObjectManager) SpringContextUtil.getBean("fileObjectManager");
    }
    
    public ReCalcObjectMD5FWorkerForMirror(CopyTask task)
    {
        this.task = task;
    }
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        initBean();
        
        if (null == task)
        {
            LOGGER.info("the copytask is null");
            return;
        }
        
        calcCopyObjectMd5(task);
        
    }
    
    private  void calcCopyObjectMd5(CopyTask task)
    {
        try
        {
            if(CopyTaskStatus.SUCCESS.getCode() != task.getCopyStatus())
            {
                LOGGER.info("task not finish");
            }
            
            if (StringUtils.isNotBlank(task.getMd5()) || StringUtils.isNotBlank(task.getBlockMD5()))
            {
                LOGGER.info("task.getMd5() is not null,don't need reCalcObjectMark");
                return;
            }
            
            if(null == task.getModifiedAt())
            {
                LOGGER.info("task.getModifiedAt() is null ,return");
                return;
            }
            

            if( DateUtils.addHours(task.getModifiedAt(), 2).getTime() > new Date().getTime())
            {
                LOGGER.info("The now time is  not over 2 hours than task.getModifiedAt() ," +task.getModifiedAt());
                return;
            }
            
            FileObject fileObject = fileObjectManager.reCalcObjectMark(task.getDestObjectId());
            LOGGER.info("calcCopyObjectMd5 succeess,object id is:"+fileObject.getSha1());
            copyTaskService.updateMD5ByObjectId(fileObject);
            
        }catch(Exception e)
        {
                LOGGER.error("calcCopyObjectMd5 Job execute failed," + e.getMessage(), e);
        }
    }
}
