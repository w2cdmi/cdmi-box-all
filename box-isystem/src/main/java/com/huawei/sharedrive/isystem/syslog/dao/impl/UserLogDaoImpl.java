package com.huawei.sharedrive.isystem.syslog.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.adminlog.domain.QueryCondition;
import com.huawei.sharedrive.isystem.syslog.dao.UserLogDao;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.log.UserLog;

@SuppressWarnings("deprecation")
@Service
public class UserLogDaoImpl extends AbstractDAOImpl implements UserLogDao 
{
    @Resource(name="logSqlMapClientTemplate")
    private SqlMapClientTemplate logSqlMapClientTemplate;
    
    @Resource(name="logdb")
    private BasicDataSource logdb;
    
    public SqlMapClientTemplate getLogSqlMapClientTemplate()
    {
        return logSqlMapClientTemplate;
    }
    
    
    @PostConstruct
    public void init()
    {
        logSqlMapClientTemplate.setDataSource(logdb);
    }

    @Override
    public String insert(UserLog userLog)
    {
        logSqlMapClientTemplate.insert("UserLog.insert", userLog);
        return userLog.getId();
    }
    
    @Override
    @SuppressWarnings({"unchecked"})
    public List<UserLog> getListUserLog(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", filter.getPageRequest().getOrder());
        map.put("limit", filter.getPageRequest().getLimit());
        return logSqlMapClientTemplate.queryForList("UserLog.getFilterd", map);
    }
    
    @Override
    public int getTotals(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) logSqlMapClientTemplate.queryForObject("UserLog.getFilterdCount", map);
    }
    
    @Override
    public void updateUserLog(UserLog userLog)
    {
        logSqlMapClientTemplate.update("UserLog.update", userLog); 
    }

}
