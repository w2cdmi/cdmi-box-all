package com.huawei.sharedrive.app.files.manager;

import java.util.List;

import com.huawei.sharedrive.app.files.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.app.files.dto.MigrationRecordDto;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Order;

/**
 * 数据移交接口
 * @author 77235
 *
 */
public interface IDataMigrationManager {
	
	String EX_MIGRATION_TEAMSPACE_ERROR = "migration.migrate.teamspace.error";
	
	String EX_MIGRATION_GROUP_ERROR = "migration.migrate.group.error";
	
	String EX_MIGRATION_PERSONAL_FILE_ERROR = "migration.migrate.personal.folder.error";
    /**
     * 更新账户基本信息
     * @param accountId
     * @param cloudUserId
     * @param migrationRequest
     */
    void updateAccountUser(Long accountId, Long cloudUserId, DataMigrationRequestDto migrationRequest) ;
    
    /**
     * 获取用户账号信息
     * @param accountId
     * @param cloudUserId
     * @return
     */
    User getAccountUserByAccountAndUserId(Long accountId, Long cloudUserId);
    
    /**
     * 列举用户所在团队空间
     * @param userId
     * @param userType
     * @param orderList
     * @return
     */
    List<Long> listUserTeamSpaceIds(String userId, String userType, List<Order> orderList);
    
    /**
     * 根据用户虚拟编号获取用户基本信息
     * @param cloudUserId
     * @return
     */
    User getUserByCloudUserId(Long cloudUserId);
    
   /**
    * 更新用户基本信息
    * @param cloudUserId
    * @param migrationRequest
    */
    void updateUser(Long cloudUserId, DataMigrationRequestDto migrationRequest) ;
    
    /**
     * 离职用户数据迁移
     * @param userToken
     * @param recipientUser
     * @param migrationRequest
     * @param folderName
     * @return
     */
    Long migrateData(UserToken userToken, User recipientUser, DataMigrationRequestDto migrationRequest, String folderName);
    
    /**
     * 清理离职用户信息
     * @param userToken
     * @param migrationRecord
     */
    void cleanDepartureUserInfo(UserToken userToken, MigrationRecordDto migrationRecords);
}
