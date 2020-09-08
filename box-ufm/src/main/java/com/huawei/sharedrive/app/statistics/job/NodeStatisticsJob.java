package com.huawei.sharedrive.app.statistics.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.ObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempUserNodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.impl.AsyncNodeStatisticsResult;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

/**
 * 节点定时统计任务，按天统计每日Node和对象数量和增量
 * 
 * @author l90003768
 * 
 */
@Component("nodeStatisticsJob")
public class NodeStatisticsJob extends QuartzJobTask
{
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    @Autowired
    private NodeStatisticsDAO  nodeStatisticsDAO;
    
    @Autowired
    private ObjectStatisticsDAO objectStatisticsDAO;
    
    @Autowired
    private TempObjectStatisticsDAO tempObjectStatisticsDAO;
    
    
    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeStatisticsJob.class);
    
    @Autowired
    private TempUserNodeStatisticsDAO tempUserNodeStatisticsDAO;
    
    
    
    private static final int TIME_STATISTICS_WAIT = 240;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        LOGGER.info("[statisticsLog] begin to statistics the node" + context.getJobDefinition());
        int day = StatisticsDateUtils.getDay();
        List<String> userDbList = new ArrayList<String>(10);
        List<String> tableList = slaveDatabaseManager.getDatabaseList();
        for(String dbName: tableList)
        {
            if(dbName.startsWith("userdb"))
            {
                userDbList.add(dbName);
            }
        }
        if(userDbList.isEmpty())
        {
            LOGGER.warn("[statisticsLog] userdb list is empty");
            return;
        }
        ExecutorService statisticsPool = new ThreadPoolExecutor(userDbList.size(), userDbList.size(), 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(userDbList.size()));
        List<Future<AsyncNodeStatisticsResult>> futureList = new ArrayList<Future<AsyncNodeStatisticsResult>>(10);
        StatisticsMapper statisticsMapper = new StatisticsMapper(slaveDatabaseManager);
        try
        {
            statisticsMapper.initUserAndAppMap();
        }
        catch (Exception e1)
        {
            LOGGER.error("", e1);
            record.setSuccess(false);
            return;
        }
        nodeStatistics(record,
            day,
            userDbList,
            statisticsPool,
            futureList,
            statisticsMapper);
        gatherStatistics(day);
        LOGGER.info("[statisticsLog]end the job for statistics nodes and objects.");
        LOGGER.info("end");
    }

    /**
     * @param record
     * @param day
     * @param allSeccess
     */
    private void gatherStatistics(int day)
    {
        NodeStatisticsGather nodeStatisticsGather = new NodeStatisticsGather(tempUserNodeStatisticsDAO, nodeStatisticsDAO);
        nodeStatisticsGather.statisticsTempUserNodes(day);
        ObjectStatisticsGather objectStatisticsGather = new ObjectStatisticsGather(tempObjectStatisticsDAO, nodeStatisticsDAO, objectStatisticsDAO);
        objectStatisticsGather.gatherTempObjectData(day);
    }

    /**
     * @param record
     * @param day
     * @param userDbList
     * @param statisticsPool
     * @param futureList
     * @param statisticsMapper
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void nodeStatistics(JobExecuteRecord record, int day, List<String> userDbList,
        ExecutorService statisticsPool, List<Future<AsyncNodeStatisticsResult>> futureList, StatisticsMapper statisticsMapper)
    {
        boolean allSeccess = true;
        NodeStatisticsThread thread = null;
        Future<AsyncNodeStatisticsResult> future = null;
        for(String userDb: userDbList)
        {
            thread = new NodeStatisticsThread(userDb, slaveDatabaseManager, statisticsMapper, tempUserNodeStatisticsDAO,
                nodeStatisticsDAO, tempObjectStatisticsDAO, day);
            future = statisticsPool.submit(thread);
            futureList.add(future);
        }
        for(Future<AsyncNodeStatisticsResult> tempFuture: futureList)
        {
            try
            {
                AsyncNodeStatisticsResult res = tempFuture.get(TIME_STATISTICS_WAIT, TimeUnit.MINUTES);
                if(res.getException() != null)
                {
                    LOGGER.error("[statisticsLog]Fail to statistics db " + res.getDbName(), res.getException());
                    allSeccess = false;
                }
            }
            catch (Exception e)
            {
                LOGGER.error("[statisticsLog]TimeoutException", e);
                record.setSuccess(false);
            }
        }
        if(!allSeccess)
        {
            record.setSuccess(false);
        }
    }
}
