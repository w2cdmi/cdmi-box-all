/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskTimeoutException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

/**
 * @author w00186884
 */
@Component
public class CopyTaskDispatcherImpl implements CopyTaskDispatcher, ApplicationContextAware, DisposableBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskDispatcherImpl.class);
    
    @Autowired
    private ThreadPoolTaskExecutor copyTaskExecutor;
    
    private ApplicationContext applicationContext;
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    @Value("${mirro.split.size}")
    private long mirroSplitSize = CopyTaskTool.DEFAULT_SPLIT_SIZE;
    
    private static final int RETRY_NUMBER = 1;
    
    /**
     * 是否启动分发
     */
    private AtomicBoolean startBoolean = new AtomicBoolean(false);
    
    private volatile boolean shutdown = false;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    private static String exeAgent = EnvironmentUtils.getHostName();
    
    private static BlockingQueue<CopyTask> waitCopyList = new ArrayBlockingQueue<CopyTask>(
        CopyTaskTool.DEFAULT_TASK_QUEUE_SIZE);
    
    public boolean addCopyTask(CopyTask copyTask)
    {
        try
        {
            return waitCopyList.offer(copyTask, CopyTaskTool.WAIT_OFFERPOLL_TIME, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("CopyTaskReceiver Thread offer copy task occur error", e);
            return false;
        }
    }
    
    public int getQueueSize()
    {
        return waitCopyList.size();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void taskStartFailedCallback(CopyTask copyTask)
    {
        copyTask.setCopyStatus(CopyTaskStatus.INPUT.getCode());
        copyTaskService.unLockCopyTask(copyTask);
    }
    
    public void start()
    {
        if (!startBoolean.compareAndSet(false, true))
        {
            return;
        }
        
        try
        {
            this.firstStartWork();
            
            while (!shutdown)
            {
                tryExecuteTask();
            }
        }
        finally
        {
            startBoolean.set(false);
            LOGGER.warn("DefaultCopyTaskDispatcher Thread stoped");
        }
    }
    
    private void tryExecuteTask()
    {
        try
        {
            CopyTask copyTask = null;
            try
            {
                copyTask = waitCopyList.take();
            }
            catch (InterruptedException e)
            {
                this.threadSleep();
                LOGGER.warn(CopyTaskTool.LOG_TAG + "DefaultCopyTaskDispatcher take a task failed,"
                    + e.getMessage());
            }
            
            boolean isPutBack = false;
            if (copyTask != null)
            {
                // 根据异地复制总开关判断是否执行任务
                if (!systemConfigManager.isAllowCopyTaskRun())
                {
                    putBackCopyTask(copyTask);
                    LOGGER.info("mirror enable is false ,put queue tasks to db");
                    isPutBack = true;
                }
                
                if (!systemConfigManager.isAllowCopyTaskRunByTime())
                {
                    putBackCopyTask(copyTask);
                    LOGGER.info("mirror timer isnot false ,put queue tasks to db");
                    isPutBack = true;
                }
                
                // 根据异地复制总暂停开关判断是否执行任务
                if (systemConfigManager.isAllowCopyTaskPause())
                {
                    putBackCopyTask(copyTask);
                    LOGGER.info("mirror pause is open,put queue tasks to db");
                    isPutBack = true;
                }
                
                if (!isPutBack)
                {
                    this.executeTask(copyTask);
                }
                
            }
        }
        catch (Exception e)
        {
            LOGGER.error("DefaultCopyTaskDispatcher Thread occur error", e);
            this.threadSleep();
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void firstStartWork()
    {
        copyTaskService.resetAllCopyFailedTask(exeAgent);
    }
    
    private void executeTask(CopyTask copyTask)
    {
        // 当活动线程池小于最大线程池时去数据库获取任务,否则先进入休眠状态
        LOGGER.info(CopyTaskTool.LOG_TAG + "current thread pool info :ActiveCount:"
            + copyTaskExecutor.getActiveCount() + ",CorePoolSize" + copyTaskExecutor.getCorePoolSize()
            + ",PoolSize:" + copyTaskExecutor.getPoolSize() + ",MaxPoolSize:"
            + copyTaskExecutor.getMaxPoolSize());
        
        CopyTaskWorker taskWorker = null;
        
        // 重试大于50次之后放弃Copy
        if (copyTask.getRetryNum() >= CopyTaskTool.DEAFULT_RETRYNUM)
        {
            return;
        }
        
        if (copyTask.getSize() > mirroSplitSize && copyTask.getRetryNum() >= RETRY_NUMBER)
        {
            taskWorker = (CopyTaskWorker) this.applicationContext.getBean("multipartCopyTaskWorker");
        }
        else
        {
            taskWorker = (CopyTaskWorker) this.applicationContext.getBean("defaultCopyTaskWorker");
        }
        
        taskWorker.setCopyTask(copyTask);
        
        try
        {
            copyTaskExecutor.execute(taskWorker, CopyTaskTool.TASK_START_TIMEOUT);
        }
        catch (TaskTimeoutException e)
        {
            LOGGER.warn(CopyTaskTool.LOG_TAG + " " + copyTask.toString()
                + " start time out,and return to wait task pool", e);
            this.taskStartFailedCallback(copyTask);
        }
        
        LOGGER.info(CopyTaskTool.LOG_TAG + " put a task to pool successfully!");
    }
    
    private void threadSleep()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            LOGGER.error(CopyTaskTool.LOG_TAG + "The Copy Task Dispatcher Thread Sleep occur error", e);
        }
    }
    
    @Override
    public void run()
    {
        this.start();
    }
    
    @Override
    public boolean isStart()
    {
        return startBoolean.get();
    }
    
    @Override
    public void destroy()
    {
        this.shutdown = true;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    private void putBackCopyTask(CopyTask copyTask)
    {
        copyTask.setCopyStatus(CopyTaskStatus.INPUT.getCode());
        copyTaskService.unLockCopyTask(copyTask);
    }
    
}
