package com.huawei.sharedrive.isystem.mirror.appdatamigration.service;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;

public interface MigrationEverydayProcessService
{
    void create(MigrationProcessInfo processInfo);
    
    /**
     * 获取一个迁移周期的未完成的一天的记录
     * @param id
     * @return
     */
    MigrationEverydayProcess getUnCompleteDayProcess(String id);
    
    void closeUnCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess);
    
    void updateCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess);
    
    void updateForCompleteAFile(MigrationProcessInfo processInfo,long size);
    
    /**
     * 列举一次迁移记录的每日情况
     * @param parent
     * @return
     */
    List<MigrationEverydayProcess> getMigrationEverydayProcessById(String parent);
}
