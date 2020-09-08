package pw.cdmi.box.uam.authapp.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.authapp.dao.AuthAppDao;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.box.uam.authapp.service.AppAccessKeyService;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.exception.AuthFailedException;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class AuthAppServiceImpl implements AuthAppService
{
    @Autowired
    private AuthAppDao authAppDao;
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private ConfigManager configManager;
    
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
    
    @Override
    public AuthApp getDefaultWebApp()
    {
        return authAppDao.getDefaultWebApp();
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
    public List<AuthApp> getAuthAppList(AuthAppExtend filter, Order order, Limit limit)
    {
        List<AuthApp> appList = authAppDao.getFilterd(filter, order, limit);
        for (AuthApp authApp : appList)
        {
            authApp.setSpaceUsed(0L);
        }
        return appList;
    }
    
    @Override
    public String checkAuthApp(String authorization, String date) throws AuthFailedException
    {
        if (StringUtils.isBlank(authorization))
        {
            throw new AuthFailedException();
        }
        String[] authorizationStr = authorization.split(",");
        if (authorizationStr.length < 3)
        {
            throw new AuthFailedException();
        }
        String appType = authorizationStr[0];
        if (!"app".equals(appType))
        {
            throw new AuthFailedException();
        }
        String sign = authorizationStr[2];
        AppAccessKey key = appAccessKeyService.getById(authorizationStr[1]);
        if (key == null)
        {
            throw new AuthFailedException();
        }
        String selSign = SignatureUtils.getSignature(key.getSecretKey(), date);
        if (!selSign.equals(sign))
        {
            throw new AuthFailedException();
        }
        AuthApp authApp = getByAuthAppID(key.getAppId());
        if (authApp == null)
        {
            throw new AuthFailedException();
        }
        return key.getAppId();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#delete(java.lang.String
     * )
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(String authAppId)
    {
        authAppDao.delete(authAppId);
        appAccessKeyService.deleteByAppId(authAppId);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#create(com.huawei.
     * sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public void create(AuthApp authApp)
    {
        createInNewTransaction(authApp);
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(AuthApp.class.getSimpleName(), authApp.getAuthAppId());
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createInNewTransaction(AuthApp authApp)
    {
        authAppDao.create(authApp);
        appAccessKeyService.createAppAccessKeyForApp(authApp.getAuthAppId());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#updateAuthApp(com.
     * huawei.sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public void updateAuthApp(AuthApp authApp)
    {
        authAppDao.updateAuthApp(authApp);
    }
    
    @Override
    public int getCountByAuthentication(long enterpriseId)
    {
        return authAppDao.getCountByAuthentication(enterpriseId);
    }
    
    @Override
    public List<AuthApp> getByAuthentication(long enterpriseId, Limit limit)
    {
        return authAppDao.getByAuthentication(enterpriseId, limit);
    }
    
    @Override
    public void updateNetworkRegionStatus(AuthApp authApp)
    {
        authApp.setModifiedAt(new Date());
        authAppDao.updateNetworkRegionStatus(authApp);
    }
    
    @Override
    public int getAuthAppNum()
    {
        return authAppDao.getAuthAppNum();
    }
    
}
