/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.logfile.dao.LogAgentDao;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgent;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

/**
 * 
 * @author s90006125
 *
 */
@SuppressWarnings({"unchecked", "deprecation"})
@Service("logAgentDao")
public class LogAgentDaoImpl extends AbstractDAOImpl implements LogAgentDao
{
    
    @Override
    public LogAgent select(int id)
    {
        return (LogAgent) sqlMapClientTemplate.queryForObject("LogAgent.select", id);
    }
    
    @Override
    public List<LogAgent> selectAll()
    {
        return sqlMapClientTemplate.queryForList("LogAgent.selectAll");
    }

    @Override
    public LogAgent selectByClusterId(int clusterId)
    {
        return (LogAgent)sqlMapClientTemplate.queryForObject("LogAgent.selectByClusterId", clusterId);
    }
    
}
