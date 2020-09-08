package pw.cdmi.box.uam.user.service.impl;

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

import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.UserLockedException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.log.service.UserLogService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.UserLocked;
import pw.cdmi.box.uam.user.service.AccountRetryCountService;
import pw.cdmi.box.uam.util.PropertiesUtils;
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
    
    /**
     * 缓存根路径, 结尾不加/
     */
    private String accountCountCache = "/BMS-ACCOUNT-COUNT-CACHES";
    
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
                String logId = systemLogManager.saveFailLog(request,
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
    public void checkUserLocked(String userName, HttpServletRequest request) throws UserLockedException
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        UserLocked userLocked = doReadUserLocked(userName);
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
        // 超出时间限制则清空数据
        if (lockDateSeconds > LOCK_DATE_LIMIT)
        {
            logger.warn("user account unlocked for " + userName);
            deleteUserLocked(userName, request);
        }
        // 未超出且登录次数不低于上限，拦截登录
        if (lockDateSeconds <= LOCK_DATE_LIMIT && loginTime >= (LOCK_TIME_LIMIT - 1))
        {
            // 第一次校验出锁定，更新锁定时间
            if (loginTime == (LOCK_TIME_LIMIT - 1))
            {
                Date recentDate = new Date();
                userLocked.setLoginTimes(userLocked.getLoginTimes() + 1);
                userLocked.setLoginDate(recentDate);
                doCreateUserLocked(userName, userLocked);
                
                Alarm alarm = new ManagerLockedAlarm(managerLockedAlarm, userName);
                alarmHelper.sendAlarm(alarm);
                String[] description = new String[]{admin.getLoginName()};
                String logId = systemLogManager.saveFailLog(request,
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
     * 添加锁定数据
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
