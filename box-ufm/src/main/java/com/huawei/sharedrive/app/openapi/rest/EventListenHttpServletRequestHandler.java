/**
 * 
 */
package com.huawei.sharedrive.app.openapi.rest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.DeclarationException;
import com.huawei.sharedrive.app.exception.InvalidSpaceStatusException;
import com.huawei.sharedrive.app.exception.PasswordInitException;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.sync.SyncFolderRequest;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;

/**
 * @author q90003805
 * 
 */
@Component("eventListeServletHandler")
public class EventListenHttpServletRequestHandler implements HttpRequestHandler
{
    public static final String ATTR_CLIENT_USER_ID = "event-listen-client-userId";
    
    public static final String ATTR_CLIENT_UUID = "event-listen-client-id";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenHttpServletRequestHandler.class);
    
    @Value("${listen.polling.timeout}")
    private int pollingTimeout;
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if ("GET".equals(request.getMethod()))
        {
            SyncFolderRequest syncFolderRequest = new SyncFolderRequest();
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            if (!parseToken(request, response, syncFolderRequest, headerCustomMap))
            {
                return;
            }
            if (!parseSyncVersion(request, response, syncFolderRequest))
            {
                return;
            }
            long userId = syncFolderRequest.getUserId();
            long version = syncFolderRequest.getClientSyncVersion();
            
            LOGGER.info("handleRequest begain,userid: " + userId + ",search version:" + version);
            
            long currentVersion = version;
            try
            {
                currentVersion = userSyncVersionService.getUserCurrentSyncVersion(userId);
            }
            catch (Exception e)
            {
                LOGGER.error("error occur when get user current sync version ", e);
            }
            if (currentVersion > version)
            {
                response.getWriter().println(currentVersion);
                LOGGER.info("handleRequest end,userid: " + userId + ",search version:" + currentVersion);
            }
            else
            {
                request.setAttribute(ATTR_CLIENT_USER_ID, syncFolderRequest.getUserId());
                String clientId = generateClientId();
                request.setAttribute(ATTR_CLIENT_UUID, clientId);
                AsyncContext asyncCtx = request.startAsync();
                asyncCtx.addListener(new EventListenAsyncListener(userSyncVersionService));
                asyncCtx.setTimeout(pollingTimeout);
                AsyncContextWrapper wrapper = new AsyncContextWrapper(asyncCtx, clientId,
                    syncFolderRequest.getUserId(), syncFolderRequest.getClientSyncVersion());
                userSyncVersionService.registContext(wrapper);
            }
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    
    private String generateClientId()
    {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
    
    private boolean parseSyncVersion(HttpServletRequest request, HttpServletResponse response,
        SyncFolderRequest syncFolderRequest)
    {
        String sycnVersion = request.getParameter("syncVersion");
        if (StringUtils.isBlank(sycnVersion))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        try
        {
            long clientSyncVersion = Long.parseLong(sycnVersion);
            syncFolderRequest.setClientSyncVersion(clientSyncVersion);
        }
        catch (NumberFormatException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }
    
    private boolean parseToken(HttpServletRequest request, HttpServletResponse response,
        SyncFolderRequest syncFolderRequest, Map<String, String> headerCustomMap) throws IOException
    {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token))
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        User userInfo = null;
        try
        {
            userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            // 用户状态校验
            userTokenHelper.checkUserStatus(userInfo.getAppId(), userInfo.getId());
            
            syncFolderRequest.setUserId(userInfo.getId());
            return true;
        }
        catch (InvalidSpaceStatusException e)
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        catch (DeclarationException e)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"code\"" + ":\"" + e.getCode() + "\",\"message\":\"" + e.getMsg()
                + "\"}");
            return false;
        }
        catch (PasswordInitException e)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"code\"" + ":\"" + e.getCode() + "\",\"message\":\"" + e.getMsg()
                + "\"}");
            return false;
        }
        catch (BaseRunException e)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
    
}
