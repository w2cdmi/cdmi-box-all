package com.huawei.sharedrive.app.cluster.manage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * 老的心跳文件<br>
 * 为了兼容现网DNS方案，未删除
 * @author s90006125
 *
 */
public class HeartbeatCheckServlet extends HttpServlet
{
    private static final long serialVersionUID = -1833357430613093609L;

    private static final String RESPONSE_OK = "{\"uiFlag\":1,\"uiAbility\":32}";
    
    private static final String RESPONSE_FAILED = "{\"uiFlag\":0,\"uiAbility\":32}";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String responseBody = RESPONSE_OK;
        if(!NetworkStatusCache.isReachable())
        {
            responseBody = RESPONSE_FAILED;
        }
        
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
