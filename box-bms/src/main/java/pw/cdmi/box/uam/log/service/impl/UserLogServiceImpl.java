package pw.cdmi.box.uam.log.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

import pw.cdmi.box.uam.log.dao.UserLogDAO;
import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogListRsp;
import pw.cdmi.box.uam.log.domain.UserLogRes;
import pw.cdmi.box.uam.log.domain.UserLogType;
import pw.cdmi.box.uam.log.service.UserLogService;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.DateUtils;

@Component("userLogService")
public class UserLogServiceImpl implements UserLogService
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
            userLogDAO.create(userLog);
        }
        
        private void doDispatch()
        {
            UserLog userLog;
            try
            {
                userLog = queue.take();
                dispatchEvent(userLog);
            }
            catch (Exception e)
            {
                logger.debug("catch a InterruptedException", e);
            }
            
        }
        
    }
    
    public static final int WORKER_NUMBER = 2;
    
    private static final String INIT_CREATE_DAY_TABLES = "10";
    
    private static Logger logger = LoggerFactory.getLogger(UserLogServiceImpl.class);
    
    private static LinkedBlockingQueue<UserLog> queue = new LinkedBlockingQueue<UserLog>(100000);
    
    private static final String TABLE_PREFIX = "user_log_";
    
    private static List<String> getRecentTables()
    {
        List<String> tableList = new ArrayList<String>(10);
        Calendar ca = Calendar.getInstance();
        String tableName;
        for (int i = 0; i < 30; i++)
        {
            tableName = TABLE_PREFIX
                + DateUtils.dateToString(ca.getTime(), UserLogDAO.EVENT_LOG_DATE_PATTERN);
            tableList.add(tableName);
            ca.add(Calendar.DAY_OF_MONTH, -1);
        }
        return tableList;
    }
    
    @Autowired
    private CreateUserLogTablesTask createUserLogTablesTask;
    
    private ExecutorService executorService;
    
    @Autowired
    private UserLogDAO userLogDAO;
    
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
            createUserLogTablesTask.createTables(INIT_CREATE_DAY_TABLES);
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
    public UserLogListRsp queryLogs(UserLogListReq req)
    {
        List<String> tableList = getRecentTables();
        if (null != req.getBeginTime())
        {
            req.setBeginTime(req.getBeginTime() / 1000);
        }
        if (null != req.getEndTime())
        {
            req.setEndTime(req.getEndTime() / 1000);
        }
        long total = 0;
        List<UserLog> userLogList = new ArrayList<UserLog>(16);
        long tmpOffset = req.getOffset();
        int tmpLimit = req.getLimit();
        long tableMatchSize;
        for (int i = 0; i < tableList.size(); i++)
        {
            tableMatchSize = 0;
            try
            {
                tableMatchSize = userLogDAO.getTotals(req, tableList.get(i));
            }
            catch (Exception e)
            {
                if (e.getMessage().indexOf("doesn't exist") != -1)
                {
                    logger.warn("doesn't exist:" + e.getMessage());
                    break;
                }
                logger.error("", e);
            }
            total += tableMatchSize;
            if (tmpLimit <= 0)
            {
                continue;
            }
            if (tableMatchSize <= tmpOffset)
            {
                tmpOffset -= tableMatchSize;
                continue;
            }
            try
            {
                List<UserLog> tempList = userLogDAO.getUserLogList(req, tableList.get(i), tmpOffset, tmpLimit);
                userLogList.addAll(tempList);
                tmpLimit = tmpLimit - tempList.size();
                tmpOffset = 0;
            }
            catch (RuntimeException e)
            {
                logger.warn("queryLogs error");
                break;
            }
        }
        
        List<UserLogRes> resultList = new ArrayList<UserLogRes>(req.getLimit());
        for (UserLog tmpUserLog : userLogList)
        {
            resultList.add(UserLogRes.builderUserLogRes(tmpUserLog));
        }
        
        UserLogListRsp response = new UserLogListRsp();
        response.setLimit(req.getLimit());
        response.setOffset(req.getOffset());
        response.setTotalCount(total);
        response.setUserLogs(resultList);
        return response;
    }
    
    @Override
    public void saveFailLog(String loginName, String appId, String[] params, UserLogType logType)
    {
        try
        {
            boolean isContinue = unsaveLog(logType);
            if (!isContinue)
            {
                return;
            }
            UserLog userLog = new UserLog();
            userLog.setAppId(appId);
            if (loginName != null && loginName.length() > 255)
            {
                userLog.setKeyword(loginName);
            }
            else
            {
                userLog.setLoginName(loginName);
            }
            userLog.setDetail(logType.getDetails(params));
            userLog.setId(UUID.randomUUID().toString());
            userLog.setCreatedAt(new Date());
            userLog.setType(logType.getTypeCode());
            UserLogServiceImpl.queue.add(userLog);
            // this.saveUserLog(userLog);
        }
        catch (IllegalStateException e)
        {
            logger.info("Fail to record logger", e);
        }
    }
    
    @Override
    public void saveFailLog(String appId, UserLogType logType)
    {
        boolean isContinue = unsaveLog(logType);
        if (!isContinue)
        {
            return;
        }
        try
        {
            UserLog userLog = new UserLog();
            userLog.setAppId(appId);
            userLog.setDetail(logType.getDetails());
            userLog.setId(UUID.randomUUID().toString());
            userLog.setCreatedAt(new Date());
            userLog.setType(logType.getTypeCode());
            UserLogServiceImpl.queue.add(userLog);
        }
        catch (Exception e)
        {
            logger.warn("Fail to save user log", e);
        }
        
    }
    
    @Override
    public void saveUserLog(UserLog userLog, UserLogType logType, String[] params)
    {
        boolean isContinue = unsaveLog(logType);
        if (!isContinue)
        {
            return;
        }
        try
        {
            userLog.setDetail(logType.getDetails(params));
            userLog.setType(logType.getTypeCode());
            UserLogServiceImpl.queue.add(userLog);
        }
        catch (IllegalStateException e)
        {
            logger.warn("Fail to save user log", e);
        }
    }
    
    private boolean unsaveLog(UserLogType logType)
    {
        if (logType == null || !logType.isEnable())
        {
            return false;
        }
        return true;
    }
    
}
