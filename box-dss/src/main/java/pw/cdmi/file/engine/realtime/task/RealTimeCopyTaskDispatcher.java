/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.task;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskTimeoutException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

@Component
public class RealTimeCopyTaskDispatcher implements Runnable, ApplicationContextAware, DisposableBean
{
    
    @Autowired
    private ThreadPoolTaskExecutor realTimeCopyTaskExecutor;
    
    private ApplicationContext applicationContext;
    
    @Autowired
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    private static String exeAgent = EnvironmentUtils.getHostName();
    
    private long realTimePartSize = SystemConfigContainer.getLong(SystemConfigKeys.REALTIME_DEFAULT_SPLIT_SIZE,
        RealTimeCopyTaskTool.DEFAULT_SPLIT_SIZE);
    
    private final static long SLEEP_TIME = 500;
    
    /**
     * 是否启动分发
     */
    private AtomicBoolean startBoolean = new AtomicBoolean(false);
    
    private volatile boolean shutdown = false;
    
    private void putTaskToThreadPool()
    {
        List<RealTimeCopyTask> taskList = null;
        RealTimeCopyTaskWorker realTimeCopyTaskWorker = null;
        
        taskList = realTimeCopyTaskService.queryWaitCopyTaskByAgent(exeAgent);
        
        if (CollectionUtils.isEmpty(taskList))
        {
            try
            {
                Thread.sleep(SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG + "RealTimeCopyTaskDispatcher sleep failt",
                    e);
            }
        }
        
        for (RealTimeCopyTask task : taskList)
        {
            if (this.applicationContext != null)
            {
                
                if (task.getNodeSize() > realTimePartSize)
                {
                    realTimeCopyTaskWorker = (RealTimeCopyTaskWorker) this.applicationContext.getBean("multipartRealTimeCopyTaskWorker");
                }
                else
                {
                    realTimeCopyTaskWorker = (RealTimeCopyTaskWorker) this.applicationContext.getBean("singleRealTimeCopyTaskWorker");
                }
                
                task.setStatus(RealTimeCopyTaskStatus.RUNNING.getCode());
                realTimeCopyTaskService.updateTaskStatus(task);
                
                realTimeCopyTaskWorker.setRealTimeCopyTask(task);
                RealTimePrinter.info("send the copy task to threadpool with id : " + task.getTaskId()
                    + " srcObjectId:" + task.getSrcObjectId() + "now date:" + System.currentTimeMillis());
                try
                {
                    realTimeCopyTaskExecutor.execute(realTimeCopyTaskWorker,
                        RealTimeCopyTaskTool.TASK_START_TIMEOUT);
                }
                catch (TaskTimeoutException e)
                {
                    RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG
                        + " start time out,and return to wait task pool", e);
                    this.taskStartFailedCallback(task);
                }
                
                RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + " put a task to pool successfully!");
                
            }
            else
            {
                RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG + "no tasks exsit");
                break;
            }
        }
    }
    
    public void start()
    {
        if (!startBoolean.compareAndSet(false, true))
        {
            return;
        }
        try
        {
            while (!shutdown)
            {
                putTaskToThreadPool();
            }
        }
        finally
        {
            startBoolean.set(false);
            RealTimePrinter.warn(RealTimeCopyTaskTool.LOG_TAG + "RealTimeCopyTaskManager Thread stoped");
        }
    }
    
    @Override
    public void run()
    {
        this.start();
        
    }
    
    public boolean isStart()
    {
        return startBoolean.get();
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void taskStartFailedCallback(RealTimeCopyTask task)
    {
        task.setStatus(RealTimeCopyTaskStatus.WAITING.getCode());
        realTimeCopyTaskService.updateTaskStatus(task);
    }
    
    @Override
    public void destroy()
    {
        this.shutdown = true;
    }
}
