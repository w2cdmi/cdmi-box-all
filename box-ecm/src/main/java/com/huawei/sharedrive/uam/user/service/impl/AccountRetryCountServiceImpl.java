package com.huawei.sharedrive.uam.user.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.SecurityUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.ModifyPasswdLocked;
import com.huawei.sharedrive.uam.user.service.AccountRetryCountService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.core.alarm.ManagerLockedAlarm;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class AccountRetryCountServiceImpl implements AccountRetryCountService
{
    private static Logger logger = LoggerFactory.getLogger(AccountRetryCountServiceImpl.class);
    
    private static final int LOCK_TIME_LIMIT = 5;
    
    private static final long LOCK_DATE_LIMIT = Integer.parseInt(PropertiesUtils.getProperty("account.chgpwd.lock.time",
        "300000"));
    
    private String accountCountCache = "/UAM-ACCOUNT-COUNT-CACHES";
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private ManagerLockedAlarm managerLockedAlarm;
    
    @Autowired
    protected UserLogService userLogService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
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
    public void deleteUserLocked(String userName, HttpServletRequest request)
    {
        String path = getPath(userName);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        try
        {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat != null)
            {
                zkClient.delete().forPath(path);
                String[] description = new String[]{admin.getLoginName()};
                String logId = systemLogManager.save(request,
                    OperateType.UnlockModifyPassword,
                    OperateDescription.UNLOCK_MODIFY_PWD,
                    null,
                    description);
                systemLogManager.updateSuccess(logId);
            }
        }
        catch (Exception e)
        {
            logger.error("delete error!", e);
        }
        
    }
    
    @Override
    public void doCreateUserLocked(String userName, ModifyPasswdLocked userLocked)
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
    public ModifyPasswdLocked doReadUserLocked(String userName)
    {
        ModifyPasswdLocked userLocked = null;
        try
        {
            byte[] byteData = zkClient.getData().forPath(getPath(userName));
            if (byteData != null && byteData.length > 0)
            {
                userLocked = (ModifyPasswdLocked) SerializationUtils.deserialize(byteData);
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
    public void checkUserLocked(String userName, HttpServletRequest request) throws UserLockedException
    {
        ModifyPasswdLocked userLocked = doReadUserLocked(userName);
        if (userLocked == null)
        {
            return;
        }
        int loginTime = userLocked.getLoginTimes();
        Date lockDate = userLocked.getLoginDate();
        if (lockDate == null)
        {
            return;
        }
        Date nowDate = new Date();
        long lockDateSeconds = nowDate.getTime() - lockDate.getTime();
        if (lockDateSeconds > LOCK_DATE_LIMIT)
        {
            logger.warn("user account unlocked for " + userName);
            deleteUserLocked(userName, request);
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
                String[] description = new String[]{userName};
                String logId = systemLogManager.save(request,
                    OperateType.LockModifyPassword,
                    OperateDescription.LOCK_MODIFY_PWD,
                    null,
                    description);
                systemLogManager.updateSuccess(logId);
            }
            logger.warn("user change password locked for " + userName);
            
            throw new UserLockedException();
        }
        
    }
    
    /**
     */
    @Override
    public void addUserLocked(String userName)
    {
        ModifyPasswdLocked ntUserLocked = doReadUserLocked(userName);
        if (ntUserLocked != null)
        {
            ntUserLocked.setLoginTimes(ntUserLocked.getLoginTimes() + 1);
            doCreateUserLocked(userName, ntUserLocked);
        }
        else
        {
            ModifyPasswdLocked userLocked = new ModifyPasswdLocked();
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
