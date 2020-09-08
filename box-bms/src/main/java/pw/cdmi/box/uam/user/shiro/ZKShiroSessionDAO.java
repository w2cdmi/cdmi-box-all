package pw.cdmi.box.uam.user.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import pw.cdmi.core.utils.ZookeeperUtil;
import pw.cdmi.core.zk.ZookeeperServer;

public class ZKShiroSessionDAO extends AbstractSessionDAO
{
    private static Logger logger = LoggerFactory.getLogger(ZKShiroSessionDAO.class);
    
    private String shiroSessionZKPath = "/ISYSTEM-SHIROSESSIONS";
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            ZookeeperUtil.safeCreateNode(zkClient, shiroSessionZKPath, new byte[0]);
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
    
    public void setShiroSessionZKPath(String shiroSessionZKPath)
    {
        this.shiroSessionZKPath = shiroSessionZKPath;
    }
    
    /**
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
            Session session;
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
        catch (Exception e)
        {
            logger.warn("Session id not found");
        }
        return session;
    }
    
}
