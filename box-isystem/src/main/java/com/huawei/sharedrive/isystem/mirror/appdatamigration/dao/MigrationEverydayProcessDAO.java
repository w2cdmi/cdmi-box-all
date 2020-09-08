package com.huawei.sharedrive.isystem.mirror.appdatamigration.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;



public interface MigrationEverydayProcessDAO
{
    /**
     * 创建
     * @param dayProcess
     */
    void create(MigrationEverydayProcess dayProcess);
    
    /**
     * 列举历史迁移记录
     * @param id
     * @param beginDay
     * @param endDay
     * @return
     */
    List<MigrationEverydayProcess> lstDayProcess(String migrationId,String beginDay,String endDay);
    
    /**
     * 理解某迁移ID的所有历史记录
     * @param migrationId
     * @return
     */
    List<MigrationEverydayProcess> lstAllDayProcess(String migrationId);
    
    /**
     * 删除该迁移ID的所有的历史记录
     * @param migrationId
     */
    int deleteByMigrationId(String migrationId); 
    
    /**
     * 得到不是一条完整记录【一天有开始和结束时间】的数据
     */
    MigrationEverydayProcess getUnCompleteDayProcess(String migrationId);
    
    /**
     * 完成一天的记录
     * @param migrationEverydayProcess
     */
    void closeUnCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess);
    
    /**
     * 完成一个文件复制后更新一天的复制记录
     * @param migrationEverydayProcess
     */
    void updateForCompleteAFile(MigrationEverydayProcess migrationEverydayProcess);
    
    /**
     * 列举一次迁移记录的每日情况
     * @param parent
     * @return
     */
    List<MigrationEverydayProcess> getMigrationEverydayProcessById(String parent);
}
