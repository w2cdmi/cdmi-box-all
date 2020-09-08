package pw.cdmi.box.uam.user.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.cache.CacheClient;

@Component
public class MemCacheShiroSessionDAO extends AbstractSessionDAO
{
    
    public static final String CACHE_KEY_SESSION_PREFIX = "shiro_session_";
    
    private static Logger logger = LoggerFactory.getLogger(MemCacheShiroSessionDAO.class);
    
    @Autowired
    private AdminService adminService;
    
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
        Object obj = session.getAttribute(Constants.SESS_OBJ_KEY);
        if (obj == null)
        {
            return session;
        }
        Admin admin = adminService.get((Long) obj);
        if (admin == null)
        {
            logger.debug("admin for " + id + " is null");
            cacheClient.deleteCache(sessionKey);
            return null;
        }
        obj = session.getAttribute(Constants.SESS_ROLE_KEY);
        if (obj == null)
        {
            return session;
        }
        if (checkRolesNotEQ(obj, admin))
        {
            cacheClient.deleteCache(sessionKey);
            return null;
        }
        return session;
    }
    
    private boolean checkRolesNotEQ(Object obj, Admin admin)
    {
        if (!(obj instanceof String))
        {
            logger.debug("session attr " + Constants.SESS_ROLE_KEY + " is not String");
            return true;
        }
        String roleNames = (String) obj;
        String[] roles = roleNames.split(",");
        Set<AdminRole> roelSet = admin.getRoles();
        if (roles.length != roelSet.size())
        {
            logger.debug("old role size  is " + roles.length + "new role size is " + roelSet.size());
            return true;
        }
        HashSet<String> nameSet = new HashSet<String>(10);
        for (AdminRole role : roelSet)
        {
            nameSet.add(role.name());
        }
        for (String role : roles)
        {
            if (!nameSet.contains(role))
            {
                logger.debug("new role set does not contains " + role);
                return true;
            }
        }
        return false;
    }
    
    private String getSessionKey(Serializable sessionId)
    {
        return CACHE_KEY_SESSION_PREFIX + sessionId.toString();
    }
}
