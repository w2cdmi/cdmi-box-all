package com.huawei.sharedrive.app.mirror.appdatamigration.dao;

import java.util.List;

import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;

public interface MigrationProcessInfoDAO
{
    /**
     * 创建任务
     * @param processInfo
     */
    void create(MigrationProcessInfo processInfo);
    
    /**
     * 更新进度
     * @param processInfo
     * @param filesSize
     * @param filesNumber
     */
    int update(MigrationProcessInfo processInfo,long filesSize,long filesNumber);
    
    /**
     * 获取最新的一个进度
     * @param policyId
     * @return
     */
    MigrationProcessInfo getLast(int policyId);
    
    /**
     * 获取某一迁移策略的所有迁移进度
     * @param policyId
     * @return
     */
    List<MigrationProcessInfo> lstProcessInfo(int policyId);
    
    /**
     * 删除进度信息
     * @param processInfo
     * @return
     */
    int delete(MigrationProcessInfo processInfo);
    
    /**
     * 得到最近一次未完成的扫描过程
     * @return
     */
    MigrationProcessInfo getLastUnDoneMigrationProcess(MigrationProcessInfo processInfo);
    
    /**
     * 完成一个文件的迁移后（成功或失败），更新数量
     */
    void updateMigrationProcessForCompleteFile(MigrationProcessInfo migrationProcessInfo);
    
    /**
     * 获取某一迁移策略的所有迁移进度
     * @param policyId
     * @return
     */
    List<MigrationProcessInfo> lstAllMigrationProcessInfoByPolicyId(int policyId);
    
    /**
     * 关闭一次扫描记录
     * @param migrationProcessInfo
     */
    void endMigrationProcess(MigrationProcessInfo migrationProcessInfo);
}
