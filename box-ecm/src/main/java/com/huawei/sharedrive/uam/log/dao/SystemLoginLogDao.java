package com.huawei.sharedrive.uam.log.dao;

import java.util.List;

import com.huawei.sharedrive.uam.log.domain.QueryCondition;

import pw.cdmi.common.log.SystemLog;

public interface SystemLoginLogDao
{
    void create(SystemLog systemLog);
    
    int getFilterdCount(QueryCondition filter);
    
    List<SystemLog> getFilterd(QueryCondition filter);
    
    void deleteById(String id);
    
}
