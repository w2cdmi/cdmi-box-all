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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.dao.DCDao;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter.Status;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCService;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.DCThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;

/**
 * 
 * @author s90006125
 * 
 */
@Component("dcService")
public class DCServiceImpl implements DCService,InitializingBean
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DCServiceImpl.class);
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    // 循环引用bug修复
	@Autowired
	private CopyConfigLocalCache copyPolicyLocalCache;
	
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized void activeDataCenter(int dcid)
    {
        DataCenter dataCenter = getDataCenter(dcid);
        if (null == dataCenter)
        {
            String message = "DataCenter Not Exist With DCID [ " + dcid + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        // 如果已经激活，就不在发送请求
        if (DataCenter.Status.Enable == dataCenter.getStatus())
        {
            return;
        }
        dataCenter.setStatus(DataCenter.Status.Enable);
        // 更新数据库状态
        dcDao.update(dataCenter);
        resourceGroupService.updateStatus(dcid, ResourceGroup.Status.Enable);
        
        // 一期版本，resourcegroup和dc是1对1的关系
        ResourceGroup group = dataCenter.getResourceGroup();
        // 选择一个可用的节点，发送激活请求
        String domain = dssDomainService.getDomainByDssId(group);
        // 调用DC提供的Thrift接口
        DCThriftServiceClient client = null;
        try
        {
            client = new DCThriftServiceClient(domain, group.getManagePort());
            
            client.active();
        }
        catch (TBusinessException e)
        {
            LOGGER.warn("addDataCenter Failed.", e);
            throw new BusinessException(e.getStatus(), e.getMessage(), e);
        }
        catch (TException e)
        {
            LOGGER.warn("addDataCenter Failed.", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
        
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized DataCenter addDataCenter(String name, ResourceGroup resourceGroup)
    {
        if (null != this.findByName(name)
            || null != resourceGroupService.findGroup(resourceGroup.getManageIp(),
                resourceGroup.getManagePort()))
        {
            String message = "DataCenter Already Exist With [ name : " + name + ",managerIp : "
                + resourceGroup.getManageIp() + ", port: " + resourceGroup.getManagePort() + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.AlreadyExistException, message);
        }
        
        // 判断区域是否存在
        Region region = regionService.getRegion(resourceGroup.getRegionId());
        if (null == region)
        {
            String message = "NoSuch Region [ " + resourceGroup.getRegionId() + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        int dcid = newDCId();
        
        // 调用DC提供的Thrift接口
        DCThriftServiceClient client = null;
        try
        {
            client = new DCThriftServiceClient(resourceGroup.getManageIp(), resourceGroup.getManagePort());
            
            // 调用DC远程接口，进行初始化
            // 如果在调用该接口之前，DC已经初始化了，那么该接口返回的DC对象的ID和我们发送请求的dcid是不一样的
            com.huawei.sharedrive.thrift.dc2app.ResourceGroup dc = client.init(dcid,
                getReportIP(),
                getReportPort(),
                resourceGroup.getGetProtocol(),
                resourceGroup.getPutProtocol());
            
            DataCenter dataCenter = dcDao.get(dc.getId());
            if (null != dataCenter)
            {
                String message = "DataCenter Already Exist With [ managerIp : " + resourceGroup.getManageIp()
                    + ", port: " + resourceGroup.getManagePort() + " ]";
                LOGGER.warn(message);
                throw new BusinessException(BusinessErrorCode.AlreadyExistException, message);
            }
            
            dataCenter = new DataCenter();
            dataCenter.setId(dc.getId());
            dataCenter.setName(name);
            dataCenter.setRegion(region);
            dataCenter.setStatus(Status.Initial);
            // 保存DC元数据
            dcDao.create(dataCenter);
            
            ResourceGroup tempResourceGroup = new ResourceGroup();
            BeanUtils.copyProperties(resourceGroup, tempResourceGroup);
            tempResourceGroup.setServiceHttpPort(dc.getServiceHttpPort());
            tempResourceGroup.setServiceHttpsPort(dc.getServiceHttpsPort());
            tempResourceGroup.setServicePath(dc.getServicePath());
            // 设置集群类型： 0:合并部署, 1:分开部署
            tempResourceGroup.setType(dc.getType());
            
            // 一期的模型是DC和resourcegroup 1:1的关系，因此这两个对象的ID是一样的
            // 保存资源组元数据
            ResourceGroup group = resourceGroupService.createNewGroup(dataCenter, tempResourceGroup);
            
            LOGGER.warn("nodesNeedAdd.");
            ResourceGroupNode tempNode = null;
            for (com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode node : dc.getNodes())
            {
                tempNode = new ResourceGroupNode();
                tempNode.setName(node.getName());
                tempNode.setManagerIp(node.getManagerIp());
                tempNode.setManagerPort(node.getManagerPort());
                tempNode.setInnerAddr(node.getInnerAddr());
                tempNode.setServiceAddr(node.getServiceAddr());
                // 添加新的节点
                resourceGroupService.createNewNode(group, tempNode);
            }
            dataCenter.setResourceGroup(group);
            
            return dataCenter;
        }
        catch (TBusinessException e)
        {
            LOGGER.warn("addDataCenter Failed.", e);
            throw new BusinessException(e.getStatus(), e.getMessage(), e);
        }
        catch (TException e)
        {
            LOGGER.warn("addDataCenter Failed.", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized void deleteDataCenter(int dcid)
    {
        DataCenter dataCenter = getDataCenterCheckStatus(dcid);
        
        dcDao.delete(dcid);
        resourceGroupService.deleteByDC(dcid);
        
        ResourceGroup group = dataCenter.getResourceGroup();
        // 选择一个可用的几点，发送激活请求
        String domain = dssDomainService.getDomainByDssId(group);
        DCThriftServiceClient client = null;
        try
        {
            client = new DCThriftServiceClient(domain, group.getManagePort());
            
            client.reset();
        }
        catch (Exception e)
        {
            LOGGER.warn("Reset DC Failed.", e);
        }
        finally
        {
            if (client != null)
            {
                client.close();
            }
        }
    }
    
    private DataCenter getDataCenterCheckStatus(int dcid)
    {
        DataCenter dataCenter = getDataCenter(dcid);
        if (null == dataCenter)
        {
            String message = "DataCenter Not Exist With DCID [ " + dcid + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        // 如果已经激活，就不在发送请求
        if (DataCenter.Status.Enable == dataCenter.getStatus())
        {
            String message = "DataCenter Already Active, Cann't Delete.";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.PreconditionFailedException, message);
        }
        return dataCenter;
    }
    
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
        List<ResourceGroup> groups = resourceGroupService.listGroupsByDC(dcid);
        if (null != groups && !groups.isEmpty())
        {
            dataCenter.setResourceGroup(groups.get(0));
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
            if (null != groups && !groups.isEmpty())
            {
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
            if (null != groups && !groups.isEmpty())
            {
                dc.setResourceGroup(groups.get(0));
            }
        }
        return dataCenters;
    }
    
    @Override
    public int newDCId()
    {
        return dcDao.getNextId();
    }
    
    /**
     * 获取APP自己的上报IP，DC根据这个IP进行上报
     * 
     * @return
     */
    private String getReportIP()
    {
        return PropertiesUtils.getProperty("thrift.dataserver.report.addr", "127.0.0.1");
    }
    
    /**
     * 获取APP自己的上报端口，DC根据这个端口进行上报
     * 
     * @return
     */
    private int getReportPort()
    {
        String port = PropertiesUtils.getProperty("thrift.dataserver.port", "13003");
        return Integer.parseInt(port);
    }
    
    @Override
    public List<ResourceGroupNode> getNodeList(int resourceGroupId)
    {
        ResourceGroup resourceGroup = resourceGroupService.getResourceGroup(resourceGroupId);
        if (null == resourceGroup)
        {
            String message = "ResourceGroup Not Found [ resourceGroupId : " + resourceGroupId + " ]";
            LOGGER.warn(message);
            throw new BusinessException(BusinessErrorCode.NotFoundException, message);
        }
        
        // 调用DC提供的Thrift接口
        DCThriftServiceClient client = null;
        String domain = dssDomainService.getDomainByDssId(resourceGroup);
        try
        {
            client = new DCThriftServiceClient(domain, resourceGroup.getManagePort());
            
            List<com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode> nodeList = client.getResourceGroupNodeList();
            
            List<ResourceGroupNode> nodeListTemp = new ArrayList<ResourceGroupNode>(nodeList.size());
            if (CollectionUtils.isNotEmpty(nodeList))
            {
                ResourceGroupNode tempItem = null;
                long time = System.currentTimeMillis();
                for (com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode item : nodeList)
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
                    tempItem.setRegionId(resourceGroup.getRegionId());
                    tempItem.setResourceGroupId(resourceGroupId);
                    tempItem.setRuntimeStatus(ResourceGroupNode.RuntimeStatus.parseStatus(item.getRuntimeStatus()));
                    tempItem.setServiceAddr(item.getServiceAddr());
                    tempItem.setStatus(ResourceGroupNode.Status.parseStatus(item.getStatus()));
                    nodeListTemp.add(tempItem);
                }
            }
            
            return nodeListTemp;
        }
        catch (TBusinessException e)
        {
            LOGGER.warn("getResourceGroupNodeList Failed.", e);
            throw new BusinessException(e.getStatus(), e.getMessage(), e);
        }
        catch (TException e)
        {
            LOGGER.warn("v Failed.", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    public List<DataCenter> listPriorityDataCenter()
    {
        List<DataCenter> dataCenters = dcDao.getAllPriorityDataCenter();
        if (null == dataCenters || dataCenters.isEmpty())
        {
            return null;
        }
        
        List<ResourceGroup> groups = null;
        for (DataCenter dc : dataCenters)
        {
            groups = resourceGroupService.listGroupsByDC(dc.getId());
            if (null != groups && !groups.isEmpty())
            {
                dc.setResourceGroup(groups.get(0));
            }
        }
        return dataCenters;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		List<DataCenter> lstdc = null;
		lstdc = this.listPriorityDataCenter();
		if (null == lstdc || lstdc.isEmpty()) {
			return;
		}
		copyPolicyLocalCache.addDataCenters(lstdc);
	}
}
