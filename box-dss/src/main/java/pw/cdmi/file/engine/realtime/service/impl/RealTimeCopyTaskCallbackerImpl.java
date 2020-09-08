/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskCallbacker;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.task.RealTimeCallbackWorker;
import pw.cdmi.file.engine.realtime.task.RealTimeReCalcObjectMD5Worker;

@Service("realTimeCopyTaskCallbacker")
public class RealTimeCopyTaskCallbackerImpl implements RealTimeCopyTaskCallbacker
{
    @Resource
    private ThreadPoolTaskExecutor realTimeCopyTaskExecutor;
    
    @Resource
    private ThreadPoolTaskExecutor realTimeReCalcObjectMD5Executor;
    
    @Autowired
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Override
    public void taskCallback(RealTimeCopyTask realTimeCopyTask)
    {
        if (null == realTimeCopyTask)
        {
            return;
        }
        
        if (StringUtils.isBlank(realTimeCopyTask.getMd5())
            && realTimeCopyTask.getErrorCode() != RealTimeCopyTaskError.FAIL_MANY_TIME.getErrorCode())
        {
            RealTimePrinter.info("The real time copy task " + realTimeCopyTask.getTaskId() + ", object "
                + realTimeCopyTask.getDestObjectId()
                + " not md5 or block md5, don't need report, need to re calculate MD5.");
            
            RealTimeReCalcObjectMD5Worker realTimeReCalcObjectMD5Worker = new RealTimeReCalcObjectMD5Worker(
                realTimeCopyTask);
            realTimeReCalcObjectMD5Executor.execute(realTimeReCalcObjectMD5Worker);
            
            realTimeCopyTaskService.updateTaskStatus(realTimeCopyTask);
            return;
        }
        
        RealTimeCallbackWorker callbackWorker = new RealTimeCallbackWorker(realTimeCopyTask);
        realTimeCopyTaskExecutor.execute(callbackWorker);
    }
}
