/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

/**
 * @author w00186884 批量提交线程,单线程跑
 */
@Component
public class CopyTaskReceiverImpl implements CopyTaskRecevier
{
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    private static BlockingQueue<CopyTask> copyTasks = new ArrayBlockingQueue<CopyTask>(CopyTaskTool.DEFAULT_TASK_QUEUE_SIZE);
    
    private AtomicBoolean startBoolean = new AtomicBoolean(false);
    
    private volatile boolean shutdown = false;
    
    @Override
    public void run()
    {
        
        if (!startBoolean.compareAndSet(false, true))
        {
            return;
        }
        
        try
        {
            while (!shutdown)
            {
                tryCommitTask();
            }
        }
        finally
        {
            startBoolean.set(false);
            MirrorPrinter.warn("CopyTaskReceiver Thread stoped");
        }
    }
    
    private void tryCommitTask()
    {
        try
        {
            this.commitTask();
        }
        catch (Exception e)
        {
            MirrorPrinter.error("CopyTaskReceiver Thread occur error", e);
            this.threadSleep();
        }
    }
    
    private void commitTask()
    {
        List<CopyTask> copyTaskList = new ArrayList<CopyTask>(10);
        // 当满足50个任务或者是等待超过1s就批量提交
        CopyTask copyTask = null;
        for (int i = 0; i < CopyTaskTool.BATCH_COMMIT_NUM; i++)
        {
            copyTask = tryPollTask();
            if (copyTask == null)
            {
                break;
            }
            copyTaskList.add(copyTask);
        }
        
        if (copyTaskList.isEmpty())
        {
            this.threadSleep();
        }
        else
        {
            // 提交入库
            this.batchInsert(copyTaskList);
        }
    }
    
    private CopyTask tryPollTask()
    {
        try
        {
            return copyTasks.poll(CopyTaskTool.WAIT_OFFERPOLL_TIME, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            MirrorPrinter.error("CopyTaskReceiver Thread pool copy task occur error,ignore this error", e);
        }
        return null;
    }
    
    @Override
    public boolean addCopyTask(CopyTask copyTask)
    {
        try
        {
            return copyTasks.offer(copyTask, CopyTaskTool.WAIT_OFFERPOLL_TIME, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            MirrorPrinter.error("CopyTaskReceiver Thread offer copy task occur error", e);
            return false;
        }
    }
    
    private void threadSleep()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            MirrorPrinter.error("CopyTaskReceiver Thread sleep occur error");
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchInsert(List<CopyTask> copyTaskList)
    {
        copyTaskService.batchInsertOrReplace(copyTaskList);
        MirrorPrinter.info("CopyTaskReceiver Thread insert task batch " + copyTaskList.size());
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
}
