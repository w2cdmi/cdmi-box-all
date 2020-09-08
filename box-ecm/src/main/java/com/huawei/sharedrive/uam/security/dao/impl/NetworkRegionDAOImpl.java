package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.NetworkRegionDAO;
import com.huawei.sharedrive.uam.security.domain.NetworkRegion;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("NetworkRegionDAO")
@SuppressWarnings("deprecation")
public class NetworkRegionDAOImpl extends AbstractDAOImpl implements NetworkRegionDAO
{
    
    @Override
    public void insert(NetworkRegion networkRegion)
    {
        networkRegion.setId(getNextAvailableNetworkRegionId());
        sqlMapClientTemplate.insert("NetworkRegion.insert", networkRegion);
    }
    
    public long getNextAvailableNetworkRegionId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "networkRegionId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
    
    @Override
    public void delete(long id)
    {
        sqlMapClientTemplate.delete("NetworkRegion.delete", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NetworkRegion> query(NetworkRegion networkRegion, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (order == null)
        {
            order = new Order();
            order.setDesc(true);
            order.setField("ipStartValue");
        }
        map.put("networkRegion", networkRegion);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("NetworkRegion.query", map);
    }
    
    @Override
    public int queryCount(NetworkRegion networkRegion)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("networkRegion", networkRegion);
        return (Integer) sqlMapClientTemplate.queryForObject("NetworkRegion.queryCount", map);
    }
    
    @Override
    public int uniquelyCheck(NetworkRegion networkRegion)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("networkRegion", networkRegion);
        return (Integer) sqlMapClientTemplate.queryForObject("NetworkRegion.uniquelyCheck", map);
    }
    
    @Override
    public void update(NetworkRegion networkRegion)
    {
        sqlMapClientTemplate.update("NetworkRegion.update", networkRegion);
    }
    
    @Override
    public NetworkRegion getById(Long id)
    {
        return (NetworkRegion) sqlMapClientTemplate.queryForObject("NetworkRegion.getById", id);
    }
    
    @Override
    public NetworkRegion getByIpValue(Long ipValue)
    {
        return (NetworkRegion) sqlMapClientTemplate.queryForObject("NetworkRegion.getByIpValue", ipValue);
    }
}
