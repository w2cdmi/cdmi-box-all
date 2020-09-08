/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.manager.FileObjectManager;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;

public class RealTimeReCalcObjectMD5Worker implements Runnable
{
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    private FileObjectManager fileObjectManager;
    
    private RealTimeCopyTask task;
    
    public RealTimeReCalcObjectMD5Worker(RealTimeCopyTask task)
    {
        this.task = task;
    }
    
    private void initBean()
    {
        realTimeCopyTaskService = (RealTimeCopyTaskService) SpringContextUtil.getBean("realTimeCopyTaskService");
        fileObjectManager = (FileObjectManager) SpringContextUtil.getBean("fileObjectManager");
    }
    
    @Override
    public void run()
    {
        LoggerUtil.regiestThreadLocalLog();
        if (null == task)
        {
            RealTimePrinter.info("The RealTime Copytask Is Null");
            return;
        }
        
        initBean();
        
        calcRealTimeCopyObjectMD5(task);
    }
    
    private void calcRealTimeCopyObjectMD5(RealTimeCopyTask realTimeCopyTask)
    {
        try
        {
            if (StringUtils.isNotBlank(realTimeCopyTask.getMd5()) || StringUtils.isNotBlank(realTimeCopyTask.getBlockMD5()))
            {
                RealTimePrinter.info("task.getMd5() is not null,don't need reCalcObjectMark");
                return;
            }
            
            if(null == realTimeCopyTask.getModifyTime())
            {
                RealTimePrinter.info("task.getModifiedAt() is null ,return");
                return;
            }      
            
            if (DateUtils.addHours(realTimeCopyTask.getModifyTime(), 2).getTime() > new Date().getTime())
            {
                RealTimePrinter.info("The now time is  not over 2 hours than task.getModifiedAt() ,"
                    + realTimeCopyTask.getModifyTime());
                return;
            }
            
            FileObject fileObject = fileObjectManager.reCalcObjectMark(realTimeCopyTask.getDestObjectId());
            RealTimePrinter.info("calcRealTimeCopyObjectMd5 succeess,object id is:" + fileObject.getSha1());
            
            realTimeCopyTaskService.updateMD5ByObjectId(fileObject);
        }
        catch (Exception e)
        {
            RealTimePrinter.error("calcRealTimeCopyObjectMd5 Job execute failed," + e.getMessage(), e);
        }
    }
    
   
}
