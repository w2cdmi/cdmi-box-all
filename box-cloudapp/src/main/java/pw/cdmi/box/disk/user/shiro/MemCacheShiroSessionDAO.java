package pw.cdmi.box.disk.user.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.common.cache.CacheClient;

@Component
public class MemCacheShiroSessionDAO extends AbstractSessionDAO
{
    
    public static final String CACHE_KEY_SESSION_PREFIX = "shiro_session_";
    
    private static Logger logger = LoggerFactory.getLogger(MemCacheShiroSessionDAO.class);
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Resource(name = "sessionIdGenerator")
    private SessionIdGenerator sessionIdGenerator;
    
    @Value("${session.expire}")
    private long sessionExpireTime;
    
    @PostConstruct
    public void init()
    {
        this.setSessionIdGenerator(sessionIdGenerator);
    }
    
    @Override
    public void delete(Session session)
    {
        String sessionKey = getSessionKey(session.getId());
        cacheClient.deleteCache(sessionKey);
    }
    
    @Override
    public Collection<Session> getActiveSessions()
    {
        return Collections.<Session> emptySet();
    }
    
    @Override
    public void update(Session session) throws UnknownSessionException
    {
        String sessionKey = getSessionKey(session.getId());
        Date expireTime = new Date(System.currentTimeMillis() + sessionExpireTime);
        boolean success = cacheClient.setCache(sessionKey, session, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store session to memcache failed.");
        }
    }
    
    @Override
    protected Serializable doCreate(Session session)
    {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        String sessionKey = getSessionKey(session.getId());
        Date expireTime = new Date(System.currentTimeMillis() + sessionExpireTime);
        boolean success = cacheClient.setCache(sessionKey, session, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store session to memcache failed.");
        }
        return sessionId;
    }
    
    @Override
    protected Session doReadSession(Serializable id)
    {
        String sessionKey = getSessionKey(id);
        Session session = (Session) cacheClient.getCache(sessionKey);
        if (session == null)
        {
            logger.debug("session is null");
            return null;
        }
        Object obj = session.getAttribute(User.SESSION_ID_KEY);
        if (obj == null)
        {
            return session;
        }
        return session;
    }
    
    private String getSessionKey(Serializable sessionId)
    {
        return CACHE_KEY_SESSION_PREFIX + sessionId.toString();
    }
}
