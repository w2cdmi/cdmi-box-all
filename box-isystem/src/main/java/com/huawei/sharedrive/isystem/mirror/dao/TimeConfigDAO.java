package com.huawei.sharedrive.isystem.mirror.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;

public interface TimeConfigDAO
{
    List<TimeConfig> lstTimeConfig();

    void delete(String uuid);

    void create(TimeConfig timeconfig);

    TimeConfig get(String uuid);
    
    int countAll();
}
