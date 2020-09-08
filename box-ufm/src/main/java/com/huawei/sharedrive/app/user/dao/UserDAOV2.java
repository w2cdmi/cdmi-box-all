package com.huawei.sharedrive.app.user.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.user.domain.User;

public interface UserDAOV2 {
    /**
     * 创建对象
     * 
     */
    void create(User user);
    
    /**
     * 删除对象
     * 
     */
    void delete(long accountId, long userId);
    
    /**
     * 获取指定对象
     * 
     * @return
     */
    User get(long userId);
    
    /**
     * 更新对象
     * 
     */
    void update(User user);
    
    /**
     * 获取user表最大ID值
     * 
     * @return
     */
    long getMaxUserId();
    
    /**
     * 获取用户最大版本数
     * 
     * @return
     */
    int getMaxVersions(long userId);
    
    /**
     * 用户id生成器，获取一个可用的用户id
     * 
     * @return
     */
    
    /**
     * 根据objectSid获取用户对象
     * 
     * @param objectSid
     * @return
     */
    User getUserByObjectSid(String objectSid, long accountId);
    
    List<User> getUserByStatus(String status);
    
    /**
     * 更新最新登录时间
     * 
     * @param lastLoginAt
     */
    void updateLastLoginTime(long accountId, long id, Date lastLoginAt);
    
    /**
     * 修改用户区域
     * 
     * @param id 要更新的用户ID
     * @param regionID 区域ID
     */
    void updateRegionID(long accountId, long id, int regionID);
    
    /**
     * 更新用户统计信息, 包括已用空间和文件总数, 设置为null则不更新
     * 
     * @param id
     * @param spaceUsed
     * @param fileCount
     * @return
     */
    int updateStatisticInfo(long accountId, long id, Long spaceUsed, Long fileCount);
    
    /**
     * 更新用户统计信息并刷新缓存, 包括已用空间和文件总数, 设置为null则不更新
     * 
     * @param id
     * @param spaceUsed
     * @param fileCount
     * @return
     */
    int updateStatisticInfoAndRefreshCache(User user,long accountId, long id, Long spaceUsed, Long fileCount);
    
    /**
     * 修改用户状态为enable/disable
     * 
     * @param id 要更新的用户ID
     * @param status 新状态
     */
    void updateStatus(long accountId, long id, String status);
    
    int updateSecurityId(long accountId, long id, Integer securityId);

    void updateSpaceQuota(long userId, long spaceQuota);
}