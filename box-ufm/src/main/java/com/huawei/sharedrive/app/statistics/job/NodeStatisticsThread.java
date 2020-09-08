package com.huawei.sharedrive.app.statistics.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempUserNodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.impl.AsyncNodeStatisticsResult;
import com.huawei.sharedrive.app.statistics.domain.NodeSelectByGroupBy;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.TempObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.TempUserNodeStatistics;

import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

/**
 * 扫描单数据库的node和object表，执行统计任务
 * 
 * @author l90003768
 * 
 */
public class NodeStatisticsThread implements Callable<AsyncNodeStatisticsResult>
{
    
    public static final int MB = 1024 * 1024;
    
    private static Logger logger = LoggerFactory.getLogger(NodeStatisticsThread.class);
    
    private static final String SQL_NODE_STATISTICS = "select ownedBy,`status`,resourceGroupId,count(*) as fileCount, sum(size) as spaceUsed from [tableName] where type=1 group by ownedBy,`status`,resourceGroupId order by ownedBy";
    
    private static final String SQL_OBJECT_STATISTICS = "select resourceGroupId,count(*) as actualFileCount, sum(size) as actualSpaceUsed from [tableName] group by resourceGroupId";
    
    private static final int TABLE_INODE_NUMBER = 500;
    
    private static final int TABLE_OBJECT_NUMBER = 500;
    
    private String dbName;
    
    private int day;
    
    private Exception exception;
    
    private NodeStatisticsDAO nodeStatisticsDAO;
    
    private TempObjectStatisticsDAO tempObjectStatisticsDAO;
    
    private SlaveDatabaseManager slaveDatabaseManager;
    
    private StatisticsMapper staticticsMapper;
    
    private TempUserNodeStatisticsDAO tempUserNodeStatisticsDAO;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public NodeStatisticsThread(String dbName, SlaveDatabaseManager slaveDatabaseManager,
        StatisticsMapper statisticsMapper, TempUserNodeStatisticsDAO tempUserNodeStatisticsDAO,
        NodeStatisticsDAO nodeStatisticsDAO, TempObjectStatisticsDAO tempObjectStatisticsDAO, int day)
    {
        this.dbName = dbName;
        this.staticticsMapper = statisticsMapper;
        this.slaveDatabaseManager = slaveDatabaseManager;
        this.tempUserNodeStatisticsDAO = tempUserNodeStatisticsDAO;
        this.nodeStatisticsDAO = nodeStatisticsDAO;
        this.tempObjectStatisticsDAO = tempObjectStatisticsDAO;
        this.day = day;
    }
    
    @Override
    public AsyncNodeStatisticsResult call()
    {
        logger.info("[statisticsLog] begin to do statistics for " + this.dbName);
        statisticsNodes();
        statisticsObjects();
        AsyncNodeStatisticsResult res = new AsyncNodeStatisticsResult();
        res.setException(this.getException());
        res.setDbName(this.dbName);
        return res;
    }
    
    public Exception getException()
    {
        return this.exception;
    }
    
    /**
     * 统计临时表的数据，汇总到统计表中
     */
    public void statisticsTempUserNodes(int day)
    {
        logger.info("[statisticsLog] start to gather tempData into statisticsNode");
        List<TempUserNodeStatistics> tempGatheredList = this.tempUserNodeStatisticsDAO.getGatherList(day);
        List<NodeStatisticsDay> yestodayList = this.nodeStatisticsDAO.getList(day - 1);
        List<NodeStatisticsDay> todayList = new ArrayList<NodeStatisticsDay>(10);
        for (TempUserNodeStatistics tempStatistics : tempGatheredList)
        {
            addToTodayList(todayList, tempStatistics, yestodayList);
        }
        for (NodeStatisticsDay today : todayList)
        {
            this.nodeStatisticsDAO.insert(today);
        }
        logger.info("[statisticsLog] end to gather tempData into statisticsNode for " + todayList.size());
    }
    
