package com.huawei.sharedrive.app.authapp.service.impl;

import com.huawei.sharedrive.app.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("authAppCache")
public class AuthAppCache implements ConfigListener
{
    
    public static final String ZK_ACCESSKEY_DELETE = "zk_accesskey_delete";
    
    public static final String ZK_APP_ACCESSKEY_DELETE = "zk_app_accesskey_delete";
    
    public static final String ZK_APP_UPDATE = "zk_app_change";
    
    private static final Map<String, AuthApp> AUTHAPPMAP = new HashMap<String, AuthApp>(2);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAppCache.class);
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private AuthAppService authAppService;

    public static AuthApp getAuthApp(String appId) {
        return AUTHAPPMAP.get(appId);
    }

    public static void setAuthApp(String appId, AuthApp app) {
        AUTHAPPMAP.put(appId, app);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (ZK_APP_UPDATE.equalsIgnoreCase(key))
        {
            LOGGER.info("[appLog] receive notice to update authApp" + key);
            fillCache();
        }
        else if (ZK_ACCESSKEY_DELETE.equalsIgnoreCase(key))
        {
            LOGGER.info("[appLog] receive notice to delete accesskey: {}", key);
            String id = (String) value;
            appAccessKeyService.deleteCache(id);
        }
        else if (ZK_APP_ACCESSKEY_DELETE.equals(key))
        {
            LOGGER.info("[appLog] receive notice to delete app all accesskey: {}", key);
            String appId = (String) value;
            appAccessKeyService.deleteCacheByAppId(appId);
        }
        else 
        {
            LOGGER.debug("Unsupprt message: {}", key);
        }
    }
    
    @PostConstruct
    public void init() throws BaseRunException
    {
        fillCache();
    }
    
    private void fillCache()
    {
        List<AuthApp> authAppList = authAppService.getAuthAppList(null, null, null);
        if (null == authAppList)
        {
            return;
        }
        for (AuthApp authApp : authAppList)
        {
            AUTHAPPMAP.put(authApp.getAuthAppId(), authApp);
        }
    }
    
}
