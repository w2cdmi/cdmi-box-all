package com.huawei.sharedrive.isystem.mirror.dao.impl;



import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.dao.TimeConfigDAO;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;


@Component
public class TimeConfigDAOImpl extends AbstractDAOImpl implements TimeConfigDAO
{
    @SuppressWarnings("deprecation")
    @Override
    public TimeConfig get(String uuid)
    {
        TimeConfig timeconfig = (TimeConfig) sqlMapClientTemplate.queryForObject("TimeConfig.get", uuid);
        return timeconfig;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void create(TimeConfig timeconfig)
    {
        sqlMapClientTemplate.insert("TimeConfig.insert", timeconfig);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void delete(String uuid)
    {
        sqlMapClientTemplate.delete("TimeConfig.delete", uuid);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<TimeConfig> lstTimeConfig()
    {
        List<TimeConfig> lstTimeConfig = sqlMapClientTemplate.queryForList("TimeConfig.getAll");
        if (null == lstTimeConfig || lstTimeConfig.isEmpty())
        {
            return null;
        }   
        return lstTimeConfig;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int countAll()
    {
        return (int) sqlMapClientTemplate.queryForObject("TimeConfig.countAll");
    }
  
}
