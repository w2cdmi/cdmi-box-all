package com.huawei.sharedrive.app.statistics.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.StatisticsAccessKeyDAO;
import com.huawei.sharedrive.app.statistics.domain.StatisticsAccessKey;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("statisticsAccessKeyDAO")
@SuppressWarnings({"deprecation"})
public class StatisticsAccessKeyDAOImpl extends AbstractDAOImpl implements StatisticsAccessKeyDAO
{
    @Override
    public StatisticsAccessKey get(String accessKey)
    {
        return (StatisticsAccessKey) sqlMapClientTemplate.queryForObject("StatisticsAccessKey.get", accessKey);
    }
    
}
