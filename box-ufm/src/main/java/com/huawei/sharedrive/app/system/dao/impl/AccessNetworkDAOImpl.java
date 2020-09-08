/**
 * 
 */
package com.huawei.sharedrive.app.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.AccessNetworkDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.AccessNetwork;

/**
 * @author d00199602
 * 
 */
@Service("accessNetworkDAO")
@SuppressWarnings("deprecation")
public class AccessNetworkDAOImpl extends AbstractDAOImpl implements AccessNetworkDAO
{
    
    @Override
    public void create(AccessNetwork accessNetwork)
    {
    }
    
    @Override
    public void delete(String id)
    {
        
    }
    
    @Override
    public AccessNetwork get(String id)
    {
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<AccessNetwork> getAll()
    {
        return (List<AccessNetwork>) sqlMapClientTemplate.queryForList("AccessNetwork.getAll");
    }
    
    @Override
    public void update(AccessNetwork t)
    {
        
    }
}
