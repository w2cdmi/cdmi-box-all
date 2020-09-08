package com.huawei.sharedrive.uam.system.dao;

import java.util.List;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

public interface SystemConfigDAO extends BaseDAO<SystemConfig, String>
{
    /**
     * 
     * @param appId
     * @param id
     * @return
     */
    SystemConfig getByPriKey(String appId, String id);
    
    /**
     * @param appId
     * @param limit
     * @param prefix
     * @return
     */
    List<SystemConfig> getByPrefix(String appId, Limit limit, String prefix);
    
    /**
     * @param appId
     * @param prefix
     * @return
     */
    int getByPrefixCount(String appId, String prefix);
    
    /**
     * @param appId
     * @param id
     */
    void deleteByPriKey(String appId, String id);
    
}