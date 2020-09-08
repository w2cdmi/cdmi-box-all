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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupDao;
import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RWStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.Status;
import com.huawei.sharedrive.isystem.cluster.service.ResourceGroupService;

import pw.cdmi.common.config.service.ConfigManager;

/**
 * 
 * @author s90006125
 * 
 */
@Service("resourceGroupService")
public class ResourceGroupServiceImpl implements ResourceGroupService
{
    private final static String CONFIG_ZOOKEEPER_KEY_DC_CHANGE="config.zookeeper.key.dcchange";
    
    private static Logger logger = LoggerFactory.getLogger(ResourceGroupServiceImpl.class);
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private ResourceGroupDao resourceGroupDao;
    
    @Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
    
    @Override
    public List<ResourceGroup> listGroupsByDC(int dcid)
    {
        return resourceGroupDao.getAllByDC(dcid);
    }
    
    @Override
    public List<ResourceGroup> listGroupsByRegion(int regionID)
    {
        return resourceGroupDao.getAllByRegion(regionID);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByDC(int dcid)
    {
        resourceGroupDao.deleteByDC(dcid);
        resourceGroupNodeDao.deleteByDC(dcid);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRegion(int dcid, int regionID)
    {
        resourceGroupDao.updateRegionByDC(dcid, regionID);
        resourceGroupNodeDao.updateRegionByDC(dcid, regionID);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStatus(int dcid, Status status)
    {
        ResourceGroup resourceGroup = resourceGroupDao.get(dcid);
        if(resourceGroup == null)
        {
            logger.error("get resourcegroup is null with id:"+dcid);
            return;
        }
        resourceGroupDao.updateStatus(dcid, status.getCode());
        try{
            configManager.setConfig(CONFIG_ZOOKEEPER_KEY_DC_CHANGE, null);
        }
        catch (Exception e)
        {
            String message = "Notify to UFM Cluster Failed.";
            logger.warn(message, e);
            // 集群通知失败 ，则回滚状态
            resourceGroupDao.updateStatus(dcid, resourceGroup.getStatus().getCode());
            throw e;
        }
        
    }

    @Override
    public void updateRWStatus(int dcid, RWStatus rwStatus)
    {
        ResourceGroup resourceGroup = resourceGroupDao.get(dcid);
        if(resourceGroup == null)
        {
            logger.error("get resourcegroup is null with id:"+dcid);
            return;
        }
        resourceGroupDao.updataRWStatus(dcid, rwStatus.getCode());
        
        try{
            configManager.setConfig(CONFIG_ZOOKEEPER_KEY_DC_CHANGE, null);
        }
        catch (Exception e)
        {
            String message = "Notify to UFM Cluster Failed.";
            logger.warn(message, e);
            // 集群通知失败 ，则回滚状态
            resourceGroupDao.updataRWStatus(dcid, resourceGroup.getRwStatus().getCode());
            throw e;
        }
    }
}
