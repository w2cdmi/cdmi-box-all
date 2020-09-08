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
package com.huawei.sharedrive.app.dataserver.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.dao.RegionDao;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.NetSegment;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCService;
import com.huawei.sharedrive.app.dataserver.service.NetConfigService;
import com.huawei.sharedrive.app.dataserver.service.RegionService;

/** 
 * @author s90006125
 * 
 */
@Component("regionService")
public class RegionServiceImpl implements RegionService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionServiceImpl.class);
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private NetConfigService netConfigService;
    
    @Autowired
    private RegionDao regionDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRegion(String name, String description)
    {
        Region region = new Region();
        region.setId(regionDao.getNextAvailableRegionId());
        region.setName(name);
        region.setDescription(description);
        // 如果当前没有默认区域，则将新增的区域设置为默认区域
        region.setDefaultRegion(null == getDefaultRegion());
        regionDao.create(region);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeRegion(int id, String name, String description)
    {
        Region region = getRegion(id);
        if (null == region)
        {
            return;
        }
        region.setId(id);
        region.setName(name);
        region.setDescription(description);
        regionDao.update(region);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRegion(int regionID)
    {
        Region region = getRegion(regionID);
        if (null == region)
        {
            return;
        }
        
        if (region.isDefaultRegion())
        {
            String message = "Cann't Delete Default Region [ " + regionID + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.PreconditionFailedException, message);
        }
        
        List<DataCenter> dataCenters = dcService.listDataCenter(regionID);
        if (null == dataCenters || dataCenters.isEmpty())
        {
            regionDao.delete(regionID);
        }
        else
        {
            String message = "Cann't Delete An Region Has DataCenter [ " + regionID + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.PreconditionFailedException, message);
        }
    }
    
    @Override
    public Region findRegionByAccessAddress(String ip)
    {
        NetSegment netSegment = netConfigService.findNetSegment(ip);
        if (null == netSegment)
        {
            return null;
        }
        
        return getRegion(netSegment.getRegionId());
    }
    
    @Override
    public Region getDefaultRegion()
    {
        return regionDao.getDefaultRegion();
    }
    
    @Override
    public Region getRegion(int regionid)
    {
        return regionDao.get(regionid);
    }
    
    @Override
    public List<Region> listRegion()
    {
        return regionDao.getAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setDefaultRegion(int regionID)
    {
        Region region = getDefaultRegion();
        if (null != region)
        {
            // 先讲现有的默认区域设置为非默认区域
            region.setDefaultRegion(false);
            regionDao.update(region);
        }
        
        region = getRegion(regionID);
        if (null == region)
        {
            return;
        }
        region.setDefaultRegion(true);
        regionDao.update(region);
    }
}
