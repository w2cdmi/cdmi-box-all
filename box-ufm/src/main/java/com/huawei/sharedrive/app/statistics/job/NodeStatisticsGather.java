package com.huawei.sharedrive.app.statistics.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempUserNodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.TempUserNodeStatistics;

/**
 * 扫描单数据库的node和object表，执行统计任务
 * 
 * @author l90003768
 * 
 */
public class NodeStatisticsGather
{
    private static Logger logger = LoggerFactory.getLogger(NodeStatisticsGather.class);
    
    private NodeStatisticsDAO nodeStatisticsDAO;
    
    private TempUserNodeStatisticsDAO tempUserNodeStatisticsDAO;
    
    public NodeStatisticsGather(TempUserNodeStatisticsDAO tempUserNodeStatisticsDAO, NodeStatisticsDAO nodeStatisticsDAO)
    {
        this.tempUserNodeStatisticsDAO = tempUserNodeStatisticsDAO;
        this.nodeStatisticsDAO = nodeStatisticsDAO;
    }
    
    /**
     * 将汇总数据计算并放入今日汇总数据中
     * @param todayList
     * @param tempStatistics
     * @param yestodayList
     */
    private void addToTodayList(List<NodeStatisticsDay> todayList, TempUserNodeStatistics tempStatistics,
        List<NodeStatisticsDay> yestodayList)
    {
        NodeStatisticsDay yestoday = null;
        for(NodeStatisticsDay tempYestoday: yestodayList)
        {
            if(tempYestoday.getRegionId() == tempStatistics.getRegionId())
            {
                if(StringUtils.equalsIgnoreCase(tempYestoday.getAppId(), tempStatistics.getAppId()))
                {
                    yestoday = tempYestoday;
                }
            }
        }
        NodeStatisticsDay today = tempStatistics.convertIntoNodeStatisticsDay();
        if(null == yestoday)
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
     * 统计临时表的数据，汇总到统计表中
     */
    public void statisticsTempUserNodes(int day)
    {
        logger.info("[statisticsLog] start to gather tempData into statisticsNode");
        List<TempUserNodeStatistics> tempGatheredList = this.tempUserNodeStatisticsDAO.getGatherList(day);
        List<NodeStatisticsDay> yestodayList = this.nodeStatisticsDAO.getList(day -1);
        List<NodeStatisticsDay> todayList = new ArrayList<NodeStatisticsDay>(10);
        for(TempUserNodeStatistics tempStatistics: tempGatheredList)
        {
            addToTodayList(todayList, tempStatistics, yestodayList);
        }
        for(NodeStatisticsDay today: todayList)
        {
            this.nodeStatisticsDAO.insert(today);
        }
        logger.info("[statisticsLog] end to gather tempData into statisticsNode for " + todayList.size());
        tempUserNodeStatisticsDAO.deleteAll();
    }
    
}
