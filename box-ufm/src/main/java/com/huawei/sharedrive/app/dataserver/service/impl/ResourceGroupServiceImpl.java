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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.core.alarm.DSSNodeOfflineAlarm;
import com.huawei.sharedrive.app.dataserver.dao.ResourceGroupDao;
import com.huawei.sharedrive.app.dataserver.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.Status;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode.RuntimeStatus;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;

import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;

/**
 * 
 * @author s90006125
 * 
 */
@Service("resourceGroupService")
public class ResourceGroupServiceImpl implements ResourceGroupService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceGroupServiceImpl.class);
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DSSNodeOfflineAlarm dssNodeOfflineAlarm;
    
    @Autowired
    private ResourceGroupDao resourceGroupDao;
    
    @Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceGroup createNewGroup(DataCenter dataCenter, ResourceGroup resourceGroup)
    {
        ResourceGroup group = new ResourceGroup();
        group.setId(dataCenter.getId());
        group.setDcId(dataCenter.getId());
        group.setRegionId(dataCenter.getRegion().getId());
        group.setManageIp(resourceGroup.getManageIp());
        group.setManagePort(resourceGroup.getManagePort());
        group.setDomainName(resourceGroup.getDomainName());
        group.setGetProtocol(resourceGroup.getGetProtocol());
        group.setPutProtocol(resourceGroup.getPutProtocol());
        group.setServiceHttpPort(resourceGroup.getServiceHttpPort());
        group.setServiceHttpsPort(resourceGroup.getServiceHttpsPort());
        group.setServicePath(resourceGroup.getServicePath());
        group.setStatus(ResourceGroup.Status.Initial);
        // 新的是资源组为读写状态
        group.setRwStatus(ResourceGroup.RWStatus.Normal);
        // 新创建的资源组，状态设置为离线
        group.setRuntimeStatus(ResourceGroup.RuntimeStatus.Offline);
        // 创建的时候，将上一次上报时间设置成当前时间，通过该时间避免添加后，DC重来不上报的问题
        group.setLastReportTime(System.currentTimeMillis());
        group.setAccessKey(generateNewKey());
        //　资源组部署类型
        group.setType(resourceGroup.getType());
        resourceGroupDao.create(group);
        return group;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceGroupNode createNewNode(ResourceGroup group, ResourceGroupNode groupNode)
    {
        ResourceGroupNode node = new ResourceGroupNode();
        node.setName(groupNode.getName());
        node.setResourceGroupId(group.getId());
        node.setDcId(group.getDcId());
        node.setRegionId(group.getRegionId());
        node.setManagerIp(groupNode.getManagerIp());
        node.setManagerPort(groupNode.getManagerPort());
        node.setInnerAddr(groupNode.getInnerAddr());
        node.setServiceAddr(groupNode.getServiceAddr());
        // 一期的网络方案采用dns方案，应此每个服务器的natip，都指定为该资源组的域名
        
        // 节点nat没有，在方法DC的时候，如果资源组域名不存在，则返回节点的serviceip，如果资源组的域名存在，则通过域名访问
        node.setStatus(ResourceGroupNode.Status.Enable);
        node.setRuntimeStatus(ResourceGroupNode.RuntimeStatus.Normal);
        node.setLastReportTime(System.currentTimeMillis());
        resourceGroupNodeDao.create(node);
        return node;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByDC(int dcid)
    {
        resourceGroupDao.deleteByDC(dcid);
        resourceGroupNodeDao.deleteByDC(dcid);
    }
    
    @Override
    public ResourceGroup findGroup(String managerIP, int managerPort)
    {
        return resourceGroupDao.get(managerIP, managerPort);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceGroup getResourceGroup(int groupid)
    {
        return resourceGroupDao.get(groupid);
    }
    
    @Override
    public ResourceGroupNode getResourceGroupNodeByManagerIp(String managerIp)
    {
       
        return resourceGroupNodeDao.getResourceGroupNodeByManagerIp(managerIp);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean handleReport(ResourceGroup newGroup)
    {
        // 首先和数据库中的数据进行比较
        ResourceGroup oldGroup = getResourceGroup(newGroup.getId());
        // 如果数据库中数据为空，表示该资源组未添加
        if (null == oldGroup)
        {
            String message = "Such ResourceGroup Is Not Regiest [ id : " + newGroup.getId() + " ] ";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        newGroup.setDcId(oldGroup.getDcId());
        newGroup.setRegionId(oldGroup.getRegionId());
        newGroup.setDomainName(oldGroup.getDomainName());
        // 不论有没有变化，都要更新，主要是刷新最新更新时间这个字段
        resourceGroupDao.update(newGroup);
        
        // 比较数据库中的数据和本次上报数据
        ResourceGroupComparer comparer = new ResourceGroupComparer(newGroup, oldGroup);
        
        boolean hasChange = comparer.isHasChange();
        // 如果有更新，就进行数据库更新
        if (hasChange)
        {
            handleWhenChange(newGroup, comparer);
        }
        else
        {
            LOGGER.info("No Chanage From DB Data [ " + newGroup.getId() + " ]");
            // 如果和数据库数据比较，没有更新，则再和缓存数据进行比较
            oldGroup = dcManager.getCacheResourceGroup(newGroup.getId());
            if (null == oldGroup)
            {
                LOGGER.info("Cache Data Is Null [ " + newGroup.getId() + " ]");
                hasChange = true;
            }
            else
            {
                comparer = new ResourceGroupComparer(newGroup, oldGroup);
                hasChange = comparer.isHasChange();
            }
        }
        return hasChange;
    }
    @Override
    public List<ResourceGroup> listAllGroups()
    {
        return resourceGroupDao.getAll();
    }
    
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
    public void updateDomainNameByDc(int dcid, String domainName)
    {
        resourceGroupDao.updateDomainNameByDc(dcid, domainName);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRuntimeStatus(int dcid,
        com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RuntimeStatus status)
    {
        resourceGroupDao.updateRuntimeStatus(dcid, status.getCode());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStatus(int dcid, Status status)
    {
        resourceGroupDao.updateStatus(dcid, status.getCode());
    }
    
    private String generateNewKey()
    {
        return "";
    }
    
    private void handleWhenChange(ResourceGroup newGroup, ResourceGroupComparer comparer)
    {
        LOGGER.info("HasChanage From DB Data [ " + newGroup.getId() + " ]");
        
        // 更新节点表
        if (!comparer.getNodesNeedAdd().isEmpty())
        {
            LOGGER.warn("nodesNeedAdd.");
            for (ResourceGroupNode node : comparer.getNodesNeedAdd())
            {
                // 添加新的节点
                createNewNode(newGroup, node);
            }
        }
        if (!comparer.getNodesNeedDelete().isEmpty())
        {
            LOGGER.warn("nodesNeedDelete.");
            for (ResourceGroupNode node : comparer.getNodesNeedDelete())
            {
                // 删除节点信息
                resourceGroupNodeDao.delete(node);
            }
        }
        if (!comparer.getNodesNeedUpdate().isEmpty())
        {
            LOGGER.warn("nodesNeedUpdate.");
            Alarm alarm = null;
            for (ResourceGroupNode node : comparer.getNodesNeedUpdate())
            {
                // 更新节点信息
                resourceGroupNodeDao.update(node);
                alarm = new DSSNodeOfflineAlarm(dssNodeOfflineAlarm, node.getServiceAddr());
                if(node.getRuntimeStatus() == RuntimeStatus.Offline)
                {
                    alarmHelper.sendAlarm(alarm);
                }
                else
                {
                    alarmHelper.sendRecoverAlarm(alarm);
                }
            }
        }
    }
}
