package com.huawei.sharedrive.uam.log.service.impl;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.log.dao.SystemLoginLogDao;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.service.SystemLoginLogService;

import pw.cdmi.common.log.SystemLog;

@Component
public class SystemLoginLogServiceImpl implements SystemLoginLogService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemLoginLogServiceImpl.class);
    
    @Autowired
    private SystemLoginLogDao systemLoginLogDao;
    
    @Override
    public void create(SystemLog systemLoginLog, OperateType operateType, String[] paramsType)
    {
        try
        {
            systemLoginLog.setOperateType(operateType.getDetails(paramsType));
            systemLoginLog.setId(UUID.randomUUID().toString());
            systemLoginLog.setCreatedAt(new Date());
            systemLoginLogDao.create(systemLoginLog);
        }
        catch (Exception e)
        {
            LOGGER.debug("save systemLoginLog fail", e);
        }
    }
}
