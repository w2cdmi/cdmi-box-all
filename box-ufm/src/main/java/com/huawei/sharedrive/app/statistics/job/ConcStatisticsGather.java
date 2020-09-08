package com.huawei.sharedrive.app.statistics.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.statistics.dao.SysConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.dao.TempConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;

/**
 * 扫描单数据库的node和object表，执行统计任务
 * 
 * @author l90003768
 * 
 */
public class ConcStatisticsGather
{
    private static Logger logger = LoggerFactory.getLogger(ConcStatisticsGather.class);
    
    private TempConcStatisticsDAO tempConcStatisticsDAO;
    
    private SysConcStatisticsDAO sysConcStatisticsDAO;
    
    
    public ConcStatisticsGather(TempConcStatisticsDAO tempConcStatisticsDAO, SysConcStatisticsDAO sysConcStatisticsDAO)
    {
        this.tempConcStatisticsDAO = tempConcStatisticsDAO;
        this.sysConcStatisticsDAO = sysConcStatisticsDAO;
    }


    /**
     * 统计临时对象统计表的数据，汇总到对象统计表中
     */
    public void gatherTempObjectData(int day)
    {
        int maxUpload = tempConcStatisticsDAO.getMaxUpload(day);
        int maxDownload = tempConcStatisticsDAO.getMaxDownload(day);
        SysConcStatisticsDay sysStatistics = new SysConcStatisticsDay();
        sysStatistics.setDay(day);
        sysStatistics.setMaxUpload(maxUpload);
        sysStatistics.setMaxDownload(maxDownload);
        SysConcStatisticsDay dbStatistics = this.sysConcStatisticsDAO.get(day);
        insertOrUpdate(dbStatistics, sysStatistics);
        logger.info("[statisticsLog] end to gather tempData into objectStatistics");
    }


    private void insertOrUpdate(SysConcStatisticsDay dbStatistics, SysConcStatisticsDay sysStatistics)
    {
        if(null == dbStatistics)
        {
            this.sysConcStatisticsDAO.insert(sysStatistics);
            return;
        }
        boolean changed = false;
        if(sysStatistics.getMaxUpload() > dbStatistics.getMaxUpload())
        {
            dbStatistics.setMaxUpload(sysStatistics.getMaxUpload());
            changed = true;
        }
        if(sysStatistics.getMaxDownload() > dbStatistics.getMaxDownload())
        {
            dbStatistics.setMaxDownload(sysStatistics.getMaxDownload());
            changed = true;
        }
        if(changed)
        {
            this.sysConcStatisticsDAO.update(dbStatistics);
        }
    }

    
}
