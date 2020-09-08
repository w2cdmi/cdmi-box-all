package com.huawei.sharedrive.uam.enterpriseuser.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;

/**
 * 迁移记录DAO
 * @author 77235
 *
 */
public interface IMigrationRecordDao {
    
    int TABLE_COUNT = 100;
    
    /**
     * 新增迁移记录
     * @param migrationRecord
     */
    void save(MigrationRecord migrationRecord);
    
    /**
     * 更新记录状态
     * @param tableIndex
     * @param recordId
     * @param status
     */
    void updateRecordStatus(int tableIndex, long recordId, int status);
    
    /**
     * 根据离职用户编号查询数据迁移记录
     * @param enterpriseId
     * @param departureUserId
     * @return
     */
    boolean isMigratedForSpecialDepartureUser(long enterpriseId, long departureUserId);
    
    /**
     * 根据表索引编号查询待清理的移交记录
     * @param tableIndex
     * @return
     */
    List<MigrationRecord> findBeCleanedRecords(int tableIndex);
    
    /**
     * 获取最大的编号值
     * @return
     */
    long getMaxRecordId();
}
