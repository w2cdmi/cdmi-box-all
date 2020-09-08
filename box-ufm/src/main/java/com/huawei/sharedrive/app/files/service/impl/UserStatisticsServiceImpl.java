/**
 * 通过zookeeper进行用户使用空间统计
 */
package com.huawei.sharedrive.app.files.service.impl;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.UserStatisticsService;
import com.huawei.sharedrive.app.files.service.job.StatisticsInfo;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class UserStatisticsServiceImpl implements UserStatisticsService
{
    /** 空间信息Zookeeper缓存根路径 */
    public static final String USER_STATISTICS_PATH = "/UserStatistics";
    
    /** 未知的应用ID */
    private static final String APP_ID_OTHERS = "others";
    
    private static final String CHARSET_UTF8 = "UTF-8";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserDAOV2 userDao;
    
    @Autowired
    private UserReverseDAO userReverseDao;
    
    private CuratorFramework zkClient;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public void consumeEvent(Event event)
    {
        UserToken userToken = event.getUserToken();
        
        // 文件大小
        long size = 0;
        // 新增/删除的节点数
        long fileCount = 0;
        
        if (event.getSource() != null)
        {
            size = event.getSource().getSize();
            fileCount = event.getSource().getFileCount();
        }
        
        if (size == 0 && fileCount == 0)
        {
            return;
        }
        
        switch (event.getType())
        {
            case INODE_CREATE:
            case INODE_PRELOAD_END:
            case INODE_COPY:
            case VERSION_RESTORE:
                add(userToken, size, fileCount);
                break;
            case INODE_CONTENT_CHANGE:
                if (event.getDest() != null)
                {
                    INode oldNode = event.getDest();
                    reduce(userToken, oldNode.getSize(), oldNode.getFileCount());
                }
                add(userToken, size, fileCount);
                break;
            case VERSION_DELETE:
            case TRASH_CLEAR:
            case TRASH_INODE_DELETE:
                reduce(userToken, size, fileCount);
                break;
            default:
                break;
        }
    }
    
    @Override
    public StatisticsInfo getAndRefreshStatisticsInfo(long userId, int checkPeriods)
    {
        User user = userDao.get(userId);
        if (null == user)
        {
            throw new NoSuchUserException("No such user:" + userId);
        }
        
        long spaceQuota = user.getSpaceQuota();
        long spaceUsed = user.getSpaceUsed();
        long fileCount = user.getFileCount();
        
        // 上次统计时间
        Date lastStatisticsTime = user.getLastStatisticsTime();
        
        // 当前时间减去统计周期后, 如果在上次统计时间之后, 需要重新实时统计
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, -checkPeriods);
        Date nextStatisticsTime = now.getTime();
        if (lastStatisticsTime == null || nextStatisticsTime.after(lastStatisticsTime))
        {
            spaceUsed = getUsedSpace(userId);
            fileCount = getTotalFiles(userId);
            updateStatisticsInfo(user.getAccountId(), userId, spaceUsed, fileCount);
            LOGGER.info("Update statistics. User: {}, spaceUsed: {}, fileCount: {}",
                userId,
                spaceUsed,
                fileCount);
        }
        StatisticsInfo statisticsInfo = new StatisticsInfo();
        statisticsInfo.setSpaceQuota(spaceQuota);
        statisticsInfo.setSpaceUsed(spaceUsed);
        statisticsInfo.setFileCount(fileCount);
        return statisticsInfo;
    }
    
    @Override
    public StatisticsInfo RefreshStatisticsInfoAndCache(long userId, long sizes, long counts)
    {
        User user = userDao.get(userId);
        if (null == user)
        {
            throw new NoSuchUserException("No such user:" + userId);
        }

        long spaceQuota = user.getSpaceQuota();
        long spaceUsed = user.getSpaceUsed();
        long fileCount = user.getFileCount();
        
        spaceUsed += sizes;
        fileCount += counts;
        // 更新数据库与缓存
        userDao.updateStatisticInfoAndRefreshCache(user,user.getAccountId(), userId, spaceUsed, fileCount);
        userReverseDao.updateStatisticInfoAndRefreshCach(user.getAccountId(), userId, spaceUsed, fileCount);
        
        StatisticsInfo statisticsInfo = new StatisticsInfo();
        statisticsInfo.setSpaceQuota(spaceQuota);
        statisticsInfo.setSpaceUsed(spaceUsed);
        statisticsInfo.setFileCount(fileCount);
        return statisticsInfo;
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INODE_CONTENT_CHANGE, EventType.INODE_COPY, EventType.INODE_CREATE,
            EventType.INODE_PRELOAD_END, EventType.TRASH_CLEAR, EventType.TRASH_INODE_DELETE,
            EventType.VERSION_DELETE, EventType.VERSION_RESTORE};
    }
    
    @Override
    public long getTotalFiles(long userId)
    {
        return fileService.getUserTotalFiles(userId);
    }
    
    @Override
    public long getUsedSpace(long userId)
    {
        // 单位 Byte
        return fileService.getUserTotalSpace(userId);
    }
    
    @PostConstruct
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(USER_STATISTICS_PATH);
            if (stat == null)
            {
                zkClient.create().forPath(USER_STATISTICS_PATH);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("init user statistics path fail!", e);
        }
    }
    
    @Override
    public void initUser(User user)
    {
        try
        {
            StatisticsInfo info = new StatisticsInfo();
            info.setFileCount(0L);
            info.setLastStatisticsTime(DateUtils.getDate());
            info.setSpaceQuota(user.getSpaceQuota());
            info.setSpaceUsed(0L);
            byte[] bytes = JsonUtils.toJson(info).getBytes(CHARSET_UTF8);
            
            // 创建应用缓存路径
            createAppPath(user.getAppId());
            
            // 创建用户缓存路径
            zkClient.create().forPath(getUserPath(user.getId(), user.getAppId()), bytes);
        }
        catch (Exception e)
        {
            LOGGER.warn("Init statistics fail!.userId: {}", user.getId(), e);
        }
    }
    
    @Override
    public void updateStatisticsInfo(long accountId, long userId, Long spaceUsed, Long fileCount)
    {
        // 更新数据库
        userDao.updateStatisticInfo(accountId, userId, spaceUsed, fileCount);
        userReverseDao.updateStatisticInfo(accountId, userId, spaceUsed, fileCount);
    }
    
    /**
     * 对用户ID增加已用容量信息，不做原子操作，允许并发时出现的容量统计错误
     * 
     * @param userId
     * @param value
     */
    private void add(UserToken userToken, long size, long fileCount)
    {
        long userId = userToken.getId();
        String appId = userToken.getAppId();
        String path = getUserPath(userId, appId);
        try
        {
            byte[] bytes = zkClient.getData().forPath(path);
            String content = new String(bytes, CHARSET_UTF8);
            StatisticsInfo statisticsInfo = JsonUtils.stringToObject(content, StatisticsInfo.class);
            if (null == statisticsInfo)
            {
                LOGGER.warn("get statisticsInfo from zookeeper failed.");
                return;
            }
            long spaceUsed = statisticsInfo.getSpaceUsed();
            long currentFileCount = statisticsInfo.getFileCount();
            spaceUsed += size;
            currentFileCount += fileCount;
            statisticsInfo.setSpaceUsed(spaceUsed);
            statisticsInfo.setFileCount(currentFileCount);
            
            String newStatisticsInfo = JsonUtils.toJson(statisticsInfo);
            zkClient.setData().forPath(path, newStatisticsInfo.getBytes(CHARSET_UTF8));
        }
        catch (KeeperException.NoNodeException e)
        {
            try
            {
                createAppPath(appId);
                StatisticsInfo statisticsInfo = getStatisticsInfoFromDB(userId);
                String info = JsonUtils.toJson(statisticsInfo);
                zkClient.create().forPath(path, info.getBytes(CHARSET_UTF8));
            }
            catch (KeeperException.NodeExistsException e1)
            {
                LOGGER.info("Node exist. Add statistics fail! retry add. UserId: {} , size: {}, fileCount: {}",
                    userId,
                    size,
                    fileCount);
                add(userToken, size, fileCount);
            }
            catch (Exception e2)
            {
                LOGGER.warn("Add statistics fail!  UserId: {} , size: {}, fileCount: {}",
                    userId,
                    size,
                    fileCount,
                    e2);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Add statistics fail! userId: {} , size: {}, fileCount: {}",
                userId,
                size,
                fileCount,
                e);
        }
    }
    
    /**
     * 创建应用缓存路径
     * 
     * @param appId
     */
    private void createAppPath(String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            appId = APP_ID_OTHERS;
        }
        
        String appPath = new StringBuffer(USER_STATISTICS_PATH).append('/').append(appId).toString();
        try
        {
            zkClient.create().forPath(appPath);
        }
        catch (KeeperException.NodeExistsException e)
        {
            LOGGER.info("App path exist. app id: {}", appId);
        }
        catch (Exception e)
        {
            LOGGER.warn("Create app path failed! app id : {}", appId, e);
        }
    }
    
    /**
     * 从数据库中查询用户统计信息
     * 
     * @param userId
     * @return
     */
    private StatisticsInfo getStatisticsInfoFromDB(long userId)
    {
        User user = userDao.get(userId);
        long fileCount = getTotalFiles(userId);
        long spaceUsed = getUsedSpace(userId);
        StatisticsInfo info = new StatisticsInfo();
        info.setSpaceQuota(user.getSpaceQuota());
        info.setFileCount(fileCount);
        info.setSpaceUsed(spaceUsed);
        info.setLastStatisticsTime(DateUtils.getDate());
        return info;
    }
    
    /**
     * 获取用户缓存路径
     * 
     * @param userId
     * @param appId
     * @return
     */
    private String getUserPath(Long userId, String appId)
    {
        if (StringUtils.isBlank(appId))
        {
            appId = APP_ID_OTHERS;
        }
        return new StringBuffer(USER_STATISTICS_PATH).append('/')
            .append(appId)
            .append('/')
            .append(userId)
            .toString();
    }
    
    /**
     * 对用户ID减少已用容量信息，不做原子操作，允许并发时出现的容量统计错误
     * 
     * @param userId
     * @param size
     */
    private void reduce(UserToken userToken, long size, long fileCount)
    {
        long userId = userToken.getId();
        String appId = userToken.getAppId();
        String path = getUserPath(userId, appId);
        try
        {
            byte[] bytes = zkClient.getData().forPath(path);
            String content = new String(bytes, CHARSET_UTF8);
            StatisticsInfo statisticsInfo = JsonUtils.stringToObject(content, StatisticsInfo.class);
            if (statisticsInfo != null)
            {
                long spaceUsed = statisticsInfo.getSpaceUsed();
                long currentFileCount = statisticsInfo.getFileCount();
                spaceUsed -= size;
                currentFileCount -= fileCount;
                statisticsInfo.setFileCount(currentFileCount);
                statisticsInfo.setSpaceUsed(spaceUsed);
                bytes = JsonUtils.toJson(statisticsInfo).getBytes(CHARSET_UTF8);
                zkClient.setData().forPath(path, bytes);
            }
        }
        catch (KeeperException.NoNodeException e)
        {
            try
            {
                createAppPath(appId);
                StatisticsInfo statisticsInfo = getStatisticsInfoFromDB(userId);
                byte[] bytes = JsonUtils.toJson(statisticsInfo).getBytes(CHARSET_UTF8);
                zkClient.create().forPath(path, bytes);
            }
            catch (KeeperException.NodeExistsException e1)
            {
                LOGGER.info("Node exists, reduce statistics fail! Retry reduce. User: {}, size: {}, fileCount: {}",
                    userId,
                    size,
                    fileCount);
                reduce(userToken, size, fileCount);
            }
            catch (Exception e2)
            {
                LOGGER.warn("Reduce statistics fail!  User: {}, size: {}, fileCount: {}",
                    userId,
                    size,
                    fileCount,
                    e2);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Reduce statistics fail!  User: {}, size: {}, fileCount: {}",
                userId,
                size,
                fileCount,
                e);
        }
    }
}
