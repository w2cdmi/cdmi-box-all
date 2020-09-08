package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.NetRegionIpDao;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class NetRegionIpDaoImpl extends CacheableSqlMapClientDAO implements NetRegionIpDao
{
    
    @Autowired
    private NetRegionGenerateService netRegionGenerateService;
    
    @Override
    public long create(NetRegionIp netRegion)
    {
        long id = netRegionGenerateService.getNextId();
        netRegion.setId(id);
        sqlMapClientTemplate.insert("NetRegionIp.insert", netRegion);
        return id;
    }
    
    @Override
    public boolean isDuplicateValues(NetRegionIp netRegion)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("NetRegionIp.getDuplicateValues", netRegion);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int getFilterdCount(NetRegionIp filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("NetRegionIp.getFilterdCount", map);
    }
    
    @Override
    public List<NetRegionIp> getFilterd(NetRegionIp filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("NetRegionIp.getFilterd", map);
    }
    
    @Override
    public NetRegionIp getById(long id)
    {
        return (NetRegionIp) sqlMapClientTemplate.queryForObject("NetRegionIp.getById", id);
    }
    
    @Override
    public void updateEnterpriseInfo(NetRegionIp netRegion)
    {
        sqlMapClientTemplate.update("NetRegionIp.modify", netRegion);
    }
    
    @Override
    public long getByDomainExclusiveId(NetRegionIp netRegion)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("NetRegionIp.getByDomainExclusiveId",
            netRegion);
        return count;
    }
    
    @Override
    public void delete(long id)
    {
        sqlMapClientTemplate.delete("NetRegionIp.delete", id);
    }
    
    @Override
    public List<NetRegionIp> getListRegionIp(NetRegionIp netRegionIp)
    {
        return sqlMapClientTemplate.queryForList("NetRegionIp.getListRegionIp", netRegionIp);
        
    }
    
    @Override
    public long getMaxId()
    {
        long selMaxUserId;
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("NetRegionIp.getMaxId", null);
        selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
        return selMaxUserId;
    }
    
    @Override
    public NetRegionIp getByIp(long accountId, long ipLong)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("accountId", accountId);
        map.put("ipLong", ipLong);
        Object obj = sqlMapClientTemplate.queryForObject("NetRegionIp.getByIp", map);
        if (null == obj)
        {
            return null;
        }
        else
        {
            return (NetRegionIp) obj;
        }
    }
    
    @Override
    public void deleteByCondition(NetRegionIp netRegionIp)
    {
        sqlMapClientTemplate.delete("NetRegionIp.deleteByCondition", netRegionIp);
    }
}
