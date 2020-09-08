/**
 * 
 */
package pw.cdmi.box.disk.authapp.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.authapp.dao.AuthAppDao;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class AuthAppServiceImpl implements AuthAppService, ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAppServiceImpl.class);
    
    @Autowired
    private AuthAppDao authAppDao;
    
    private String cachedAppId;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getByAuthAppID(java
     * .lang.String)
     */
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        return authAppDao.getByAuthAppID(authAppId);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getAuthAppList(com
     * .huawei.sharedrive.isystem.authapp.domain.AuthApp,
     * com.huawei.sharedrive.isystem.core.domain.Order,
     * com.huawei.sharedrive.isystem.core.domain.Limit)
     */
    @Override
    public List<AuthApp> getAuthAppList(AuthApp filter, Order order, Limit limit)
    {
        return authAppDao.getFilterd(filter, order, limit);
    }
    
    @Override
    public AuthApp getByAuthAppName(String name)
    {
        return authAppDao.getByAuthAppName(name);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(AuthApp.class.getSimpleName()))
        {
            LOGGER.info("Create Default Web App By Cluseter Notify.");
            cachedAppId = (String) value;
        }
    }
    
    @Override
    public String getCurrentAppId()
    {
        if (StringUtils.isEmpty(cachedAppId))
        {
            AuthApp authApp = authAppDao.getDefaultWebApp();
            if (authApp == null)
            {
                LOGGER.error("app not config,please config ECM at first!");
            }
            else
            {
                cachedAppId = authApp.getAuthAppId();
            }
        }
        return cachedAppId;
    }
}
