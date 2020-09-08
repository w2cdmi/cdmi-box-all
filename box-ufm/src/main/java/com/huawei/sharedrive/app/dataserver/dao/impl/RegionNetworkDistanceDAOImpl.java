package com.huawei.sharedrive.app.dataserver.dao.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.dao.RegionNetworkDistanceDAO;
import com.huawei.sharedrive.app.dataserver.domain.RegionNetworkDistance;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("regionNetworkDistanceDAO")
public class RegionNetworkDistanceDAOImpl  extends AbstractDAOImpl implements RegionNetworkDistanceDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(RegionNetworkDistance regionNetworkDistance)
    {
       sqlMapClientTemplate.insert("RegionNetworkDistance.insert", regionNetworkDistance);
       
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(RegionNetworkDistance regionNetworkDistance)
    {
        sqlMapClientTemplate.update("RegionNetworkDistance.update", regionNetworkDistance);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void delete(RegionNetworkDistance regionNetworkDistance)
    {
        sqlMapClientTemplate.delete("RegionNetworkDistance.delete", regionNetworkDistance);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<RegionNetworkDistance> getByRegion(RegionNetworkDistance regionNetworkDistance)
    {
        return sqlMapClientTemplate.queryForList("RegionNetworkDistance.getByRegion", regionNetworkDistance);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public RegionNetworkDistance get(String name)
    {
        return (RegionNetworkDistance) sqlMapClientTemplate.queryForObject("RegionNetworkDistance.get", name);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<RegionNetworkDistance> lstRegionNetworkDistance()
    {      
        return sqlMapClientTemplate.queryForList("RegionNetworkDistance.getAll");
    }
    
}
