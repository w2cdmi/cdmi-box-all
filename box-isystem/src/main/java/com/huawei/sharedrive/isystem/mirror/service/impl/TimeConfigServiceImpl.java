package com.huawei.sharedrive.isystem.mirror.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



import com.huawei.sharedrive.isystem.mirror.dao.TimeConfigDAO;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
import com.huawei.sharedrive.isystem.mirror.service.TimeConfigService;



@Service("timeConfigService")
public class TimeConfigServiceImpl implements TimeConfigService
{
    @Autowired
    private TimeConfigDAO timeConfigDAO;


    @Override
    public List<TimeConfig> listTimeConfig()
    {
        List<TimeConfig> lstTimeConfig = timeConfigDAO.lstTimeConfig();
        
        if (null == lstTimeConfig || lstTimeConfig.isEmpty())
        {
            return lstTimeConfig;
        }
        return lstTimeConfig;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createTimeConfig(TimeConfig timeconfig)
    {
        timeConfigDAO.create(timeconfig);
    }

    @Override
    public void deleteTimeConfig(TimeConfig timeconfig)
    {
        timeConfigDAO.delete(timeconfig.getUuid());
    }
    @Override
    public TimeConfig getTimeConfig(String uuid)
    {
        TimeConfig timeconfig = timeConfigDAO.get(uuid);
        return timeconfig;
    }
    @Override
    public int countAll()
    {
        int count=timeConfigDAO.countAll();
        return count;
    }
    
    
}
