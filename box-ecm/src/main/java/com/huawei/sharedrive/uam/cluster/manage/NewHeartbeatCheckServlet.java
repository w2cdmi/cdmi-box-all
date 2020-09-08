package com.huawei.sharedrive.uam.cluster.manage;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewHeartbeatCheckServlet extends HeartbeatCheckServlet
{
    private static final long serialVersionUID = -1833357430613093609L;
    
    private static final String RESPONST_CONTENT = "check realserver health";
    
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    
    {
        if (NetworkStatusCache.isReachable())
        {
            responseOut(response, RESPONST_CONTENT);
        }
        else
        {
            response.sendError(500, "Server Failed.");
        }
    }
}
