package com.huawei.sharedrive.app.mirror.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.dao.TimeConfigDao;
import com.huawei.sharedrive.app.mirror.domain.TimeConfig;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class TimeConfigDaoImpl extends AbstractDAOImpl implements TimeConfigDao
{
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<TimeConfig> getAllTimeConfig()
    {
        return sqlMapClientTemplate.queryForList("TimeConfig.getAll");
    }
    
}
