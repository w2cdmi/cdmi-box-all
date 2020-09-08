package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.dao.NetworkRegionDAO;
import com.huawei.sharedrive.uam.security.domain.NetworkRegion;
import com.huawei.sharedrive.uam.security.service.NetworkRegionService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.utils.IpUtils;

@Component
public class NetworkRegionServiceImpl implements NetworkRegionService
{
    
    @Autowired
    private NetworkRegionDAO networkRegionDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    public void save(NetworkRegion networkRegion)
    {
        networkRegionDAO.insert(setIpValue(networkRegion));
        configManager.setConfig(NetworkRegionServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public void delete(long id)
    {
        networkRegionDAO.delete(id);
        configManager.setConfig(NetworkRegionServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public Page<NetworkRegion> query(NetworkRegion networkRegion, PageRequest request)
    {
        int total = networkRegionDAO.queryCount(setIpValue(networkRegion));
        List<NetworkRegion> content = networkRegionDAO.query(setIpValue(networkRegion),
            request.getOrder(),
            request.getLimit());
        Page<NetworkRegion> page = new PageImpl<NetworkRegion>(content, request, total);
        return page;
    }
    
    @Override
    public NetworkRegion getById(Long id)
    {
        return networkRegionDAO.getById(id);
    }
    
    @Override
    public void update(NetworkRegion networkRegion)
    {
        networkRegionDAO.update(setIpValue(networkRegion));
        configManager.setConfig(NetworkRegionServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public long getNextId()
    {
        return networkRegionDAO.getNextAvailableNetworkRegionId();
    }
    
    @Override
    public int uniquelyCheck(NetworkRegion networkRegion)
    {
        configManager.setConfig(NetworkRegionServiceImpl.class.getSimpleName(), null);
        return networkRegionDAO.uniquelyCheck(setIpValue(networkRegion));
    }
    
    private NetworkRegion setIpValue(NetworkRegion networkRegion)
    {
        long ipStartValue = IpUtils.toLong(networkRegion.getIpStart());
        long ipEndValue = IpUtils.toLong(networkRegion.getIpEnd());
        networkRegion.setIpStartValue(ipStartValue);
        networkRegion.setIpEndValue(ipEndValue);
        return networkRegion;
    }
    
    @Override
    public NetworkRegion getByIpValue(long ipValue)
    {
        return networkRegionDAO.getByIpValue(ipValue);
    }
}
