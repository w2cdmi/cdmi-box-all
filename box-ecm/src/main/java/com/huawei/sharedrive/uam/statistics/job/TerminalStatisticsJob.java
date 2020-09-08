package com.huawei.sharedrive.uam.statistics.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.statistics.dao.TerminalStatisticsDAO;
import com.huawei.sharedrive.uam.statistics.domain.TerminalStatisticsDay;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

@Component("terminalStatisticsJob")
public class TerminalStatisticsJob extends QuartzJobTask
{
    
    public static final String TABLE_UAM = "uamdb";
    
    /**
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalStatisticsJob.class);
    
    private static final String SQL_TERMINAL_STATIS = "select deviceType,deviceAgent,count(*) as total from [tableName] where lastAccessAt >= ? group by deviceType,deviceAgent";
    
    private static final int TERMINAL_TABLE_COUNT = 100;
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    @Autowired
    private TerminalStatisticsDAO terminalStatisticsDAO;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        LOGGER.info("[statisticsLog] begin to statistics the terminal" + context.getJobDefinition());
        statisticsTerminal(record);
        LOGGER.info("[statisticsLog] end to statistics the terminal");
    }
    
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
            LOGGER.warn("Can not close connection", e);
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
        catch (SQLException e)
        {
            LOGGER.warn("Can not close preparedStatement", e);
        }
    }
    
    private void closeResultSet(ResultSet res)
    {
        if (null != res)
        {
            try
            {
                res.close();
            }
            catch (SQLException e)
            {
                LOGGER.warn("Can not close ResultSet", e);
            }
        }
    }
    
    private void saveToDb(int day, Map<StatisticsTerminal, Integer> map)
    {
        TerminalStatisticsDay terminalStatistics = null;
        StatisticsTerminal tempStatis;
        Integer count;
        for (Map.Entry<StatisticsTerminal, Integer> entry : map.entrySet())
        {
            tempStatis = entry.getKey();
            terminalStatistics = new TerminalStatisticsDay();
            terminalStatistics.setDay(day);
            terminalStatistics.setClientVersion(tempStatis.getClientAgent());
            terminalStatistics.setDeviceType(tempStatis.getDeviceType());
            count = entry.getValue();
            if (null != count)
            {
                terminalStatistics.setUserCount(count);
                this.terminalStatisticsDAO.insert(terminalStatistics);
            }
        }
    }
    
    /**
     * @param record
     */
    private void statisticsTerminal(JobExecuteRecord record)
    {
        Connection conn = null;
        int startDay = StatisticsDateUtils.getDay();
        try
        {
            conn = slaveDatabaseManager.getConnection(TABLE_UAM);
            Map<StatisticsTerminal, Integer> map = new HashMap<StatisticsTerminal, Integer>(10);
            for (int i = 0; i < TERMINAL_TABLE_COUNT; i++)
            {
                statisticsOneTable(conn, startDay, map, i);
            }
            saveToDb(startDay, map);
        }
        catch (Exception e)
        {
            record.setSuccess(false);
            record.setOutput(e.getMessage());
            LOGGER.error("[statisticsLog] error when statistics the terminal", e);
        }
        finally
        {
            closeConnection(conn);
        }
    }
    
    private void statisticsOneTable(Connection conn, int startDay, Map<StatisticsTerminal, Integer> map, int i)
        throws SQLException
    {
        StatisticsTerminal mapKey = null;
        int value = 0;
        ResultSet res = null;
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement(SQL_TERMINAL_STATIS.replace("[tableName]", "user_terminal_" + i));
            ps.setLong(1, startDay);
            res = ps.executeQuery();
            while (res.next())
            {
                mapKey = new StatisticsTerminal();
                mapKey.setClientAgent(res.getString("deviceAgent"));
                mapKey.setDeviceType(res.getByte("deviceType"));
                value = res.getInt("total");
                if (map.containsKey(mapKey))
                {
                    map.put(mapKey, map.get(mapKey).intValue() + value);
                }
                else
                {
                    map.put(mapKey, value);
                }
            }
        }
        finally
        {
            closePreparedStatment(ps);
            closeResultSet(res);
        }
    }
    
}
