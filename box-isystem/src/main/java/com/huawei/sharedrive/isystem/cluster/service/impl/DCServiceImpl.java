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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RWStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RuntimeStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.Status;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.cluster.service.ResourceGroupService;
import com.huawei.sharedrive.isystem.dns.common.DnsThriftCommon;
import com.huawei.sharedrive.isystem.dns.dao.DnsDomainDao;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;
import com.huawei.sharedrive.isystem.dns.manager.InnerLoadBalanceManager;
import com.huawei.sharedrive.isystem.exception.BusinessErrorCode;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.thrift.client.DCManageServiceClient;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

/**
 * 
 * @author s90006125
 *         
 */
@Component("dcService")
public class DCServiceImpl implements DCService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DCServiceImpl.class);
    
    public final static String PRIORITY_CHANGE_KEY = "priority_change";
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private DnsDomainDao dnsDomainDao;
    
    @Autowired
    private DnsThriftCommon dnsThriftCommon;
    
    @Autowired
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    @Override
    public DataCenter findByName(String name)
    {
        return dcDao.getByName(name);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public DataCenter getDataCenter(int dcid)
    {
        DataCenter dataCenter = dcDao.get(dcid);
        if (null == dataCenter)
        {
            return null;
        }
        // 若是开启了内部负载均衡，直接返回，前面已经从数据库获取到状态，不走之前DNS更新状态的流程，没开启走DNS更新状态
        if (innerLoadBalanceManager.isSysInnerLoadblanceConfig())
        {
            fillDataCenterInfoFromApp(dataCenter);
        }
        
        return dataCenter;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public DataCenter getSingleDataCenter(int regionId) {
        List<DataCenter> dataCenters = dcDao.getAllByRegion(regionId);
        if (null == dataCenters)
        {
            return null;
        }
        if(dataCenters.size() > 1) {
            throw new RuntimeException("一个区域已存在多个数据中心");
        }
        DataCenter dataCenter = dataCenters.get(0);
        // 若是开启了内部负载均衡，直接返回，前面已经从数据库获取到状态，不走之前DNS更新状态的流程，没开启走DNS更新状态
        if (innerLoadBalanceManager.isSysInnerLoadblanceConfig())
        {
            fillDataCenterInfoFromApp(dataCenter);
        }
        
        return dataCenter;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<DataCenter> listDataCenter()
    {
        List<DataCenter> dataCenters = dcDao.getAll();
        if (null == dataCenters || dataCenters.isEmpty())
        {
            return null;
        }
        List<ResourceGroup> groups = null;
        for (DataCenter dc : dataCenters)
        {
            groups = resourceGroupService.listGroupsByDC(dc.getId());
            if (null != groups && groups.size() >= 1)
            {
                // 当前版本DC和resourcegroup是1:1的关系
                dc.setResourceGroup(groups.get(0));
            }
        }
        return dataCenters;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<DataCenter> listDataCenter(int regionID)
    {
        List<DataCenter> dataCenters = dcDao.getAllByRegion(regionID);
        if (null == dataCenters || dataCenters.isEmpty())
        {
            return null;
        }
        List<ResourceGroup> groups = null;
        for (DataCenter dc : dataCenters)
        {
            groups = resourceGroupService.listGroupsByDC(dc.getId());
            if (null != groups && groups.size() >= 1)
            {
                // 当前版本DC和resourcegroup是1:1的关系
                dc.setResourceGroup(groups.get(0));
            }
        }
        return dataCenters;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<DataCenter> listDataCenterRe(int regionID)
    {
        List<DataCenter> dataCenters = dcDao.getAllByRegion(regionID);
        if (null == dataCenters || dataCenters.isEmpty())
        {
            return null;
        }
        // 是否开启内部负载均衡
        boolean blinnerLoadBalance = innerLoadBalanceManager.isSysInnerLoadblanceConfig();
        
        for (DataCenter dc : dataCenters)
        {
            fillDataCenterInfo(blinnerLoadBalance, dc);
        }
        return dataCenters;
    }
    
    private void fillDataCenterInfo(boolean blinnerLoadBalance, DataCenter dc)
    {
        // 若是没有开启内部负载均衡，走之前DNS更新状态的流程
        if (!blinnerLoadBalance)
        {
            List<ResourceGroup> groups = resourceGroupService.listGroupsByDC(dc.getId());
            if (null != groups && groups.size() >= 1)
            {
                // 当前版本DC和resourcegroup是1:1的关系
                dc.setResourceGroup(groups.get(0));
            }
            
            List<DssDomain> domains = dnsDomainDao.getAllByDataCenterID(dc.getId());
            if (null != domains && domains.size() >= 1)
            {
                // 当前版本DC和resourcegroup是1:1的关系
                dc.setDssDomain(domains);
            }
            RuntimeStatus runtimeStatus = dnsThriftCommon.getDataCenterStatus(dc);
            
            LOGGER.info("DC runtimeStatus " + runtimeStatus + "---" + runtimeStatus.getCode());
            dc.getResourceGroup().setRuntimeStatus(runtimeStatus);
        }
        else
        {
            fillDataCenterInfoFromApp(dc);
        }
    }
    
    private void fillDataCenterInfoFromApp(DataCenter dc)
    {
        List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroup> resourceGroupList = new ArrayList<com.huawei.sharedrive.thrift.app2isystem.ResourceGroup>(
            1);
        try
        {
            resourceGroupList = ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class)
                .getResourceGroupList(dc.getId());
        }
        catch (TException e)
        {
            LOGGER.error("getResourceGroupInfo error");
        }
        
        if (CollectionUtils.isNotEmpty(resourceGroupList))
        {
            // 当前版本DC和resourcegroup是1:1的关系
            com.huawei.sharedrive.thrift.app2isystem.ResourceGroup resourceGroup = resourceGroupList.get(0);
            ResourceGroup resourceGroupTemp = new ResourceGroup();
            resourceGroupTemp.setDcId(dc.getId());
            resourceGroupTemp.setId(resourceGroup.getId());
            resourceGroupTemp.setType(resourceGroup.getType());
            resourceGroupTemp.setLastReportTime(resourceGroup.getLastReportTime());
            resourceGroupTemp.setDomainName(resourceGroup.getDomainName());
            resourceGroupTemp.setManageIp(resourceGroup.getManagerIp());
            resourceGroupTemp.setManagePort(resourceGroup.getManagerPort());
            resourceGroupTemp.setProtocol(resourceGroup.getProtocol());
            resourceGroupTemp.setRegionId(resourceGroup.getRegionId());
            resourceGroupTemp.setRuntimeStatus(RuntimeStatus.parseStatus(resourceGroup.getRuntimeStatus()));
            resourceGroupTemp.setServiceHttpPort(resourceGroup.getServiceHttpPort());
            resourceGroupTemp.setServiceHttpsPort(resourceGroup.getServiceHttpsPort());
            resourceGroupTemp.setServicePath(resourceGroup.getServicePath());
            resourceGroupTemp.setRwStatus(RWStatus.parseStatus(resourceGroup.getRwStatus()));
            resourceGroupTemp.setStatus(Status.parseStatus(resourceGroup.getStatus()));
            fillResourceGroupNodeFromApp(resourceGroupTemp);
            dc.setResourceGroup(resourceGroupTemp);
            
        }
    }
    
    private void fillResourceGroupNodeFromApp(ResourceGroup resourceGroup)
    {
        List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode> nodeList = new ArrayList<com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode>(
            4);
        try
        {
            nodeList = ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class)
                .getResourceGroupNodeList(resourceGroup.getId());
        }
        catch (TException e)
        {
            LOGGER.error("getResourceGroupNodeList error");
        }
        
        if (CollectionUtils.isNotEmpty(nodeList))
        {
            ResourceGroupNode tempItem;
            long time = System.currentTimeMillis();
            List<ResourceGroupNode> nodeListTemp = new ArrayList<ResourceGroupNode>(nodeList.size());
            for (com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode item : nodeList)
            {
                tempItem = new ResourceGroupNode();
                tempItem.setDcId(resourceGroup.getDcId());
                tempItem.setInnerAddr(item.getInnerAddr());
                tempItem.setLastReportTime(time);
                tempItem.setManagerIp(item.getManagerIp());
                tempItem.setManagerPort(item.getManagerPort());
                tempItem.setName(item.getName());
                tempItem.setNatAddr(item.getNatAddr());
                tempItem.setNatPath(item.getNatPath());
                tempItem.setRegionId(item.getRegionId());
                tempItem.setResourceGroupID(resourceGroup.getId());
                tempItem
                    .setRuntimeStatus(ResourceGroupNode.RuntimeStatus.parseStatus(item.getRuntimeStatus()));
                tempItem.setServiceAddr(item.getServiceAddr());
                tempItem.setStatus(ResourceGroupNode.Status.parseStatus(item.getStatus()));
                nodeListTemp.add(tempItem);
            }
            
            resourceGroup.setNodes(nodeListTemp);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRegion(int dcid, int regionid)
    {
        DataCenter dataCenter = dcDao.get(dcid);
        if (null == dataCenter)
        {
            String message = "DataCenter Not Exist With DCID [ " + dcid + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        // 判断区域是否存在
        Region region = regionService.getRegion(regionid);
        if (null == region)
        {
            String message = "NoSuch Region [ " + regionid + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        if (dataCenter.getRegion().getId() == region.getId())
        {
            return;
        }
        dataCenter.setRegion(region);
        dcDao.update(dataCenter);
        resourceGroupService.updateRegion(dcid, region.getId());
    }

    @Override
    public void setPriority(int regionid, int dcid)
    {
        // 先将该区域下的所有数据中心优先级设置为0
        dcDao.setAllPriorityDefault(regionid);
        // 在指定区域下的指定数据中心优先级设置为1
        dcDao.setPriority(regionid,dcid);
        String sregionid=Integer.toString(regionid);
        String sdcid=Integer.toString(dcid);
        String str=sregionid+"-"+sdcid;
        sendPriorityChangeNotify(PRIORITY_CHANGE_KEY,str);
    }

    @Override
    public void setPriorityDefault(int regionid, int dcid)
    {
        // 在指定区域下的指定数据中心优先级设置为0
        dcDao.setPriorityDefault(regionid,dcid);
        String sregionid=Integer.toString(regionid);
        String sdcid=Integer.toString(dcid);
        String str=sregionid+"-"+sdcid;
        sendPriorityChangeNotify(PRIORITY_CHANGE_KEY,str);
    }
    /**
     * 发送优先级改变的消息
     */
    private void sendPriorityChangeNotify(String key, String value)
    {
        configManager.setConfig(key, value);
    }

}
