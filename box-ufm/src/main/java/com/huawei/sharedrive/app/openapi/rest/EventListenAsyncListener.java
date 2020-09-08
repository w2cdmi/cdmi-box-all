/**
 * 
 */
package com.huawei.sharedrive.app.openapi.rest;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.user.service.UserSyncVersionService;

/**
 * @author q90003805
 * 
 */
public class EventListenAsyncListener implements AsyncListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenAsyncListener.class);
    
    private UserSyncVersionService userSyncVersionService;
    
    public EventListenAsyncListener(UserSyncVersionService userSyncVersionService)
    {
        this.userSyncVersionService = userSyncVersionService;
    }
    
    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException
    {
        LOGGER.debug("AppAsyncListener onComplete");
    }
    
    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException
    {
        LOGGER.warn("AppAsyncListener onError");
        Long userId = (Long) asyncEvent.getSuppliedRequest()
            .getAttribute(EventListenHttpServletRequestHandler.ATTR_CLIENT_USER_ID);
        String clientId = (String) asyncEvent.getSuppliedRequest()
            .getAttribute(EventListenHttpServletRequestHandler.ATTR_CLIENT_UUID);
        ((HttpServletResponse) asyncEvent.getSuppliedResponse()).setStatus(HttpServletResponse.SC_NO_CONTENT);
        userSyncVersionService.completeContext(userId, clientId);
    }
    
    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException
    {
        LOGGER.debug("AppAsyncListener onStartAsync");
    }
    
    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException
    {
        LOGGER.debug("AppAsyncListener onTimeout");
        Long userId = (Long) asyncEvent.getSuppliedRequest()
            .getAttribute(EventListenHttpServletRequestHandler.ATTR_CLIENT_USER_ID);
        String clientId = (String) asyncEvent.getSuppliedRequest()
            .getAttribute(EventListenHttpServletRequestHandler.ATTR_CLIENT_UUID);
        ((HttpServletResponse) asyncEvent.getSuppliedResponse()).setStatus(HttpServletResponse.SC_NO_CONTENT);
        userSyncVersionService.completeContext(userId, clientId);
        
    }
}
