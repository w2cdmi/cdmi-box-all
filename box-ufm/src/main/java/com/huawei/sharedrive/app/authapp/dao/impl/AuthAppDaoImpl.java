/**
 * 
 */
package com.huawei.sharedrive.app.authapp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.app.core.domain.OrderV1;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
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
    public List<AuthApp> getFilterd(AuthApp filter, OrderV1 order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        List<AuthApp> appLists = sqlMapClientTemplate.queryForList("AuthApp.getFilterd", map);
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
    
}