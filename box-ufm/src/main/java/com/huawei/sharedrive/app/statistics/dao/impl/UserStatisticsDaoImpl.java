package com.huawei.sharedrive.app.statistics.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.statistics.dao.UserStatisticsDao;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;
import com.huawei.sharedrive.app.statistics.manager.impl.StatisticsManagerImpl;
import com.huawei.sharedrive.app.user.dao.impl.UserDAOImplV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

@SuppressWarnings("deprecation")
@Service("userStatisticsDao")
public class UserStatisticsDaoImpl extends AbstractDAOImpl implements UserStatisticsDao
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsManagerImpl.class);
    
    private static final String DB_ID_SYSDB = "sysdb";
    
    private static final String BLANK = " ";
    
    private static final String SPLIT = ",";
    
    private static final String SELECT_CLUSTER = "select count(*) as userCount from [tableName] ";
    
    private static final String WHILE_CLUSTER_END = " where spaceUsed >= [begin] and spaceUsed < [end] and type=[type] ";
    
    private static final String WHILE_CLUSTER_NO_END = " where spaceUsed >= [begin] and type=[type] ";
    
    private static final int TABLE_USER_COUNT = 100;
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseService;
    
    @Override
    public void addHistoryDay(UserStatisticsDay historyDay)
    {
        sqlMapClientTemplate.insert("UserStatisticsDay.insert", historyDay);
    }
    
    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public List<UserCurrentStatisticsInfo> getUserCurrentStatistics(String groupBy, Integer regionId,
        String appId)
    {
        Connection conn = null;
        ArrayList<UserCurrentStatisticsInfo> list = new ArrayList<UserCurrentStatisticsInfo>(
            BusinessConstants.INITIAL_CAPACITIES);
        try
        {
            conn = slaveDatabaseService.getConnection(DB_ID_SYSDB);
            StringBuilder sqlPreffix = new StringBuilder("select");
            StringBuilder sqlSuffix = new StringBuilder("group by");
            if (TYPE_GROUPBY_REGION.equals(groupBy))
            {
                sqlPreffix.append(BLANK).append("regionId").append(SPLIT);
                sqlPreffix.append("count(id) as userCount from");
                sqlSuffix.append(BLANK).append("regionId");
            }
            else if (TYPE_GROUPBY_APP.equals(groupBy))
            {
                sqlPreffix.append(BLANK).append("appId").append(SPLIT);
                sqlPreffix.append("count(id) as userCount from");
                sqlSuffix.append(BLANK).append("appId");
            }
            else if (TYPE_GROUPBY_ALL.equals(groupBy))
            {
                sqlPreffix.append(BLANK).append("appId").append(SPLIT);
                sqlPreffix.append("regionId").append(SPLIT);
                sqlPreffix.append("count(id) as userCount from");
                sqlSuffix.append(BLANK).append("appId,regionId ");
            }
            String whereString = null;
            StringBuilder sql = null;
            for (int i = 0; i < UserDAOImplV2.TABLE_COUNT; i++)
            {
                sql = new StringBuilder();
                sql.append(sqlPreffix);
                sql.append(BLANK);
                sql.append("account_user_");
                sql.append(i);
                whereString = getUserCurrentWhereSql(regionId, appId);
                if (StringUtils.isNotBlank(whereString))
                {
                    sql.append(BLANK).append(whereString);
                }
                sql.append(BLANK).append(sqlSuffix.toString());
                tryQuery(groupBy, regionId, appId, conn, list, sql.toString());
            }
        }
        catch (SQLException e)
        {
            throw new InternalServerErrorException("SQL Exception" + e.getErrorCode());
        }
        finally
        {
            closeConnection(conn);
        }
        return list;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void tryQuery(String groupBy, Integer regionId, String appId, Connection conn,
        List<UserCurrentStatisticsInfo> list, String sql) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = conn.prepareStatement(sql);
            setUserCurrentStmt(regionId, appId, stmt);
            rs = stmt.executeQuery();
            while (rs.next())
            {
                list.add(getDataFromResultSet(groupBy, rs));
            }
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }
    
    @Override
    public List<UserCurrentStatisticsInfo> getUserCurrentStatistics(
        RestUserCurrentStatisticsRequest restStatistiscRequest)
    {
        return getUserCurrentStatistics(restStatistiscRequest.getGroupBy(),
            restStatistiscRequest.getRegionId(),
            restStatistiscRequest.getAppId());
    }
    
    private String getUserCurrentWhereSql(Integer regionId, String appId) throws SQLException
    {
        StringBuffer whereBuffer = new StringBuffer();
        // 过滤掉团队空间
        whereBuffer.append(" where type= ? ");
        
        if (StringUtils.isNotBlank(appId))
        {
            whereBuffer.append(" and appId= ? ");
            if (regionId != null)
            {
                whereBuffer.append(" and regionId= ? ");
            }
        }
        else
        {
            if (regionId != null)
            {
                whereBuffer.append(" and regionId= ? ");
            }
        }
        return whereBuffer.toString();
    }
    
    private void setUserCurrentStmt(Integer regionId, String appId, PreparedStatement stmt)
        throws SQLException
    {
        stmt.setInt(1, User.USER_TYPE_USER);
        if (StringUtils.isNotBlank(appId))
        {
            stmt.setString(2, appId);
            
            if (regionId != null)
            {
                stmt.setInt(3, regionId);
            }
        }
        else
        {
            if (regionId != null)
            {
                stmt.setInt(2, regionId);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UserStatisticsDay> getHistoryDaysByRange(Integer beginDay, Integer endDay, Integer regionId,
        String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("UserStatisticsDay.listRange", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UserStatisticsDay> getFilterHistoryDays(Integer day, Integer regionId, String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        
        map.put("day", day);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("UserStatisticsDay.getFilterd", map);
    }
    
    private void closeResultSet(ResultSet rs)
    {
        if (null != rs)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement.");
            }
        }
    }
    
    private void closeStatement(PreparedStatement stmt)
    {
        if (null != stmt)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement.");
            }
        }
    }
    
    private void closeConnection(Connection conn)
    {
        if (null != conn)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e1)
            {
                LOGGER.error("Caught exception while closing connection: ");
            }
        }
    }
    
    private UserCurrentStatisticsInfo getDataFromResultSet(String groupBy, ResultSet rs) throws SQLException
    {
        UserCurrentStatisticsInfo info = new UserCurrentStatisticsInfo();
        
        info.setUserCount(rs.getLong("userCount"));
        
        if (TYPE_GROUPBY_REGION.equals(groupBy))
        {
            info.setRegionId(rs.getInt("regionId"));
        }
        else if (TYPE_GROUPBY_APP.equals(groupBy))
        {
            info.setAppId(rs.getString("appId"));
        }
        else if (TYPE_GROUPBY_ALL.equals(groupBy))
        {
            info.setRegionId(rs.getInt("regionId"));
            info.setAppId(rs.getString("appId"));
            
        }
        return info;
    }
    
    @Override
    public UserClusterStatisticsInfo getClusterStatisticsInfo(Long begin, Long end)
    {
        Connection conn = null;
        try
        {
            conn = slaveDatabaseService.getConnection(DB_ID_SYSDB);
            StringBuffer sqlBf = null;
            long userCountTotal = 0L;
            for (int i = 0; i < TABLE_USER_COUNT; i++)
            {
                sqlBf = new StringBuffer();
                sqlBf.append(SELECT_CLUSTER.replace("[tableName]", "user_" + i));
                sqlBf.append(BLANK);
                if (end != null)
                {
                    sqlBf.append(WHILE_CLUSTER_END.replace("[begin]", begin + "")
                        .replace("[end]", end + "")
                        .replace("[type]", User.USER_TYPE_USER + ""));
                }
                else
                {
                    sqlBf.append(WHILE_CLUSTER_NO_END.replace("[begin]", begin + "").replace("[type]",
                        User.USER_TYPE_USER + ""));
                }
                userCountTotal = tryQueryUserCount(conn, sqlBf.toString(), userCountTotal);
            }
            UserClusterStatisticsInfo info = new UserClusterStatisticsInfo();
            info.setUserCount(userCountTotal);
            info.setBegin(begin);
            info.setEnd(end);
            return info;
        }
        catch (RuntimeException e)
        {
            throw new InternalServerErrorException(e);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
        finally
        {
            closeConnection(conn);
        }
    }
    
    private long tryQueryUserCount(Connection conn, String sql, long userCountTotal)
    {
        PreparedStatement ps = null;
        ResultSet res = null;
        try
        {
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            while (res.next())
            {
                userCountTotal += res.getLong("userCount");
            }
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
        finally
        {
            closeResultSet(res);
            closeStatement(ps);
        }
        return userCountTotal;
    }
    
}
