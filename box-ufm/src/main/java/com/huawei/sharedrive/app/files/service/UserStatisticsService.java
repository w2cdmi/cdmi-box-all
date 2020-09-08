/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.files.service.job.StatisticsInfo;
import com.huawei.sharedrive.app.user.domain.User;

/**
 * 用户容量统计服务
 * 
 * @author s00108907
 * 
 */
public interface UserStatisticsService extends EventConsumer
{
    /**
     * 获取统计信息, 根据最后一次统计时间和checkPeriods判断是否需要重新实时统计。 重新统计后将统计信息更新至数据库，如果不需要重新统计则直接从数据库查询
     * 
     * @param userId 用户ID
     * @param checkPeriods 统计周期, 单位秒
     * @return
     */
    StatisticsInfo getAndRefreshStatisticsInfo(long userId, int checkPeriods);
  
    /**
     * 获取用户文件总数(含文件/文件夹/版本/回收站中的数据)
     * 
     * @param userId
     * @return
     */
    long getTotalFiles(long userId);
    
    /**
     * 根据用户ID获取用户已用空间容量
     * 
     * @param userId 用户ID
     * @return 容量值
     */
    long getUsedSpace(long userId);
    
    /**
     * 初始化用户统计信息，设置为0
     * 
     * @param user
     */
    void initUser(User user);
    
    /**
     * 更新用户统计信息, 包括已用空间和文件总数, 设置为null则不更新
     * 
     * @param userId
     * @param appId
     * @param spaceUsed
     * @param fileCount
     * @param lastStatisticsTime
     */
    void updateStatisticsInfo(long accountId, long userId, Long spaceUsed, Long fileCount);
    
    /**
     * 
     * @Title: RefreshStatisticsInfoAndCache
     * @Description:获取统计信息,并刷新缓存
     *
     * @param userId
     * @param sizes
     * @param counts
     * @return StatisticsInfo
     * @throws
     */
    StatisticsInfo RefreshStatisticsInfoAndCache(long userId, long sizes, long counts);
}
