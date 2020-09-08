package com.huawei.sharedrive.isystem.cluster.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.dao.RegionDao;
import com.huawei.sharedrive.isystem.cluster.domain.Region;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("RegionDAO")
@SuppressWarnings("deprecation")
public class RegionDAOImpl extends AbstractDAOImpl implements RegionDao
{
    
    @Override
    public Region get(Integer regionID)
    {
        return (Region) sqlMapClientTemplate.queryForObject("Region.get", regionID);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Region> getAll()
    {
        return sqlMapClientTemplate.queryForList("Region.getAll");
    }
    
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.delete("Region.delete", id);
    }
    
    @Override
    public void create(Region region)
    {
        sqlMapClientTemplate.insert("Region.insert", region);
    }
    
    @Override
    public void update(Region region)
    {
        sqlMapClientTemplate.update("Region.update", region);
    }
    
    @Override
    public Region getDefaultRegion()
    {
        return (Region) sqlMapClientTemplate.queryForObject("Region.getDefault");
    }
    
    @Override
    public int getNextAvailableRegionId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "regionId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }

    @Override
    public Region findByCode(String code) {
        return (Region) sqlMapClientTemplate.queryForObject("Region.findBycode", code);
    }
}
