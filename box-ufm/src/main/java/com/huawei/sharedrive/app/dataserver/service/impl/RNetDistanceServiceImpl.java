package com.huawei.sharedrive.app.dataserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.dao.RegionNetworkDistanceDAO;
import com.huawei.sharedrive.app.dataserver.domain.RegionNetworkDistance;
import com.huawei.sharedrive.app.dataserver.service.RNetDistanceService;

@Service("rNetDistanceService")
public class RNetDistanceServiceImpl implements RNetDistanceService
{
    @Autowired
    private RegionNetworkDistanceDAO regionNetworkDistanceDAO;
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void create(RegionNetworkDistance regionNetworkDistance)
    {
        regionNetworkDistanceDAO.create(regionNetworkDistance);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void update(RegionNetworkDistance regionNetworkDistance)
    {
        regionNetworkDistanceDAO.update(regionNetworkDistance); 
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void delete(RegionNetworkDistance regionNetworkDistance)
    {
        regionNetworkDistanceDAO.delete(regionNetworkDistance);
    }
    
    @Override
    public List<RegionNetworkDistance> get(RegionNetworkDistance regionNetworkDistance)
    {
           return regionNetworkDistanceDAO.getByRegion(regionNetworkDistance);
    }
    
    @Override
    public RegionNetworkDistance get(String name)
    {
        return regionNetworkDistanceDAO.get(name);
    }
    
    @Override
    public List<RegionNetworkDistance> lstRegionNetworkDistance()
    {
        return regionNetworkDistanceDAO.lstRegionNetworkDistance();
    }
    
}
