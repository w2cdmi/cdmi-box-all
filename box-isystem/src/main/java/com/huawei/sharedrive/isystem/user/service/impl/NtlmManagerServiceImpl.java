package com.huawei.sharedrive.isystem.user.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.user.domain.UserLocked;
import com.huawei.sharedrive.isystem.user.service.NtlmManagerService;

import pw.cdmi.core.zk.ZookeeperServer;

@Component("ntlmManagerService")
public class NtlmManagerServiceImpl implements NtlmManagerService
{
    private static Logger logger = LoggerFactory.getLogger(NtlmManagerServiceImpl.class);
    
    /**
     * 缓存根路径, 结尾不加/
     */
    private String ntmlUserCaches = "/ISYSTEM-NTMLUSERCACHES";
    
    @Resource(name = "zookeeperServer")
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    @PostConstruct
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(ntmlUserCaches);
            if (stat == null)
            {
                zkClient.create().forPath(ntmlUserCaches);
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
    public void deleteUserLocked(String userName)
    {
        String path = getPath(userName);
        try
        {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat != null)
            {
                zkClient.delete().forPath(path);
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
            logger.warn("Session id not found", e);
        }
        return userLocked;
    }
    
    /**
     * 生成全路径
     * 
     * @param sessionId
     * @return
     */
    private String getPath(String hashId)
    {
        return ntmlUserCaches + '/' + hashId;
    }
    
}
