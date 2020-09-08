/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.huawei.sharedrive.isystem.exception.BusinessException;

import pw.cdmi.core.zk.ZookeeperServer;

/**
 * @author s00108907
 * 
 */
public class ZKShiroSessionDAO extends AbstractSessionDAO
{
    private static Logger logger = LoggerFactory.getLogger(ZKShiroSessionDAO.class);
    
    /**
     * 缓存根路径, 结尾不加/
     */
    private String shiroSessionZKPath = "/ISYSTEM-SHIROSESSIONS";
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(shiroSessionZKPath);
            if (stat == null)
            {
                zkClient.create().forPath(shiroSessionZKPath);
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
    
    /**
     * 设置Shiro在ZK服务器存放根路径
     * 
     * @param shiroSessionZKPath 默认值：/SHIROSESSIONS/
     */
    public void setShiroSessionZKPath(String shiroSessionZKPath)
    {
        this.shiroSessionZKPath = shiroSessionZKPath;
    }
    
    /**
     * 生成全路径
     * 
     * @param sessionId
     * @return
     */
    private String getPath(Serializable sessionId)
    {
        return shiroSessionZKPath + '/' + sessionId.toString();
    }
    
    @Override
    public void delete(Session session)
    {
        String path = getPath(session.getId());
        try
        {
            zkClient.delete().forPath(path);
        }
        catch (Exception e)
        {
            logger.error("delete error!", e);
        }
    }
    
    @Override
    public Collection<Session> getActiveSessions()
    {
        Set<Session> sessions = new HashSet<Session>(16);
        try
        {
            List<String> ss = zkClient.getChildren().forPath(shiroSessionZKPath);
            Session session = null;
            for (String id : ss)
            {
                session = doReadSession(id);
                if (session != null)
                {
                    sessions.add(session);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("getActiveSessions error!", e);
        }
        return sessions;
    }
    
    @Override
    public void update(Session session) throws UnknownSessionException
    {
        String path = getPath(session.getId());
        byte[] data = SerializationUtils.serialize(session);
        try
        {
            zkClient.setData().forPath(path, data);
        }
        catch (Exception e)
        {
            logger.error("update error!", e);
        }
    }
    
    @Override
    protected Serializable doCreate(Session session)
    {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        String path = getPath(session.getId());
        byte[] data = SerializationUtils.serialize(session);
        try
        {
            zkClient.create().forPath(path, data);
        }
        catch (Exception e)
        {
            logger.error("doCreate error!", e);
        }
        return sessionId;
    }
    
    @Override
    protected Session doReadSession(Serializable id)
    {
        Session session = null;
        try
        {
            byte[] byteData = zkClient.getData().forPath(getPath(id));
            if (byteData != null && byteData.length > 0)
            {
                session = (Session) SerializationUtils.deserialize(byteData);
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
        return session;
    }
    
}
