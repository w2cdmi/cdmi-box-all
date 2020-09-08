package com.huawei.sharedrive.app.mirror.datamigration.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.job.Task;

/**
 * 2015-11-20 执行数据迁移扫描任务的线程池
 * 
 * @author c00287749
 * 
 */
public abstract class DataMigrationTaskAnalysisThreadPool
{
    private static final ExecutorService EXECUTORSERVICE = Executors.newCachedThreadPool();
    
    @SuppressWarnings("rawtypes")
    private static final List<ScheduledFuture> SCHEDULEDFUTURES = new ArrayList<ScheduledFuture>(20);
    
    /** 线程池 */
    private static final ScheduledExecutorService SCHEDULEDEXECUTORSERVICE = Executors.newScheduledThreadPool(20);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationTaskAnalysisThreadPool.class);
    
    private static final int MAX_THREAD_SIZE = 5;
    
    private static final int MIN_THREAD_SIZE = 1;
    
    private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<Runnable>(
        MAX_THREAD_SIZE);
    
    /**
     * 通用线程
     */
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(MIN_THREAD_SIZE,
        MAX_THREAD_SIZE, 10, TimeUnit.MINUTES, WORK_QUEUE);
    
    public static Future<?> execute(Callable<?> task)
    {
        return EXECUTORSERVICE.submit(task);
    }
    
    public static void execute(Runnable task)
    {
        EXECUTORSERVICE.execute(task);
    }
    
    public synchronized static boolean addTask(Runnable task)
    {
        if (THREAD_POOL_EXECUTOR.getQueue().size() > MAX_THREAD_SIZE)
        {
            LOGGER.warn("Add task failed. Queue size:{}, Max size:{}",
                THREAD_POOL_EXECUTOR.getQueue().size(),
                MAX_THREAD_SIZE);
            return false;
        }
        THREAD_POOL_EXECUTOR.execute(task);
        return true;
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