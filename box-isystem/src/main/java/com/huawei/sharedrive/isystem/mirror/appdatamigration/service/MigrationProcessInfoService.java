package com.huawei.sharedrive.isystem.mirror.appdatamigration.service;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;

public interface MigrationProcessInfoService
{
    // 添加一条记录，开始新周期的扫描
    void createNewMigrationProcess(MigrationProcessInfo migrationProcessInfo);
    
    MigrationProcessInfo getLastUnDoneMigrationProcess(int id);
    
    List<MigrationProcessInfo> lstAllMigrationProcessInfoByPolicyId(int policyId);
    
    void updateMigrationProcessForCompleteFile(MigrationProcessInfo migrationProcessInfo);
    
    void endMigrationProcess(MigrationProcessInfo migrationProcessInfo);
    
    /**
     * 根据policyid列举相关的迁移记录
     * @param policyId
     * @return
     */
    List<MigrationProcessInfo> getMigrationProcessInfoByPolicyId(int policyId);

    MigrationProcessInfo getMigrationProcessInfo(String id);
}
