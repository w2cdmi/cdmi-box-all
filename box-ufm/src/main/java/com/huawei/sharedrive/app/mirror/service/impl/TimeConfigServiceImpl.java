package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.dao.TimeConfigDao;
import com.huawei.sharedrive.app.mirror.domain.TimeConfig;
import com.huawei.sharedrive.app.mirror.service.TimeConfigService;

@Service("timeConfigService")
public class TimeConfigServiceImpl implements TimeConfigService
{
    @Autowired
    private TimeConfigDao timeConfigDao;
    
    @Override
    public List<TimeConfig> getAllTimeConfig()
    {
        return timeConfigDao.getAllTimeConfig();
    }
    
}
