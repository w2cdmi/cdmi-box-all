/**
 * 
 */
package pw.cdmi.file.engine.mirro.monitor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author w00186884
 * 
 */
public class CopyTaskMonitor
{
    private static AtomicLong waitingTask = new AtomicLong(0);
    
    private static AtomicLong runningTask = new AtomicLong(0);
    
    private static AtomicLong successTask = new AtomicLong(0);
    
    private static AtomicLong failedTask = new AtomicLong(0);
    
    private static AtomicLong callbackSuccessTask = new AtomicLong(0);
    
    private static AtomicLong callCackFailedTask = new AtomicLong(0);
    
    /**
     * 任务连续失败次数
     */
    private static AtomicLong successiveFailedNum = new AtomicLong(0);
    
    public static AtomicLong getWaitingTask()
    {
        return waitingTask;
    }
    
    public static void setWaitingTask(AtomicLong waitingTask)
    {
        CopyTaskMonitor.waitingTask = waitingTask;
    }
    
    public static AtomicLong getRunningTask()
    {
        return runningTask;
    }
    
    public static void setRunningTask(AtomicLong runningTask)
    {
        CopyTaskMonitor.runningTask = runningTask;
    }
    
    public static AtomicLong getSuccessTask()
    {
        return successTask;
    }
    
    public static void setSuccessTask(AtomicLong successTask)
    {
        CopyTaskMonitor.successTask = successTask;
    }
    
    public static AtomicLong getFailedTask()
    {
        return failedTask;
    }
    
    public static void setFailedTask(AtomicLong failedTask)
    {
        CopyTaskMonitor.failedTask = failedTask;
    }
    
    public static AtomicLong getCallbackSuccessTask()
    {
        return callbackSuccessTask;
    }
    
    public static void setCallbackSuccessTask(AtomicLong callbackSuccessTask)
    {
        CopyTaskMonitor.callbackSuccessTask = callbackSuccessTask;
    }
    
    public static AtomicLong getCallCackFailedTask()
    {
        return callCackFailedTask;
    }
    
    public static void setCallCackFailedTask(AtomicLong callCackFailedTask)
    {
        CopyTaskMonitor.callCackFailedTask = callCackFailedTask;
    }
    
    public static AtomicLong getSuccessiveFailedNum()
    {
        return successiveFailedNum;
    }
    
    public static void setSuccessiveFailedNum(AtomicLong successiveFailedNum)
    {
        CopyTaskMonitor.successiveFailedNum = successiveFailedNum;
    }
    
    @Override
    public String toString()
    {
        return "CopyTaskMonitor [waitingTask=" + waitingTask.longValue() + "],[runningTask=" + runningTask.longValue() + "],[successTask=" + successTask.longValue() + "],[failedTask=" + failedTask.longValue() + "],[callbackSuccessTask=" + callbackSuccessTask.longValue() + "],[callCackFailedTask="
            + callCackFailedTask.longValue() + "]";
    }
}