    /**
     * 将分组查询结果放入临时的用户节点列表中
     * 
     * @param list
     * @param nodeDb
     */
    private void addToList(List<TempUserNodeStatistics> list, NodeSelectByGroupBy nodeDb)
    {
        TempUserNodeStatistics userNode = null;
        for (TempUserNodeStatistics tempNode : list)
        {
            if (nodeDb.getOwnedBy() == tempNode.getOwnedBy())
            {
                if (nodeDb.getResourceGroupId() == tempNode.getResourceGroupId())
                {
                    userNode = tempNode;
                }
            }
        }
        if (null == userNode)
        {
            userNode = new TempUserNodeStatistics();
            userNode.setOwnedBy(nodeDb.getOwnedBy());
            userNode.setDbName(nodeDb.getDbName());
            userNode.setDay(this.day);
            userNode.setAppId(this.staticticsMapper.getAppId(nodeDb.getOwnedBy()));
            userNode.setResourceGroupId(nodeDb.getResourceGroupId());
            userNode.setRegionId(this.staticticsMapper.getRegionId(nodeDb.getResourceGroupId()));
            list.add(userNode);
        }
        switch (nodeDb.getStatus())
        {
            case INode.STATUS_TRASH:
            case INode.STATUS_TRASH_DELETE:
                userNode.setTrashSpaceUsed(userNode.getTrashSpaceUsed() + nodeDb.getSpaceUsed());
                userNode.setTrashFileCount(userNode.getTrashFileCount() + nodeDb.getFileCount());
                break;
            case INode.STATUS_DELETE:
                userNode.setDeletedSpaceUsed(nodeDb.getSpaceUsed());
                userNode.setDeletedFileCount(userNode.getDeletedFileCount());
                break;
            default:
                userNode.setSpaceUsed(userNode.getSpaceUsed() + nodeDb.getSpaceUsed());
                userNode.setFileCount(userNode.getFileCount() + nodeDb.getFileCount());
                break;
        }
    }
    
    /**
     * 将汇总数据计算并放入今日汇总数据中
     * 
     * @param todayList
     * @param tempStatistics
     * @param yestodayList
     */
    private void addToTodayList(List<NodeStatisticsDay> todayList, TempUserNodeStatistics tempStatistics,
        List<NodeStatisticsDay> yestodayList)
    {
        NodeStatisticsDay yestoday = null;
        for (NodeStatisticsDay tempYestoday : yestodayList)
        {
            if (tempYestoday.getRegionId() == tempStatistics.getRegionId())
            {
                if (StringUtils.equalsIgnoreCase(tempYestoday.getAppId(), tempStatistics.getAppId()))
                {
                    yestoday = tempYestoday;
                }
            }
        }
        NodeStatisticsDay today = tempStatistics.convertIntoNodeStatisticsDay();
        if (null == yestoday)
        {
            today.setAddedDeletedFileCount(today.getDeletedFileCount());
            today.setAddedDeletedSpaceUsed(today.getDeletedSpaceUsed());
            today.setAddedFileCount(today.getFileCount());
            today.setAddedSpaceUsed(today.getSpaceUsed());
            today.setAddedTrashFileCount(today.getTrashFileCount());
            today.setAddedTrashSpaceUsed(today.getTrashSpaceUsed());
        }
        else
        {
            today.setAddedDeletedFileCount(today.getDeletedFileCount() - yestoday.getDeletedFileCount());
            today.setAddedDeletedSpaceUsed(today.getDeletedSpaceUsed() - yestoday.getDeletedSpaceUsed());
            today.setAddedFileCount(today.getFileCount() - yestoday.getFileCount());
            today.setAddedSpaceUsed(today.getSpaceUsed() - yestoday.getSpaceUsed());
            today.setAddedTrashFileCount(today.getTrashFileCount() - yestoday.getTrashFileCount());
            today.setAddedTrashSpaceUsed(today.getTrashSpaceUsed() - yestoday.getTrashSpaceUsed());
        }
        todayList.add(today);
    }
    
    /**
     * @param conn
     */
    private void closeConnection(Connection conn)
    {
        if (null == conn)
        {
            return;
        }
        try
        {
            conn.close();
        }
        catch (Exception e)
        {
            logger.warn("Can not close connection", e);
        }
    }
    
    /**
     * @param ps
     */
    private void closePreparedStatment(PreparedStatement ps)
    {
        if (null == ps)
        {
            return;
        }
        try
        {
            ps.close();
        }
        catch (Exception e)
        {
            logger.warn("Can not close preparedStatement", e);
        }
    }
    
    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection getConnection(String dbName) throws ClassNotFoundException, SQLException
    {
        Connection conn = slaveDatabaseManager.getConnection(dbName);
        return conn;
    }
    
