package com.huawei.sharedrive.uam.log.service;

import com.huawei.sharedrive.uam.log.domain.QueryCondition;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.log.SystemLog;

public interface SystemLogService
{
    
    String create(SystemLog systemLog);
    
    Page<SystemLog> getFilterd(QueryCondition filter);
    
    Page<SystemLog> getByLoginName(SystemLog systemLog, PageRequest pageRequest);
    
    void updateSuccess(String id);
    
    void deleteById(String id);
    
}
