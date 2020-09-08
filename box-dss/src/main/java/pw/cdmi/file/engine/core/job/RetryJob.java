/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.file.engine.core.job;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.log.LoggerUtil;

/**
 * 可重试的任务
 * 
 * @author s90006125
 * 
 */
public class RetryJob implements Callable<Boolean>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryJob.class);
    
    private int retry;
    
    private long interval = -1;
    
    private Executor executor;
    
    private Throwable t;
    
    /**
     * @param retry 重试次数
     * @param executor 任务执行器
     */
    public RetryJob(int retry, Executor executor)
    {
        this.retry = retry;
        this.executor = executor;
    }
    
    /**
     * @param retry 重试次数
     * @param interval 重试之间的间隔
     * @param executor 任务执行器
     */
    public RetryJob(int retry, long interval, Executor executor)
    {
        this(retry, executor);
        this.interval = interval;
    }
    
    /**
     * 获取任务执行过程中，捕获到的异常
     * 
     * @return
     */
    public Throwable getException()
    {
        return this.t;
    }
    
    /**
     * 执行任务，返回任务执行结果
     * 
     * @return
     */
    public Boolean execute()
    {
        for (int i = 0; i < retry; i++)
        {
            t = this.executor.execute();
            
            if (null == t)
            {
                // 表示会执行成功
                break;
            }

            sleep();
        }
        
        return (null == t);
    }
    
    @Override
    public Boolean call()
    {
        LoggerUtil.regiestThreadLocalLog();
        return execute();
    }
    
    /**
     * 任务执行器
     * 
     * @author s90006125
     * 
     */
    public static interface Executor
    {
        /**
         * 如果该方法返回Null，表示任务执行成功，如果返回非空，表示任务执行失败
         * 
         * @return
         */
        Throwable execute();
    }
    
    private void sleep()
    {
        if (this.interval <= 0)
        {
            return;
        }
        
        try
        {
            Thread.sleep(this.interval);
        }
        catch (InterruptedException e)
        {
            LOGGER.warn(e.getMessage());
        }
    }
}