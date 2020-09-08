/**
 * 
 */
package com.huawei.sharedrive.app.log.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.log.dao.UserLogDAO;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListReq;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.DateUtils;

/**
 * @author s00108907
 * 
 */
@SuppressWarnings("deprecation")
@Service
public class UserLogDAOImpl implements UserLogDAO
{
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    private static final int MAX_KEYWORD_LENGTH = 255;
    
    @Override
    public void create(UserLog userLog)
    {
        
        // 防止keyword超过255
        if (userLog != null && userLog.getKeyword() != null
            && userLog.getKeyword().length() > MAX_KEYWORD_LENGTH)
        {
            userLog.setKeyword(userLog.getKeyword().substring(0, MAX_KEYWORD_LENGTH - 1));
        }
        if (null == userLog || null == userLog.getCreatedAt())
        {
            return;
        }
        int tableSuffix = Integer.parseInt(DateUtils.dateToString(userLog.getCreatedAt(),
            EVENT_LOG_DATE_PATTERN));
        userLog.setTableSuffix(tableSuffix);
        sqlMapClientTemplate.insert("UserLog.insert", userLog);
    }
    
    @Override
    public void createTable(Date ltDate)
    {
        int tableSuffix = Integer.parseInt(DateUtils.dateToString(ltDate, EVENT_LOG_DATE_PATTERN));
        UserLog userLog = new UserLog();
        userLog.setTableSuffix(tableSuffix);
        sqlMapClientTemplate.update("UserLog.createTable", userLog);
    }
    
    @Override
    public void dropTable(Date ltDate)
    {
        int tableSuffix = Integer.parseInt(DateUtils.dateToString(ltDate, EVENT_LOG_DATE_PATTERN));
        UserLog userLog = new UserLog();
        userLog.setTableSuffix(tableSuffix);
        sqlMapClientTemplate.update("UserLog.dropTable", userLog);
    }
    
    @Override
    public long getTotals(UserLogListReq req, String tableName)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("tableName", tableName);
        map.put("filter", req);
        return (long) sqlMapClientTemplate.queryForObject("UserLog.getTotalForOneLog", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UserLog> getUserLogList(UserLogListReq req, String tableName, long offset, int limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("tableName", tableName);
        map.put("filter", req);
        map.put("limit", limit);
        map.put("offset", offset);
        return (List<UserLog>) sqlMapClientTemplate.queryForList("UserLog.getList", map);
    }
    
}
