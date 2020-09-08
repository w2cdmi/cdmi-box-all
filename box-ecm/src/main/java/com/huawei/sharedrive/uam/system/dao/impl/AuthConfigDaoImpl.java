package com.huawei.sharedrive.uam.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.system.dao.AuthConfigDao;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AuthServer;

@Service
public class AuthConfigDaoImpl implements AuthConfigDao
{
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.uam.system.dao.AuthConfigDao#get(java.lang.Long)
     */
    @Override
    public AuthServer get(Long id)
    {
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.dao.AuthConfigDao#getFilterd(com.huawei.sharedrive
     * .uam.system.domain.AuthConfig, com.huawei.sharedrive.uam.core.domain.Order,
     * com.huawei.sharedrive.uam.core.domain.Limit)
     */
    @Override
    public List<AuthServer> getFilterd(AuthServer filter, Order order, Limit limit)
    {
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.uam.system.dao.AuthConfigDao#getFilterdCount(com.huawei.
     * sharedrive.uam.system.domain.AuthConfig)
     */
    @Override
    public int getFilterdCount(AuthServer filter)
    {
        return 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.uam.system.dao.AuthConfigDao#delete(java.lang.Long)
     */
    @Override
    public void delete(Long id)
    {
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.dao.AuthConfigDao#create(com.huawei.sharedrive
     * .uam.system.domain.AuthConfig)
     */
    @Override
    public void create(AuthServer authConfig)
    {
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.dao.AuthConfigDao#update(com.huawei.sharedrive
     * .uam.system.domain.AuthConfig)
     */
    @Override
    public void update(AuthServer authConfig)
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.uam.system.dao.AuthConfigDao#getNextAvailableId()
     */
    @Override
    public long getNextAvailableId()
    {
        return 0;
    }
    
    @Override
    public AuthServer getDefaultAuthServer()
    {
        return null;
    }
    
}
