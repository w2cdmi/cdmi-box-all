/**
 * 
 */
package com.huawei.sharedrive.app.log.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.log.dao.UserLogDAO;
import com.huawei.sharedrive.app.log.service.UserLogService;
import com.huawei.sharedrive.app.logconfig.listener.LogListener;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListReq;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListRsp;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogRes;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.DateUtils;

/**
 * @author s00108907
 * 
 */
@Component
public class UserLogServiceImpl implements UserLogService
{
    // 删除保留时间前多少天的日志
    private static final int CLEAN_DAYS = Integer.parseInt(PropertiesUtils.getProperty("event.log.clean.days",
        "10"));
    
    private static final String INIT_CREATE_DAY_TABLES = "10";
    
    private static Logger logger = LoggerFactory.getLogger(UserLogServiceImpl.class);
    
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
    
    private ExecutorService taskPool;
    
    @Autowired
    private UserLogDAO userLogDAO;
    
    @Autowired
    private CreateUserLogTablesTask createUserLogTablesTask;
    
    private static final String TABLE_PREFIX = "user_log_";
    
    private static List<String> getRecentTables()
    {
        List<String> tableList = new ArrayList<String>(10);
        Calendar ca = Calendar.getInstance();
        String tableName = null;
        for (int i = 0; i < 30; i++)
        {
            tableName = TABLE_PREFIX
                + DateUtils.dateToString(ca.getTime(), UserLogDAO.EVENT_LOG_DATE_PATTERN);
            tableList.add(tableName);
            ca.add(Calendar.DAY_OF_MONTH, -1);
        }
        return tableList;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchWriteItem(UserLog data)
    {
        userLogDAO.create(data);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchWriteItems(List<UserLog> infos)
    {
        LinkedList<UserLog> tempInfos = null;
        if (infos instanceof LinkedList)
        {
            tempInfos = (LinkedList<UserLog>) infos;
        }
        if (tempInfos == null || tempInfos.size() <= 0)
        {
            return;
        }
        
        UserLog data = tempInfos.poll();
        while (data != null)
        {
            userLogDAO.create(data);
            data = tempInfos.poll();
        }
    }
    
    @PreDestroy
    public void close()
    {
        taskPool.shutdown();
    }
    
    @Override
    public void consumeEvent(Event event)
    {
        if (!LogListener.isEnable())
        {
            return;
        }
        try
        {
            if (event.getOptType() == null)
            {
                return;
            }
            if (!event.getOptType().isEnable())
            {
                return;
            }
            LoggerCacheWriter.writeLog(event);
        }
        catch (IOException e)
        {
            logger.warn("Can not write the log, write to the db directly.", e);
            try
            {
                userLogDAO.create(event.convertToUserLog());
            }
            catch (Exception e1)
            {
                logger.warn("Can not write to db directly", e1);
            }
        }
    }
    
    @Override
    public void dataClean(String remainDays)
    {
        logger.info("Start clean event log before, remainDays:" + remainDays);
        int remain = Integer.parseInt(remainDays);
        long curTime = System.currentTimeMillis();
        long durTimes = 0L;
        long toDeleteTime = 0L;
        Date toDeleteDate = null;
        // 之前多少天前的数据由于是在一个表中，直接drop表
        for (int i = remain + 1; i < remain + CLEAN_DAYS; i++)
        {
            durTimes = ONE_DAY * i;
            toDeleteTime = curTime - durTimes;
            toDeleteDate = new Date(toDeleteTime);
            userLogDAO.dropTable(toDeleteDate);
        }
        logger.info("Clean event log complement.");
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return EventType.values();
    }
    
    @PostConstruct
    public void init()
    {
        // 为防止定时任务未创建日志表，服务启动时立即创建
        try
        {
            createUserLogTablesTask.createTables(INIT_CREATE_DAY_TABLES);
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
        long total = 0;
        List<UserLog> userLogList = new ArrayList<UserLog>(10);
        long tmpOffset = req.getOffset();
        int tmpLimit = req.getLimit();
        
        long tableMatchSize = 0;
        int tableSize = tableList.size();
        for (int i = 0; i < tableSize; i++)
        {
            try
            {
                tableMatchSize = userLogDAO.getTotals(req, tableList.get(i));
            }
            catch (DataAccessException e)
            {
                if (e.getMessage().indexOf("doesn't exist") != -1)
                {
                    logger.warn("doesn't exist:" + e.getMessage());
                    break;
                }
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
                List<UserLog> tempList = userLogDAO.getUserLogList(req,
                    tableList.get(i),
                    tmpOffset,
                    tmpLimit);
                userLogList.addAll(tempList);
                tmpLimit = tmpLimit - tempList.size();
                tmpOffset = 0;
            }
            catch (DataAccessException e)
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
}
