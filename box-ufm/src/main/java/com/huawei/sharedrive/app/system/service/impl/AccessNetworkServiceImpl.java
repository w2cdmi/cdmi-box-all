/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.AccessNetworkDAO;
import com.huawei.sharedrive.app.system.service.AccessNetworkService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.AccessNetwork;

/**
 * @author d00199602
 * 
 */
@Component
public class AccessNetworkServiceImpl implements AccessNetworkService, ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(AccessNetworkServiceImpl.class);
    
    @Autowired
    private AccessNetworkDAO accessNetworkDAO;
    
    private List<AccessNetwork> localCache;
    
    private static final Object LOCK = new Object();
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(AccessNetwork.class.getSimpleName()))
        {
            synchronized(LOCK)
            {
                logger.info("Reload AccessNetwork By Cluster Notify.");
                localCache = (List)value;
                StringBuilder sb = new StringBuilder("[ ");
                if(null != localCache)
                {
                    for(AccessNetwork a : localCache)
                    {
                        sb.append(a.getAccessIp()).append(", ");
                    }
                }
                sb.append(" ]");
                logger.info(sb.toString());
            }
        }
    }
    
    @Override
    public List<AccessNetwork> getAll()
    {
        if (localCache == null)
        {
            List<AccessNetwork> tmpAccessNetworkList = accessNetworkDAO.getAll();
            if (tmpAccessNetworkList != null)
            {
                localCache = tmpAccessNetworkList;
            }
        }
        return localCache;
    }
    
}
