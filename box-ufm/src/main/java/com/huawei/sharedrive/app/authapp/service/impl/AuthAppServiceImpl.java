/**
 * 
 */
package com.huawei.sharedrive.app.authapp.service.impl;

import com.huawei.sharedrive.app.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.app.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.AuthFailedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.uam.domain.AuthApp;

import java.util.List;

/**
 * @author d00199602
 * 
 */
@Service
public class AuthAppServiceImpl implements AuthAppService
{
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private AuthAppDao authAppDao;
    
    @Override
    public void checkAuthApp(String authorization, String date) throws AuthFailedException
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
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getAuthAppList
     * (com .huawei.sharedrive.isystem.authapp.domain.AuthApp,
     * com.huawei.sharedrive.isystem.core.domain.Order,
     * com.huawei.sharedrive.isystem.core.domain.Limit)
     */
    @Override
    public List<AuthApp> getAuthAppList(AuthApp filter, OrderV1 order, Limit limit)
    {
        return authAppDao.getFilterd(filter, order, limit);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getByAuthAppID
     * (java .lang.String)
     */
    @Override
    public AuthApp getByAuthAppID(String authAppId) {
        AuthApp authApp = AuthAppCache.getAuthApp(authAppId);
        if (authApp == null) {
            authApp = authAppDao.getByAuthAppID(authAppId);

            if (authApp != null) {
                AuthAppCache.setAuthApp(authAppId, authApp);
            }
        }

        return authApp;
    }
}
