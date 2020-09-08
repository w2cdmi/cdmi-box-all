package pw.cdmi.file.engine.core.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ThreadPool
{
    private ThreadPool()
    {
    }
    
    private static final ExecutorService EXECUTORSERVICE = Executors.newCachedThreadPool();
    
    @SuppressWarnings("rawtypes")
    private static final List<ScheduledFuture> SCHEDULEDFUTURES = new ArrayList<ScheduledFuture>(20);
    
    /** 线程池 */
    private static final ScheduledExecutorService SCHEDULEDEXECUTORSERVICE = Executors.newScheduledThreadPool(20);
    
    public static Future<?> execute(Callable<?> task)
    {
        return EXECUTORSERVICE.submit(task);
    }
    
    public static void execute(Runnable task)
    {
        EXECUTORSERVICE.execute(task);
    }
    
    /**
     * 定时执行
     * 
     * @param command
     * @param initialDelay
     * @param period
     * @param unit <br>
     *            创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；也就是将在 initialDelay 后开始执行，然后在
     *            initialDelay+period 后执行，接着在 initialDelay + 2 * period 后执行，依此类推。
     */
    @SuppressWarnings("rawtypes")
    public static ScheduledFuture scheduleAtFixedRate(Task command, long initialDelay, long period,
        TimeUnit unit)
    {
        ScheduledFuture sf = SCHEDULEDEXECUTORSERVICE.scheduleAtFixedRate(command, initialDelay, period, unit);
        SCHEDULEDFUTURES.add(sf);
        return sf;
    }
    
    /**
     * 定时执行
     * 
     * @param command
     * @param initialDelay
     * @param period
     * @param unit <br>
     *            创建并执行一个在给定初始延迟后首次启用的定期操作，随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。
     */
    @SuppressWarnings("rawtypes")
    public static ScheduledFuture scheduleWithFixedDelay(Task command, long initialDelay, long period,
        TimeUnit unit)
    {
        ScheduledFuture sf = SCHEDULEDEXECUTORSERVICE.scheduleWithFixedDelay(command,
            initialDelay,
            period,
            unit);
        SCHEDULEDFUTURES.add(sf);
        return sf;
    }
    
    @SuppressWarnings("rawtypes")
    public static void showdown()
    {
        EXECUTORSERVICE.shutdown();
        
        for (ScheduledFuture sf : SCHEDULEDFUTURES)
        {
            sf.cancel(true);
        }
    }
}
