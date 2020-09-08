package com.huawei.sharedrive.app.message.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.BusinessConstants;

@ServerEndpoint(value = "/message/listen/{userId}/{authStr}", configurator = MessageListenConfig.class)
public class MessageListener
{
    public static final String USER_SESSOIN_KEY_PREFIX = "USER_";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    
    // 用户终端session缓存, value为用户当前登录的终端session的map集合
    private static final Map<String, Map<String, Session>> USER_CLIENT_SESSION_INFO = new ConcurrentHashMap<String, Map<String, Session>>();
    
    public static List<Session> getAllSession()
    {
        List<Session> sessions = new ArrayList<Session>(30);
        synchronized (MessageListener.class)
        {
            for (Map<String, Session> entrys : USER_CLIENT_SESSION_INFO.values())
            {
                sessions.addAll(entrys.values());
            }
        }
        
        return sessions;
    }
    
    /**
     * 获取用户当前登录终端的session
     * 
     * @param clientKey
     * @return
     */
    public static Map<String, Session> getRemoteSession(String clientKey)
    {
        Map<String, Session> sessionMap = USER_CLIENT_SESSION_INFO.get(clientKey);
        return sessionMap;
    }
    
    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam("authStr") String authStr)
    {
        LOGGER.info("Websocket disconnect, remove user session " + userId);
        
        removeClient(userId, authStr);
        
    }
    
    @OnError
    public void onError(@PathParam("userId") String userId,
        @PathParam("authStr") String authStr, Throwable e)
    {
        LOGGER.error("Websocket connection error. User: {}", userId, e);
        
        removeClient(userId, authStr);
    }
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId,
        @PathParam("authStr") String authStr)
    {
        LOGGER.info("Websocket connected, user id: " + userId);
        
        String userKey = USER_SESSOIN_KEY_PREFIX + userId;
        String sessionKey = userId + '_' + authStr;
        
        synchronized (MessageListener.class)
        {
            Map<String, Session> sessionMap = USER_CLIENT_SESSION_INFO.get(userKey);
            if (sessionMap == null)
            {
                sessionMap = new HashMap<String, Session>(BusinessConstants.INITIAL_CAPACITIES);
            }
            sessionMap.put(sessionKey, session);
            USER_CLIENT_SESSION_INFO.put(userKey, sessionMap);
        }
    }
    
    private void removeClient(String userId, String authStr)
    {
        String userKey = USER_SESSOIN_KEY_PREFIX + userId;
        String clientKey = userId + '_' + authStr;
        Map<String, Session> clientSessionMap = USER_CLIENT_SESSION_INFO.get(userKey);
        if(clientSessionMap == null || clientSessionMap.isEmpty())
        {
            return;
        }
        
        synchronized (MessageListener.class)
        {
            clientSessionMap.remove(clientKey);
            
            // 如果所有客户端都已断开连接, 移除用户终端缓存
            if (clientSessionMap.isEmpty())
            {
                USER_CLIENT_SESSION_INFO.remove(userKey);
            }
        }
    }
    
}
