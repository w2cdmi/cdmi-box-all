package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.util.Date;

import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;

/**
 * 数据迁移记录服务接口
 * @author 77235
 *
 */
public interface IMigrationRecordService {
	/**
	 * 新增迁移记录
	 * @param migrationRecord
	 */
    void save(MigrationRecord migrationRecord);
 
    /**
     * 清除离职用户信息
     * @param optTime
     * @param submeterIndex
     */
    void cleanExpiredMigrationRecord(Date optTime, int submeterIndex);
    
    /**
     * 判断用户是否已经进行了离职处理
     * @param enterpriseId
     * @param userId
     * @return
     */
    boolean isMigratedForUser(long enterpriseId, long userId);
}
