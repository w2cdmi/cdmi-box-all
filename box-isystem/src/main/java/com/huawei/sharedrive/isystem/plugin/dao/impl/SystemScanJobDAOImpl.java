package com.huawei.sharedrive.isystem.plugin.dao.impl;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.isystem.plugin.dao.SystemScanJobDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@SuppressWarnings("deprecation")
@Repository
public class SystemScanJobDAOImpl extends AbstractDAOImpl implements SystemScanJobDAO
{
    
    public void updateScanJob(String jobCron)
    {
        sqlMapClientTemplate.update("SystemScanJob.updateScanJob", jobCron);
    }
    
}
