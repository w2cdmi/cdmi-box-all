package com.huawei.sharedrive.isystem.plugin.dao.impl;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.isystem.plugin.dao.SecurityScanTaskDAO;
import com.huawei.sharedrive.isystem.plugin.domain.SecurityScanTask;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@SuppressWarnings("deprecation")
@Repository
public class SecurityScanTaskDAOImpl extends AbstractDAOImpl implements SecurityScanTaskDAO
{
    
    @Override
    public int getTotalTasks(byte status)
    {
        SecurityScanTask filter = new SecurityScanTask();
        filter.setStatus(status);
        return (int) sqlMapClientTemplate.queryForObject("SecurityScanTask.getTotalTasks", filter);
    }
    
}
