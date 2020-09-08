package com.huawei.sharedrive.uam.cluster.manage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatCheckServlet extends HttpServlet
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatCheckServlet.class);
    
    private static final long serialVersionUID = -1833357430613093609L;
    
    private static final String RESPONSE_OK = "{\"uiFlag\":1,\"uiAbility\":32}";
    
    private static final String RESPONSE_FAILED = "{\"uiFlag\":0,\"uiAbility\":32}";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String responseBody = RESPONSE_OK;
        if (!NetworkStatusCache.isReachable())
        {
            responseBody = RESPONSE_FAILED;
        }
        
        try
        {
            responseOut(response, responseBody);
        }
        catch (IOException e)
        {
            LOGGER.error("IOException");
        }
    }
    
    protected void responseOut(HttpServletResponse response, String responseBody) throws IOException
    {
        PrintWriter out = null;
        
        try
        {
            out = response.getWriter();
            out.write(responseBody);
            out.flush();
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }
}