    /**
     * 根据区域汇总数据
     * 
     * @param objectStatisticsList
     * @return
     */
    private List<TempObjectStatisticsDay> mergeByRegion(List<TempObjectStatisticsDay> objectStatisticsList)
    {
        if (CollectionUtils.isEmpty(objectStatisticsList))
        {
            return objectStatisticsList;
        }
        Map<Integer, TempObjectStatisticsDay> regionMap = new HashMap<Integer, TempObjectStatisticsDay>(1);
        TempObjectStatisticsDay tempStatistics = null;
        for (TempObjectStatisticsDay dbObjStatistics : objectStatisticsList)
        {
            tempStatistics = regionMap.get(Integer.valueOf(dbObjStatistics.getRegionId()));
            if (null == tempStatistics)
            {
                regionMap.put(Integer.valueOf(dbObjStatistics.getRegionId()), dbObjStatistics);
            }
            else
            {
                tempStatistics.setActualFileCount(tempStatistics.getActualFileCount()
                    + dbObjStatistics.getActualFileCount());
                tempStatistics.setActualSpaceUsed(tempStatistics.getActualSpaceUsed()
                    + dbObjStatistics.getActualSpaceUsed());
            }
        }
        return new ArrayList<TempObjectStatisticsDay>(regionMap.values());
    }
    
    /**
     * 统计inode表的数据
     */
    private void statisticsNodes()
    {
        Connection conn = null;
        try
        {
            conn = getConnection(this.dbName);
            List<TempUserNodeStatistics> userNodeList = null;
            PreparedStatement ps = null;
            String sql = null;
            for (int i = 0; i < TABLE_INODE_NUMBER; i++)
            {
                userNodeList = new ArrayList<TempUserNodeStatistics>(100);
                sql = SQL_NODE_STATISTICS.replace("[tableName]", "inode_" + i);
                try
                {
                    ps = conn.prepareStatement(sql);
                    ResultSet res = ps.executeQuery();
                    NodeSelectByGroupBy nodeDb = null;
                    while (res.next())
                    {
                        nodeDb = new NodeSelectByGroupBy();
                        nodeDb.setFileCount(res.getLong("fileCount"));
                        nodeDb.setOwnedBy(res.getLong("ownedBy"));
                        nodeDb.setResourceGroupId(res.getInt("resourceGroupId"));
                        nodeDb.setSpaceUsed(res.getLong("spaceUsed"));
                        nodeDb.setDbName(this.dbName);
                        addToList(userNodeList, nodeDb);
                    }
                    tempUserNodeStatisticsDAO.saveList(userNodeList);
                }
                catch (Exception e)
                {
                    this.exception = e;
                    return;
                }
                finally
                {
                    closePreparedStatment(ps);
                }
            }
        }
        catch (RuntimeException e)
        {
            this.exception = e;
            return;
        }
        catch (Exception e)
        {
            this.exception = e;
            return;
        }
        finally
        {
            closeConnection(conn);
        }
    }
    
    /**
     * 统计object_reference表的数据
     */
    private void statisticsObjects()
    {
        Connection conn = null;
        try
        {
            conn = getConnection(this.dbName);
            PreparedStatement ps = null;
            String sql = null;
            TempObjectStatisticsDay objectDb = null;
            List<TempObjectStatisticsDay> tempObjectStatisticsList = new ArrayList<TempObjectStatisticsDay>(
                500);
            for (int i = 0; i < TABLE_OBJECT_NUMBER; i++)
            {
                sql = SQL_OBJECT_STATISTICS.replace("[tableName]", "object_reference_" + i);
                try
                {
                    ps = conn.prepareStatement(sql);
                    ResultSet res = ps.executeQuery();
                    while (res.next())
                    {
                        objectDb = new TempObjectStatisticsDay();
                        objectDb.setActualFileCount(res.getLong("actualFileCount"));
                        objectDb.setResourceGroupId(res.getInt("resourceGroupId"));
                        objectDb.setRegionId(this.staticticsMapper.getRegionId(objectDb.getResourceGroupId()));
                        objectDb.setActualSpaceUsed(res.getLong("actualSpaceUsed"));
                        objectDb.setDbName(this.dbName);
                        objectDb.setDay(this.day);
                        tempObjectStatisticsList.add(objectDb);
                    }
                }
                catch (Exception e)
                {
                    this.exception = e;
                    return;
                }
                finally
                {
                    closePreparedStatment(ps);
                }
            }
            List<TempObjectStatisticsDay> mergedStatisticsList = mergeByRegion(tempObjectStatisticsList);
            for (TempObjectStatisticsDay objectStatistics : mergedStatisticsList)
            {
                tempObjectStatisticsDAO.insert(objectStatistics);
            }
        }
        catch (RuntimeException e)
        {
            logger.warn("[statisticsLog]", e);
            this.exception = e;
            return;
        }
        catch (Exception e)
        {
            logger.warn("[statisticsLog]", e);
            this.exception = e;
            return;
        }
        finally
        {
            closeConnection(conn);
        }
    }
    
}
