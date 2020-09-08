package com.huawei.sharedrive.uam.log.service.impl;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.log.dao.UserLoginLogDAO;
import com.huawei.sharedrive.uam.log.service.UserLoginLogService;

import pw.cdmi.common.log.UserLog;

@Component("userLoginLogService")
public class UserLoginLogServiceImpl implements UserLoginLogService
{
    private class UserLogDispatcher implements Runnable
    {
        
        @Override
        public void run()
        {
            while (true)
            {
                doDispatch();
            }
        }
        
        private void dispatchEvent(UserLog userLog)
        {
            userLoginLogDAO.create(userLog);
        }
        
        private void doDispatch()
        {
            UserLog userLog;
            try
            {
                userLog = queue.take();
                dispatchEvent(userLog);
            }
            catch (InterruptedException e)
            {
                logger.debug("catch a InterruptedException", e);
            }
            
        }
        
    }
    
    public static final int WORKER_NUMBER = 2;
    
    private static final String INIT_CREATE_DAY_TABLES = "10";
    
    private static Logger logger = LoggerFactory.getLogger(UserLoginLogServiceImpl.class);
    
    private static LinkedBlockingQueue<UserLog> queue = new LinkedBlockingQueue<UserLog>(100000);
    
    private ExecutorService executorService;
    
    @Autowired
    private UserLoginLogDAO userLoginLogDAO;
    
    @PreDestroy
    public void close()
    {
        executorService.shutdown();
    }
    
    @PostConstruct
    public void init()
    {
        try
        {
            createTables(INIT_CREATE_DAY_TABLES);
            executorService = new ThreadPoolExecutor(WORKER_NUMBER, WORKER_NUMBER, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(WORKER_NUMBER));
            UserLogDispatcher task;
            for (int i = 0; i < WORKER_NUMBER; i++)
            {
                task = new UserLogDispatcher();
                executorService.execute(task);
            }
        }
        catch (Exception e)
        {
            logger.error("init failed", e);
        }
    }
    
    @Override
    public void saveLog(UserLog userLog)
    {
        try
        {
            UserLoginLogServiceImpl.queue.add(userLog);
        }
        catch (IllegalStateException e)
        {
            logger.warn("Fail to save user log", e);
        }
    }
    
    private void createTables(String days)
    {
        logger.info("Start createTables, days:" + days);
        int remain = Integer.parseInt(days);
        Calendar ca = Calendar.getInstance();
        userLoginLogDAO.createTable(ca.getTime());
        for (int i = 1; i < remain; i++)
        {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            userLoginLogDAO.createTable(ca.getTime());
        }
        logger.info("createTables complement.");
    }
    
}
