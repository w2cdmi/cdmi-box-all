package com.huawei.sharedrive.uam.user.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.AccountCountService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.alarm.ManagerLockedAlarm;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class AccountCountServiceImpl implements AccountCountService
{
    private static Logger logger = LoggerFactory.getLogger(AccountCountServiceImpl.class);
    
    private static final int LOCK_TIME_LIMIT = 5;
    
    private static final long LOCK_DATE_LIMIT = Integer.parseInt(PropertiesUtils.getProperty("account.chgpwd.lock.time",
        "300000"));
    
    /**
     */
    private String accountCountCache = "/CLOUDAPP-ACCOUNT-COUNT-CACHES";
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private ManagerLockedAlarm managerLockedAlarm;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @PostConstruct
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(accountCountCache);
            if (stat == null)
            {
                zkClient.create().forPath(accountCountCache);
            }
        }
        catch (Exception e)
        {
            logger.error("init ZKShiroSessionDAO fail!", e);
        }
    }
    
    public void setZookeeperServer(ZookeeperServer zookeeperServer)
    {
        this.zookeeperServer = zookeeperServer;
    }
    
    @Override
    public void deleteUserLocked(@RequestHeader("Authorization") String authorization, String userName,
        String appId, long userId)
    {
        String path = getPath(userName);
        UserToken userToken = userTokenHelper.unSafeCheckTokenAndGetUser(authorization);
        try
        {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat != null)
            {
                zkClient.delete().forPath(path);
                UserLog userLog = UserLogType.getUserLog(userToken);
                userLogService.saveFailLog(userLog,
                    userName,
                    appId,
                    new String[]{String.valueOf(userId)},
                    UserLogType.KEY_PASSWD_UNLOCK);
            }
        }
        catch (Exception e)
        {
            logger.error("delete error!", e);
        }
        
    }
    
    @Override
    public void doCreateUserLocked(String userName, UserLocked userLocked)
    {
        String path = getPath(userName);
        byte[] data = SerializationUtils.serialize(userLocked);
        
        try
        {
            if (doReadUserLocked(userName) != null)
            {
                zkClient.setData().forPath(path, data);
            }
            else
            {
                zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path, data);
            }
        }
        catch (Exception e)
        {
            logger.error("doCreate error!", e);
        }
        
    }
    
    @Override
    public UserLocked doReadUserLocked(String userName)
    {
        UserLocked userLocked = null;
        try
        {
            byte[] byteData = zkClient.getData().forPath(getPath(userName));
            if (byteData != null && byteData.length > 0)
            {
                userLocked = (UserLocked) SerializationUtils.deserialize(byteData);
            }
        }
        catch (RuntimeException e)
        {
            throw new BusinessException(e);
        }
        catch (Exception e)
        {
            logger.warn("Session id not found", e.getMessage());
        }
        return userLocked;
    }
    
    @Override
    public boolean checkUserLocked(@RequestHeader("Authorization") String authorization, String userName,
        String appId, long userId)
    {
        UserLocked userLocked = doReadUserLocked(userName);
        UserToken userToken = userTokenHelper.unSafeCheckTokenAndGetUser(authorization);
        if (userLocked == null)
        {
            return false;
        }
        int loginTime = userLocked.getLoginTimes();
        Date lockDate = userLocked.getLoginDate();
        if (lockDate == null)
        {
            return false;
        }
        Date nowDate = new Date();
        long lockDateSeconds = nowDate.getTime() - lockDate.getTime();
        if (lockDateSeconds > LOCK_DATE_LIMIT)
        {
            logger.warn("user account unlocked for " + userName);
            deleteUserLocked(authorization, userName, appId, userId);
        }
        if (lockDateSeconds <= LOCK_DATE_LIMIT && loginTime >= (LOCK_TIME_LIMIT - 1))
        {
            if (loginTime == (LOCK_TIME_LIMIT - 1))
            {
                Date recentDate = new Date();
                userLocked.setLoginTimes(userLocked.getLoginTimes() + 1);
                userLocked.setLoginDate(recentDate);
                doCreateUserLocked(userName, userLocked);
                
                Alarm alarm = new ManagerLockedAlarm(managerLockedAlarm, userName);
                alarmHelper.sendAlarm(alarm);
                UserLog userLog = UserLogType.getUserLog(userToken);
                userLogService.saveFailLog(userLog,
                    userName,
                    appId,
                    new String[]{String.valueOf(userId)},
                    UserLogType.KEY_PASSWD_LOCK);
            }
            logger.warn("user change password locked for " + userName);
            return true;
        }
        return false;
        
    }
    
    /**
     */
    @Override
    public void addUserLocked(String userName)
    {
        UserLocked ntUserLocked = doReadUserLocked(userName);
        if (ntUserLocked != null)
        {
            ntUserLocked.setLoginTimes(ntUserLocked.getLoginTimes() + 1);
            doCreateUserLocked(userName, ntUserLocked);
        }
        else
        {
            UserLocked userLocked = new UserLocked();
            userLocked.setLoginDate(new Date());
            userLocked.setLoginTimes(1);
            userLocked.setUserName(userName);
            doCreateUserLocked(userName, userLocked);
        }
    }
    
    private String getPath(String hashId)
    {
        return accountCountCache + '/' + hashId;
    }
    
}
