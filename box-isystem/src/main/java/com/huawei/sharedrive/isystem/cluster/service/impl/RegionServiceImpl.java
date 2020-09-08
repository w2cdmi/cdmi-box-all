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
package com.huawei.sharedrive.isystem.cluster.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.RegionDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.exception.BusinessErrorCode;
import com.huawei.sharedrive.isystem.exception.BusinessException;

/**
 * 
 * @author s90006125
 * 
 */
@Component("regionService")
public class RegionServiceImpl implements RegionService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionServiceImpl.class);
    
    @Autowired
    private RegionDao regionDao;
    
    @Autowired
    private DCService dcService;
    
    @Override
    public Region getRegion(int regionid)
    {
        return regionDao.get(regionid);
    }
    
    @Override
    public Region getRegionByCode(String code) {
        return regionDao.findByCode(code);
    }
    
    @Override
    public List<Region> listRegion()
    {
        return regionDao.getAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRegion(String name, String code, String description)
    {
        Region region = new Region();
        region.setId(regionDao.getNextAvailableRegionId());
        region.setCode(code);
        region.setName(name);
        region.setDescription(description);
        // 如果当前没有默认区域，则将新增的区域设置为默认区域
        region.setDefaultRegion(null == getDefaultRegion());
        regionDao.create(region);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeRegion(int id, String name, String code, String description)
    {
        Region region = getRegion(id);
        if (null == region)
        {
            return;
        }
        
        region.setId(id);
        region.setName(name);
        if(!StringUtils.isBlank(code)){
        	region.setCode(code);
        }
        region.setDescription(description);
        
        regionDao.update(region);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setDefaultRegion(int regionID)
    {
        Region region = getDefaultRegion();
        if (null != region)
        {
            // 先将现有的默认区域设置为非默认区域
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
    public Region getDefaultRegion()
    {
        return regionDao.getDefaultRegion();
    }


}
