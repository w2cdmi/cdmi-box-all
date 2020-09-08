/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.authapp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.authapp.dao.AuthAppNetRegionIpDao;
import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIp;


@Service("authAppNetRegionIpDao")
public class AuthAppNetRegionIpDaoImpl extends AbstractDAOImpl implements AuthAppNetRegionIpDao
{
    @SuppressWarnings("deprecation")
    @Override
    public AuthAppNetRegionIp queryByConfigId(long id)
    {
        Object obj = sqlMapClientTemplate.queryForObject("AuthAppNetRegionIp.queryByConfigId", id);
        if (null != obj)
        {
            return (AuthAppNetRegionIp) obj;
        }
        
        return null;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public AuthAppNetRegionIp queryByIp(String appId, long ip)
    {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("appId", appId);
        parameters.put("ipValue", ip);
        
        Object obj = sqlMapClientTemplate.queryForObject("AuthAppNetRegionIp.queryByIp", parameters);
        if (null != obj)
        {
            return (AuthAppNetRegionIp) obj;
        }
        
        return null;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int queryTotalCountByAuthAppId(String appId)
    {
        Object obj = sqlMapClientTemplate.queryForObject("AuthAppNetRegionIp.queryTotalCountByAuthAppId",
            appId);
        if (null == obj)
        {
            return 0;
        }
        return (int) obj;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void deleteById(long configId)
    {
        sqlMapClientTemplate.delete("AuthAppNetRegionIp.deleteById", configId);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void deleteByAuthAppId(String authAppId)
    {
        sqlMapClientTemplate.delete("AuthAppNetRegionIp.deleteByAuthAppId", authAppId);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getCount(String appId, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("regionId", regionId);
        return (Integer) sqlMapClientTemplate.queryForObject("AuthAppNetRegionIp.getCount", map);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<AuthAppNetRegionIp> listAllNetworkRegion(String appId)
    {
        return sqlMapClientTemplate.queryForList("AuthAppNetRegionIp.listAllNetworkRegion", appId);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<AuthAppNetRegionIp> listNetworkRegion(String appId, Integer regionId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("regionId", regionId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AuthAppNetRegionIp.listNetworkRegion", map);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxId()
    {
        Object obj = sqlMapClientTemplate.queryForObject("AuthAppNetRegionIp.getMaxId");
        if (null == obj)
        {
            return 0;
        }
        return (Long) obj;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(AuthAppNetRegionIp authAppNetRegionIp)
    {
        sqlMapClientTemplate.update("AuthAppNetRegionIp.insert", authAppNetRegionIp);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(AuthAppNetRegionIp authAppNetRegionIp)
    {
        sqlMapClientTemplate.update("AuthAppNetRegionIp.update", authAppNetRegionIp);
    }
}
