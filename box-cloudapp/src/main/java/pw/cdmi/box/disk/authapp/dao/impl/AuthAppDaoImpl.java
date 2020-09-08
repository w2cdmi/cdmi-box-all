/**
 * 
 */
package pw.cdmi.box.disk.authapp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.authapp.dao.AuthAppDao;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.uam.domain.AuthApp;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AuthAppDaoImpl extends AbstractDAOImpl implements AuthAppDao
{
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getByAuthAppID(java.lang.String
     * )
     */
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        AuthApp authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getByAuthAppID", authAppId);
        if (authApp != null)
        {
            authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                authApp.getUfmSecretKeyEncodeKey()));
            authApp.setUfmSecretKeyEncodeKey(null);
        }
        return authApp;
    }
    
    @Override
    public AuthApp getByAuthAppName(String name)
    {
        AuthApp authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getByAuthAppName", name);
        if (authApp != null)
        {
            authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                authApp.getUfmSecretKeyEncodeKey()));
            authApp.setUfmSecretKeyEncodeKey(null);
        }
        return authApp;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterd(com.huawei.sharedrive
     * .isystem.authapp.domain.AuthApp, com.huawei.sharedrive.isystem.core.domain.Order,
     * com.huawei.sharedrive.isystem.core.domain.Limit)
     */
    @Override
    public List<AuthApp> getFilterd(AuthApp filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        List<AuthApp> appLists = sqlMapClientTemplate.queryForList("AuthApp.getFilterd", map);
        for (AuthApp app : appLists)
        {
            app.setUfmSecretKey(EDToolsEnhance.decode(app.getUfmSecretKey(), app.getUfmSecretKeyEncodeKey()));
            app.setUfmSecretKeyEncodeKey(null);
        }
        return appLists;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterdCount(com.huawei
     * .sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public int getFilterdCount(AuthApp filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("AuthApp.getFilterdCount", map);
    }
    
    @Override
    public AuthApp getDefaultWebApp()
    {
        AuthApp authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getDefaultWebApp");
        if (authApp != null)
        {
            authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                authApp.getUfmSecretKeyEncodeKey()));
            authApp.setUfmSecretKeyEncodeKey(null);
        }
        return authApp;
    }
    
}
