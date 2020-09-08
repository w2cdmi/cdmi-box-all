package com.huawei.sharedrive.app.mirror.appdatamigration.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.appdatamigration.dao.MigrationProcessInfoDAO;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationProcessInfoService;

@Service("migrationProcessInfoService")
public class MigrationProcessInfoServiceImpl implements MigrationProcessInfoService
{
    @Autowired
    private MigrationProcessInfoDAO migrationProcessInfoDAO;
    
    @Override
    public void createNewMigrationProcess(MigrationProcessInfo migrationProcessInfo)
    {
        migrationProcessInfoDAO.create(migrationProcessInfo);
    }

    /**
     * 获取最近一次未完成的扫描
     */
    @Override
    public MigrationProcessInfo getLastUnDoneMigrationProcess(int id)
    {
        MigrationProcessInfo migrationProcessInfo = new MigrationProcessInfo();
        migrationProcessInfo.setPolicyId(id);
        migrationProcessInfo.setStatus(MigrationProcessInfo.STATUS_RUNNING);
        return migrationProcessInfoDAO.getLastUnDoneMigrationProcess(migrationProcessInfo);
    }

    @Override
    public List<MigrationProcessInfo> lstAllMigrationProcessInfoByPolicyId(int policyId)
    {
        return null;
    }

    @Override
    public void updateMigrationProcessForCompleteFile(MigrationProcessInfo migrationProcessInfo)
    {
        migrationProcessInfo.setModifiedAt(new Date());
        migrationProcessInfoDAO.updateMigrationProcessForCompleteFile(migrationProcessInfo);
    }

    @Override
    public void endMigrationProcess(MigrationProcessInfo migrationProcessInfo)
    {
        migrationProcessInfo.setEndTime(new Date());
        migrationProcessInfo.setStatus(MigrationProcessInfo.STATUS_COMPLETED);
        migrationProcessInfo.setModifiedAt(new Date());
        migrationProcessInfoDAO.endMigrationProcess(migrationProcessInfo);
        
    }
    
    
}
