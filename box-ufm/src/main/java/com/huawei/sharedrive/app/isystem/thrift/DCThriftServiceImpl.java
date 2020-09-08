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
package com.huawei.sharedrive.app.isystem.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.thrift.app2isystem.DCThriftService;
import com.huawei.sharedrive.thrift.app2isystem.ResourceGroupCreateInfo;
import com.huawei.sharedrive.thrift.app2isystem.TBusinessException;

import pw.cdmi.common.log.LoggerUtil;

/**
 * 提供给iSystem的Thrift接口
 * 
 * @author s90006125
 * 
 */
public class DCThriftServiceImpl implements DCThriftService.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCThriftServiceImpl.class);
    
    @Autowired
    private DCManager dcManager;
    
    @Override
    public void activeResourceGroup(int dcid) throws TBusinessException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            dcManager.activeDataCenter(dcid);
        }
        catch (BusinessException e)
        {
            LOGGER.warn("Active DC Faile.", e);
            throw new TBusinessException(e.getCode(), "" + e);
        }
        catch (Exception e)
        {
            String message = "Active DC Faile.";
            LOGGER.warn(message, e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), message + e);
        }
    }
    
    /**
     * 参数managerip不是真正的管理网IP，而是AC和DC之间的交互IP，网络整改后，是业务网IP
     */
    @Override
    public void addResourceGroup(ResourceGroupCreateInfo createInfo) throws TBusinessException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            LOGGER.info("Add ResourceGoup [ " + createInfo.getName() + ", " + createInfo.getManagerIp() + ", " + createInfo.getManagerPort() + ", " + ", "
                + createInfo.getRegionId() + ", " + createInfo.getDomainName() + ", " + createInfo.getGetProtocol() + ", " + createInfo.getPutProtocol() + " ]");
            ResourceGroup resourceGroup = new ResourceGroup();
            resourceGroup.setDomainName(createInfo.getDomainName());
            resourceGroup.setGetProtocol(createInfo.getGetProtocol());
            resourceGroup.setPutProtocol(createInfo.getPutProtocol());
            resourceGroup.setManageIp(createInfo.getManagerIp());
            resourceGroup.setManagePort(createInfo.getManagerPort());
            resourceGroup.setRegionId(createInfo.getRegionId());
            
            dcManager.addDataCenter(createInfo.getName(), resourceGroup);
        }
        catch (BusinessException e)
        {
            LOGGER.warn("Add DC Faile.", e);
            throw new TBusinessException(e.getCode(), "" + e);
        }
        catch (Exception e)
        {
            String message = "Add DC Faile.";
            LOGGER.warn(message, e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), message + e);
        }
    }
    
    @Override
    public void deleteResourceGroup(int dcid) throws TBusinessException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            dcManager.deleteDataCenter(dcid);
        }
        catch (BusinessException e)
        {
            LOGGER.warn("Delete DC Faile.", e);
            throw new TBusinessException(e.getCode(), "" + e);
        }
        catch (Exception e)
        {
            String message = "Delete DC Faile.";
            LOGGER.warn(message, e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), message + e);
        }
    }
    
    @Override
    public void modifyResourceGroup(int dcid, String domainName)
        throws TBusinessException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            dcManager.updateDomainNameByDc(dcid, domainName);
        }
        catch (BusinessException e)
        {
            LOGGER.warn("ModifyResourceGroup Fail.", e);
            throw new TBusinessException(e.getCode(), "" + e);
        }
        catch (Exception e)
        {
            String message = "ModifyResourceGroup Fail.";
            LOGGER.warn(message, e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), message + e);
        }
    }
    
    @Override
    public List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroup> getResourceGroupList(int dcid) throws TBusinessException, TException
    {
        List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroup> result = new ArrayList<com.huawei.sharedrive.thrift.app2isystem.ResourceGroup>(1);
            com.huawei.sharedrive.thrift.app2isystem.ResourceGroup resourceGroupTemp = null;

        List<ResourceGroup> list = dcManager.getResourceGroupByDcId(dcid);
        if (CollectionUtils.isNotEmpty(list))
        {
            for (ResourceGroup item : list)
            {
                resourceGroupTemp = new com.huawei.sharedrive.thrift.app2isystem.ResourceGroup();
                resourceGroupTemp.setDcId(dcid);
                resourceGroupTemp.setType(item.getType());
                resourceGroupTemp.setId(item.getId());
                resourceGroupTemp.setLastReportTime(item.getLastReportTime());
                resourceGroupTemp.setDomainName(item.getDomainName());
                resourceGroupTemp.setManagerIp(item.getManageIp());
                resourceGroupTemp.setManagerPort(item.getManagePort());
                resourceGroupTemp.setRegionId(item.getRegionId());
                resourceGroupTemp.setRuntimeStatus(item.getRuntimeStatus().getCode());
                resourceGroupTemp.setServiceHttpPort(item.getServiceHttpPort());
                resourceGroupTemp.setServiceHttpsPort(item.getServiceHttpsPort());
                resourceGroupTemp.setServicePath(item.getServicePath());
                resourceGroupTemp.setRwStatus(item.getRwStatus().getCode());
                resourceGroupTemp.setStatus(item.getStatus().getCode());
                result.add(resourceGroupTemp);
            }
        }
        
       return result;
    }
    
    @Override
    public List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode> getResourceGroupNodeList(int resourceGroupId) throws TBusinessException, TException
    {
        List<ResourceGroupNode> nodeList = dcManager.getNodeList(resourceGroupId);
        
        List<com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode> nodeListTemp = new ArrayList<com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode>(nodeList.size());
        if (CollectionUtils.isNotEmpty(nodeList))
        {
            com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode tempItem;
            for (ResourceGroupNode item : nodeList)
            {
                tempItem = new com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode();
                tempItem.setDcId(item.getDcId());
                tempItem.setInnerAddr(item.getInnerAddr());
                tempItem.setManagerIp(item.getManagerIp());
                tempItem.setManagerPort(item.getManagerPort());
                tempItem.setName(item.getName());
                tempItem.setNatAddr(item.getNatAddr());
                tempItem.setNatPath(item.getNatPath());
                tempItem.setRegionId(item.getRegionId());
                tempItem.setResourceGroupId(resourceGroupId);
                tempItem.setRuntimeStatus(item.getRuntimeStatus().getCode());
                tempItem.setServiceAddr(item.getServiceAddr());
                tempItem.setStatus(item.getStatus().getCode());
                nodeListTemp.add(tempItem);
            }
        }
        return nodeListTemp;
    }
    
}
