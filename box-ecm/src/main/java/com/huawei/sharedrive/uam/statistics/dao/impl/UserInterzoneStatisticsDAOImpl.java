package com.huawei.sharedrive.uam.statistics.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.statistics.dao.UserInterzoneStatisticsDAO;
import com.huawei.sharedrive.uam.statistics.domain.UserInterzone;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("userInterzoneStatisticsDAO")
@SuppressWarnings({"deprecation"})
public class UserInterzoneStatisticsDAOImpl extends AbstractDAOImpl implements UserInterzoneStatisticsDAO
{
    
    @Override
    public void save(UserInterzone interzone)
    {
        sqlMapClientTemplate.insert("UserInterzone.insert", interzone);
    }
    
    @Override
    public void delete()
    {
        sqlMapClientTemplate.delete("UserInterzone.delete");
    }
    
    @Override
    public UserInterzone query()
    {
        return (UserInterzone) sqlMapClientTemplate.queryForObject("UserInterzone.get");
    }
    
}
