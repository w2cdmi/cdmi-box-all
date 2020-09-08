package com.huawei.sharedrive.uam.log.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.log.dao.SystemLogDao;
import com.huawei.sharedrive.uam.log.domain.QueryCondition;
import com.huawei.sharedrive.uam.log.service.SystemLogService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.log.SystemLog;

@Component
public class SystemLogServiceImpl implements SystemLogService
{
    
    @Autowired
    private SystemLogDao systemLogDao;
    
    @Override
    public String create(SystemLog systemLog)
    {
        
        systemLog.setId(UUID.randomUUID().toString());
        systemLog.setCreatedAt(new Date());
        systemLogDao.create(systemLog);
        return systemLog.getId();
    }
    
    @Override
    public Page<SystemLog> getFilterd(QueryCondition filter)
    {
        int total = systemLogDao.getFilterdCount(filter);
        List<SystemLog> content = systemLogDao.getFilterd(filter);
        Page<SystemLog> page = new PageImpl<SystemLog>(content, filter.getPageRequest(), total);
        return page;
    }
    
    @Override
    public Page<SystemLog> getByLoginName(SystemLog systemLog, PageRequest pageRequest)
    {
        int total = systemLogDao.getCountByLoginName(systemLog);
        List<SystemLog> content = systemLogDao.getByLoginName(systemLog, pageRequest.getLimit());
        Page<SystemLog> page = new PageImpl<SystemLog>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public void updateSuccess(String id)
    {
        systemLogDao.updateSuccess(id);
        
    }
    
    @Override
    public void deleteById(String id)
    {
        systemLogDao.deleteById(id);
        
    }
    
}
