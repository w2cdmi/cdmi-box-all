package com.huawei.sharedrive.app.user.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserReverseDAO
{
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
     * 更新对象
     * 
     */
    void update(User user);
    
    /**
     * 获取对象列表
     * 
     * @param filter 过滤参数，设置相应属性值，支持name，loginname，email模糊查找。为空表示不做过滤
     * @param order 排序参数。为空表示不做排序
     * @param limit 分页参数。为空表示不做分页
     * @return
     */
    List<User> getFilterd(User filter, OrderV1 order, Limit limit);
    
    /**
     * 获取用户的文件总数和空间使用 用于统计
     * 
     * @param user
     * @param type
     * @return
     */
    List<User> getUsedCapacity(Map<String, Object> map);
    
    /**
     * 统计 空间总数
     * 
     * @param user
     * @param type
     * @return
     */
    long countSpaceTotal(Map<String, Object> map);
    
    /**
     * 获取用户总数
     * 
     * @param filter 过滤参数，设置相应属性值，支持name，loginname，email模糊查找。为空表示不做过滤
     * @return
     */
    int getFilterdCount(User filter);
    
    /**
     * 用户id生成器，获取一个可用的用户id
     * 
     * @return
     */
    
    /**
     * 获取排序后的用户列表
     * 
     * @param filter
     * @param orderList
     * @param offset
     * @param limit
     * @return
     */
    List<User> getOrderedUserList(User filter, List<Order> orderList, long offset, int limit);
    
    /**
     * 根据应用ID获取用户总数
     * 
     * @param appId
     * @return
     */
    int getUserCountByAccountId(long accountId);
    
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
    int updateStatisticInfoAndRefreshCach(long accountId, long id, Long spaceUsed, Long fileCount);
    
    /**
     * 修改用户状态为enable/disable
     * 
     * @param id 要更新的用户ID
     * @param status 新状态
     */
    void updateStatus(long accountId, long id, String status);
    
    /**
     * @param loginName
     * @param accountId
     * @return
     */
    User getUserByLoginNameAccountId(String loginName, long accountId);
    
    /**
     * 获取一个user
     * 
     * @param accountId
     * @param id
     * @return
     */
    User getOneUserOrderByACS(long accountId, long id);
    
    int updateSecurityId(long accountId, long id, Integer securityId);
    
    /**
     * 根据应用ID获取非删除状态的用户总数
     * 
     * @param appId
     * @return
     */
    int countActiveUserByAccountId(long accountId);
    
    /**
     * 根据应用ID获取非删除状态的团队空间总数
     * 
     * @param appId
     * @return
     */
    int countActiveTeamspaceByAccountId(long accountId);
    
    AccountStatisticsInfo getAccountInfoById(long accountId);
    
    /**
     * 获取用户信息
     * @param accountId
     * @param cloudUserId
     * @return
     */
    User getBycloudUserId(long accountId, long cloudUserId);
    
    /**
     * 更新用户基本信息
     * @param user
     */
    void updateBaseInfo(User user);

    //将accountId下配额为spaceQuota的用户账号
    List<User> getByAccountIdAndSpaceQuota(long accountId, long spaceQuota);

    //将accountId下所有配额为oldValue的调整为newValue
    void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue);

    //修改空间配额
    void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long spaceQuota);
}