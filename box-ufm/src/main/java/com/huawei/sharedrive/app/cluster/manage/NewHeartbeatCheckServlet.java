package com.huawei.sharedrive.app.cluster.manage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * 新的心跳<br>
 * 失败后返回500
 * @author s90006125
 *
 */
public class NewHeartbeatCheckServlet extends HttpServlet
{
    private static final long serialVersionUID = -1833357430613093609L;

    private static final String RESPONST_CONTENT = "check realserver health";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException
    {
        if (NetworkStatusCache.isReachable())
        {
            PrintWriter out = null;
            try
            {
                out = response.getWriter();
                out.write(RESPONST_CONTENT);
                out.flush();
            }
            finally
            {
                IOUtils.closeQuietly(out);
            }
        }
        else
        {
            response.sendError(500, "Server Failed.");
        }
    }
}
