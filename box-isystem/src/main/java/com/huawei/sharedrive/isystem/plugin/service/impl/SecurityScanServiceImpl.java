package com.huawei.sharedrive.isystem.plugin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.plugin.dao.SecurityScanTaskDAO;
import com.huawei.sharedrive.isystem.plugin.dao.SystemScanJobDAO;
import com.huawei.sharedrive.isystem.plugin.service.SecurityScanService;

@Service
public class SecurityScanServiceImpl implements SecurityScanService
{
    
    @Autowired
    private SecurityScanTaskDAO securityScanTaskDAO;
    
    @Autowired
    private SystemScanJobDAO systemJobDAO;
    
    @Override
    public int getTotalTasks(byte status)
    {
        return securityScanTaskDAO.getTotalTasks(status);
    }
    
    @Override
    public String parseToCron(int exeStartAt, int exeEndAt)
    {
        int endHour = exeEndAt - 1;
        String cron;
        if (exeStartAt == endHour)
        {
            cron = "*/20 " + "*" + " " + exeStartAt + " * * ?";
        }
        else
        {
            cron = "*/20 " + "*" + " " + exeStartAt + "-" + endHour + " * * ?";
        }
        return cron;
    }
    
    @Override
    public void updateScanJob(String jobCron)
    {
        systemJobDAO.updateScanJob(jobCron);
    }
}
