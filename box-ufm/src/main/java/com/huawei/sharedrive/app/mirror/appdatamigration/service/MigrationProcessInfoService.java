package com.huawei.sharedrive.app.mirror.appdatamigration.service;

import java.util.List;

import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;

public interface MigrationProcessInfoService
{
    // 添加一条记录，开始新周期的扫描
    void createNewMigrationProcess(MigrationProcessInfo migrationProcessInfo);
    
    MigrationProcessInfo getLastUnDoneMigrationProcess(int id);
    
    List<MigrationProcessInfo> lstAllMigrationProcessInfoByPolicyId(int policyId);
    
    void updateMigrationProcessForCompleteFile(MigrationProcessInfo migrationProcessInfo);
    
    void endMigrationProcess(MigrationProcessInfo migrationProcessInfo);
}
