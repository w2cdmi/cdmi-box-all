/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.system.service.AccessWhiteListService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;

/**
 * @author d00199602
 * 
 */
public class AccessWhiteListServiceImpl implements AccessWhiteListService, ConfigListener
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private List<String> localCache;
    
    @Override
    public List<String> getWhiteList()
    {
        if(null != localCache)
        {
            return localCache;
        }
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, "whiteList.list");
        if (CollectionUtils.isEmpty(itemList))
        {
            List<String> whiteList = new ArrayList<String>(10);
            for (SystemConfig sysConfig : itemList)
            {
                whiteList.add(sysConfig.getValue());
            }
            localCache = whiteList;
        }
        return localCache;
    }
    
    @Override
    public void configChanged(String arg0, Object arg1)
    {
    }
    
}
