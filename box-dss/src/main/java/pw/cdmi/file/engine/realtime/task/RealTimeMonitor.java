/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.object.manager.FileObjectManager;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskCallbacker;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskPartService;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;

@Service("realTimeMonitor")
public class RealTimeMonitor extends QuartzJobTask
{
    @Autowired
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Autowired
    private RealTimeCopyTaskPartService realTimeCopyTaskPartService;
    
    @Autowired
    private FileObjectManager fileObjectManager;
    
    @Autowired
    private RealTimeCopyTaskDispatcher realTimeCopyTaskDispatcher;
    
    @Autowired
    private RealTimeCopyTaskCallbacker realTimeCopyTaskCallbacker;
    
    private static String exeAgent = EnvironmentUtils.getHostName();
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        
        // 删除源文件找不到的任务
        deleteNotFoundTasks();
        
        // 删除重试次数超过10次的任务
        deleteFailManyTimeTasks();
        
        // 重置超时、失败的任务
        realTimeCopyTaskService.resetUnSuccessTask(exeAgent);
        
        // 检查任务执行管理器是否启动
        if (!realTimeCopyTaskDispatcher.isStart())
        {
            new Thread(realTimeCopyTaskDispatcher).start();
            
        }
        
        // 删除回调成功的任务
        realTimeCopyTaskService.deleteSucessHistoryTask(exeAgent);
        realTimeCopyTaskPartService.deleteUnExistTaskPart();
        
        // 处理回调：1、回调长时间没有进行回调的任务
        callBackOverTimeTasks();
        // 處理回調：2、回调失败，错误码为文件找不到，删除文件，以及任务；错误码为内容不一致，删除文件，任务重置；其他错误码，再次回调
        reCallbackFailedTasks();
        // 其他节点的modifyTime超过1个小时未更新，直接让当前节点执行
        realTimeCopyTaskService.updateBrotherTaskStatus(exeAgent);
        
    }
    
    private void deleteFailManyTimeTasks()
    {
        
        List<RealTimeCopyTask> tasks = realTimeCopyTaskService.queryFailedManyTimeTasks(exeAgent);
        for (RealTimeCopyTask task : tasks)
        {
            task.setErrorCode(RealTimeCopyTaskError.FAIL_MANY_TIME.getErrorCode());
            // 特殊情况，失败超过10次的任务也放进回调线程，让ac删除在接受特殊错误码后，删除任务
            task.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
            realTimeCopyTaskService.updateTaskStatus(task);
            realTimeCopyTaskCallbacker.taskCallback(task);
        }
    }
    
    private void deleteNotFoundTasks()
    {
        List<RealTimeCopyTask> notFoundTasks = realTimeCopyTaskService.queryNotFoundTask(exeAgent);
        for (RealTimeCopyTask task : notFoundTasks)
        {
            if (task.getErrorCode() == RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode())
            {
                realTimeCopyTaskService.deleteTask(task);
            }
        }
    }
    
    private void reCallbackFailedTasks()
    {
        List<RealTimeCopyTask> callBackFailedTasks = realTimeCopyTaskService.queryCallBackFailedTask(exeAgent);
        for (RealTimeCopyTask task : callBackFailedTasks)
        {
            if (task.getErrorCode() == RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode())
            {
                fileObjectManager.deleteFileObject(task.getDestObjectId());
                realTimeCopyTaskService.deleteTask(task);
            }
            if (task.getErrorCode() == RealTimeCopyTaskError.CONTENT_ERROR.getErrorCode())
            {
                fileObjectManager.deleteFileObject(task.getDestObjectId());
                task.setBlockMD5(null);
                task.setMd5(null);
                task.setRetryNum(0);
                task.setErrorCode(null);
                task.setStatus(0);
                task.setNodeSize(0);
                realTimeCopyTaskService.updateTaskStatus(task);
            }
            
            // 其他错误直接丢回回调线程池，重新回调，回调之前需要重置任务状态和错误码
            task.setStatus(RealTimeCopyTaskStatus.SUCCESS.getCode());
            task.setErrorCode(RealTimeCopyTaskError.TASK_SUCCESS.getErrorCode());
            realTimeCopyTaskService.updateTaskStatus(task);
            realTimeCopyTaskCallbacker.taskCallback(task);
        }
    }
    
    private void callBackOverTimeTasks()
    {
        List<RealTimeCopyTask> waitCallBackTasks = realTimeCopyTaskService.queryCallBackTask(exeAgent);
        for (RealTimeCopyTask task : waitCallBackTasks)
        {
            realTimeCopyTaskCallbacker.taskCallback(task);
        }
    }
    
}
