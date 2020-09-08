/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.core.heartbeat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class HeartbeatCheckServlet extends HttpServlet
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatCheckServlet.class);
    
    private static final long serialVersionUID = -1833357430613093609L;

    private static final String RESPONSE_OK = "{\"uiFlag\":1,\"uiAbility\":32}";
    
    private static final String RESPONSE_FAILED = "{\"uiFlag\":0,\"uiAbility\":32}";
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String responseBody = RESPONSE_OK;
        if(!checkStorageStatus())
        {
            responseBody = RESPONSE_FAILED;
        }
        
        PrintWriter out = null;
        try
        {
            out = response.getWriter();
            // {“uiFlag”:1,“uiAbility”:32}
            out.write(responseBody);
            out.flush();
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }
    
    private boolean checkStorageStatus()
    {
        List<FSEndpoint> endpoints = FSEndpointCache.getAllEndpoints();
        
        if (!endpoints.isEmpty())
        {
            for(FSEndpoint endpoint : endpoints)
            {
                if(endpoint.isAvailable()
                    && endpoint.isWriteAble())
                {
                    // 只要该节点有任意一个存储是可用的，则该节点存储可用，其他存储就算不可用，也是通过告警告知用户
                    return true;
                }
            }
        }
        
        LOGGER.warn("no available storage.");
        return false;
    }
}
