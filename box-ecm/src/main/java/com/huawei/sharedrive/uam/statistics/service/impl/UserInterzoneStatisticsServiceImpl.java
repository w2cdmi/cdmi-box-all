package com.huawei.sharedrive.uam.statistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.statistics.dao.UserInterzoneStatisticsDAO;
import com.huawei.sharedrive.uam.statistics.domain.UserInterzone;
import com.huawei.sharedrive.uam.statistics.service.UserInterzoneStatisticsService;

import pw.cdmi.core.utils.UUIDUtils;

@Component
public class UserInterzoneStatisticsServiceImpl implements UserInterzoneStatisticsService
{
    @Autowired
    private UserInterzoneStatisticsDAO userInterzoneDAO;
    
    @Override
    public void save(UserInterzone interzone)
    {
        interzone.setId(UUIDUtils.getValueAfterMD5());
        userInterzoneDAO.save(interzone);
    }
    
    @Override
    public void delete()
    {
        userInterzoneDAO.delete();
    }
    
    @Override
    public UserInterzone query()
    {
        return userInterzoneDAO.query();
    }
    
}
