package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.NetRegionDao;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class NetRegionDaoImpl extends CacheableSqlMapClientDAO implements NetRegionDao
{
    @Autowired
    private NetRegionGenerateService netRegionGenerateService;
    
    @Override
    public long create(NetRegion netRegion)
    {
        long id = netRegionGenerateService.getNextId();
        netRegion.setId(id);
        sqlMapClientTemplate.insert("NetRegion.insert", netRegion);
        return id;
    }
    
    @Override
    public boolean isDuplicateValues(NetRegion netRegion)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("NetRegion.getDuplicateValues", netRegion);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int getFilterdCount(NetRegion filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("NetRegion.getFilterdCount", map);
    }
    
    @Override
    public List<NetRegion> getFilterd(NetRegion filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("NetRegion.getFilterd", map);
    }
    
    @Override
    public NetRegion getById(long id)
    {
        return (NetRegion) sqlMapClientTemplate.queryForObject("NetRegion.getById", id);
    }
    
    @Override
    public void updateEnterpriseInfo(NetRegion netRegion)
    {
        sqlMapClientTemplate.update("NetRegion.modify", netRegion);
    }
    
    @Override
    public long getByDomainExclusiveId(NetRegion netRegion)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("NetRegion.getByDomainExclusiveId", netRegion);
        return count;
    }
    
    @Override
    public long getMaxId()
    {
        long selMaxUserId;
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("NetRegion.getMaxId", null);
        selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
        return selMaxUserId;
    }
    
    @Override
    public void deleteNetRegion(NetRegion netRegion)
    {
        sqlMapClientTemplate.delete("NetRegion.delete", netRegion);
    }
}
