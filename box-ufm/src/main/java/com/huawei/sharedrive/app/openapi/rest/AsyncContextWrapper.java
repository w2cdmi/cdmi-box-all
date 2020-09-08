/**
 * 
 */
package com.huawei.sharedrive.app.openapi.rest;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author q90003805
 * 
 */
public class AsyncContextWrapper implements AsyncContext
{
    private AsyncContext asyncContext;
    
    private String clientId;
    
    private boolean completed;
    
    private long syncVersion;
    
    private long userId;
    
    public AsyncContextWrapper(AsyncContext asyncContext, String clientId, long userId, long syncVersion)
    {
        this.asyncContext = asyncContext;
        this.clientId = clientId;
        this.userId = userId;
        this.syncVersion = syncVersion;
    }
    
    @Override
    public void addListener(AsyncListener listener)
    {
        asyncContext.addListener(listener);
    }
    
    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest,
        ServletResponse servletResponse)
    {
        asyncContext.addListener(listener, servletRequest, servletResponse);
    }
    
    @Override
    public void complete()
    {
        asyncContext.complete();
        completed = true;
    }
    
    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException
    {
        return asyncContext.createListener(clazz);
    }
    
    @Override
    public void dispatch()
    {
        asyncContext.dispatch();
    }
    
    @Override
    public void dispatch(ServletContext context, String path)
    {
        asyncContext.dispatch(context, path);
    }
    
    @Override
    public void dispatch(String path)
    {
        asyncContext.dispatch(path);
    }
    
    public String getClientId()
    {
        return clientId;
    }
    
    @Override
    public ServletRequest getRequest()
    {
        return asyncContext.getRequest();
    }
    
    @Override
    public ServletResponse getResponse()
    {
        return asyncContext.getResponse();
    }
    
    public long getSyncVersion()
    {
        return syncVersion;
    }
    
    @Override
    public long getTimeout()
    {
        return asyncContext.getTimeout();
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    @Override
    public boolean hasOriginalRequestAndResponse()
    {
        return asyncContext.hasOriginalRequestAndResponse();
    }
    
    public boolean isCompleted()
    {
        return completed;
    }
    
    @Override
    public void setTimeout(long timeout)
    {
        asyncContext.setTimeout(timeout);
    }
    
    @Override
    public void start(Runnable run)
    {
        asyncContext.start(run);
    }
    
}
