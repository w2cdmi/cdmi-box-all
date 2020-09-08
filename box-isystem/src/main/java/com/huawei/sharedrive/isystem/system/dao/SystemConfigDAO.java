/**
 * 
 */
package com.huawei.sharedrive.isystem.system.dao;

import java.util.List;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

/**
 * @author q90003805
 * 
 */
public interface SystemConfigDAO extends BaseDAO<SystemConfig, String>
{
    
    /**
     * 通过id前缀模糊匹配查找配置项
     * 
     * @param prefix
     * @return
     */
    List<SystemConfig> getByPrefix(Limit limit, String prefix);
    
    /**
     * 通过id前缀模糊匹配查找配置项数目
     * 
     * @param prefix
     * @return
     */
    int getByPrefixCount(String prefix);
    
}