package com.huawei.sharedrive.app.files.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;

/**
 * 统计任务
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-9-4
 * @see
 * @since
 */
public class StatisticsTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsTask.class);
    
    private Long userId;
    
    private UserDAOV2 userDao;
    
    private SpaceStatisticsService spaceStatisticsService;
    
    public StatisticsTask(UserDAOV2 userDao, SpaceStatisticsService spaceStatisticsService, Long userId)
    {
        this.userDao = userDao;
        this.userId = userId;
        this.spaceStatisticsService = spaceStatisticsService;
    }
    
    @Override
    public void run()
    {
        try
        {
            User user = userDao.get(userId);
            UserStatisticsInfo userInfo = spaceStatisticsService.getUserCurrentInfo(userId,
                user.getAccountId());
            LOGGER.info("Statistics task execute succeed. User: {}, spaceQuota: {}, spaceUsed: {}, fileCount: {}",
                userId,
                user.getSpaceQuota(),
                userInfo.getSpaceUsed(),
                userInfo.getFileCount());
        }
        catch (Exception e)
        {
            LOGGER.warn("Statistics task execute failed!", e);
        }
    }
    
}
