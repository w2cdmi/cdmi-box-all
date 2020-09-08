package com.huawei.sharedrive.app.statistics.job;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.ObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.TempObjectStatisticsDay;

/**
 * 扫描单数据库的node和object表，执行统计任务
 * 
 * @author l90003768
 * 
 */
public class ObjectStatisticsGather
{
    private static Logger logger = LoggerFactory.getLogger(ObjectStatisticsGather.class);
    
    private NodeStatisticsDAO nodeStatisticsDAO;
    
    private ObjectStatisticsDAO objectStatisticsDAO;
    
    private TempObjectStatisticsDAO tempObjectStatisticsDAO;
    
    public ObjectStatisticsGather(TempObjectStatisticsDAO tempObjectStatisticsDAO, NodeStatisticsDAO nodeStatisticsDAO,
        ObjectStatisticsDAO objectStatisticsDAO)
    {
        this.tempObjectStatisticsDAO = tempObjectStatisticsDAO;
        this.nodeStatisticsDAO = nodeStatisticsDAO;
        this.objectStatisticsDAO = objectStatisticsDAO;
    }


    /**
     * 统计临时对象统计表的数据，汇总到对象统计表中
     */
    public void gatherTempObjectData(int day)
    {

        List<TempObjectStatisticsDay> sumList = tempObjectStatisticsDAO.getSumList(day);
        List<NodeStatisticsDay> nodeList = nodeStatisticsDAO.getFileAndSizeSummayListByRegion(day);
        List<ObjectStatisticsDay> objectStatisticsList = fillFileAndSize(sumList, nodeList);
        List<ObjectStatisticsDay> lastDayList = this.objectStatisticsDAO.getLastDayList(day);
        fillAddedData(objectStatisticsList, lastDayList);
        for(ObjectStatisticsDay objectStatistics: objectStatisticsList)
        {
            this.objectStatisticsDAO.insert(objectStatistics);
        }
        this.tempObjectStatisticsDAO.clearData();
        logger.info("[statisticsLog] end to gather tempData into objectStatistics");
    }

    private void fillAddedData(List<ObjectStatisticsDay> objectStatisticsList,
        List<ObjectStatisticsDay> lastDayList)
    {
        for(ObjectStatisticsDay objectStatistics: objectStatisticsList)
        {
            for(ObjectStatisticsDay lastObjectStatistics: lastDayList)
            {
                if(objectStatistics.getRegionId().equals(lastObjectStatistics.getRegionId()))
                {
                    objectStatistics.setAddedActualFileCount(objectStatistics.getActualFileCount() - lastObjectStatistics.getActualFileCount());
                    objectStatistics.setAddedActualSpaceUsed(objectStatistics.getActualSpaceUsed() - lastObjectStatistics.getActualSpaceUsed());
                }
            }
        }
    }

    private List<ObjectStatisticsDay> fillFileAndSize(List<TempObjectStatisticsDay> sumList, List<NodeStatisticsDay> nodeStatisticsList)
    {
        List<ObjectStatisticsDay> list = new ArrayList<ObjectStatisticsDay>(sumList.size());
        ObjectStatisticsDay objectStatistics = null;
        for(TempObjectStatisticsDay tempObjectStatistics: sumList)
        {
            objectStatistics = tempObjectStatistics.convertToObjectStatistics();
            for(NodeStatisticsDay nodeStatistics: nodeStatisticsList)
            {
                if(nodeStatistics.getRegionId() == tempObjectStatistics.getRegionId())
                {
                    objectStatistics.setFileCount(nodeStatistics.getFileCount());
                    objectStatistics.setSpaceUsed(nodeStatistics.getSpaceUsed());
                    objectStatistics.setAddedFileCount(nodeStatistics.getAddedFileCount());
                    objectStatistics.setAddedSpaceUsed(nodeStatistics.getAddedSpaceUsed());
                }
            }
            list.add(objectStatistics);
        }
        return list;
    }
    
}
