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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 
 * @author s90006125
 * 
 */
public class ResourceGroupComparer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceGroupComparer.class);
    
    private ResourceGroup newGroup;
    
    private ResourceGroup oldGroup;
    
    private List<ResourceGroupNode> nodesNeedAdd = new ArrayList<ResourceGroupNode>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private List<ResourceGroupNode> nodesNeedDelete = new ArrayList<ResourceGroupNode>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private List<ResourceGroupNode> nodesNeedUpdate = new ArrayList<ResourceGroupNode>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private boolean hasChange = false;
    
    /**
     * old和new两个对象都不能为null
     * 
     * @param newGroup
     * @param oldGroup
     */
    public ResourceGroupComparer(ResourceGroup newGroup, ResourceGroup oldGroup)
    {
        this.oldGroup = oldGroup;
        this.newGroup = newGroup;
        // 检测是否有变化
        this.hasChange = compare(newGroup, oldGroup);
    }
    
    public ResourceGroup getNewGroup()
    {
        return newGroup;
    }
    
    public ResourceGroup getOldGroup()
    {
        return oldGroup;
    }
    
    public List<ResourceGroupNode> getNodesNeedAdd()
    {
        return this.nodesNeedAdd;
    }
    
    public List<ResourceGroupNode> getNodesNeedDelete()
    {
        return this.nodesNeedDelete;
    }
    
    public List<ResourceGroupNode> getNodesNeedUpdate()
    {
        return this.nodesNeedUpdate;
    }
    
    public boolean isHasChange()
    {
        return this.hasChange;
    }
    
    private boolean compare(ResourceGroup newGroup, ResourceGroup oldGroup)
    {
        boolean hasChange = false;
        
        if (compareResourceGroupProperty(oldGroup, newGroup))
        {
            LOGGER.warn("Group Has Change.");
            hasChange = true;
        }
        
        // Map<String, ResourceGroupNode> oldNodes = transToMap(oldGroup.getNodes());
        // Map<String, ResourceGroupNode> newNodes = transToMap(newGroup.getNodes());
        //
        // findNeedAdd(oldNodes, newNodes);
        // if (!this.nodesNeedAdd.isEmpty())
        // {
        // hasChange = true;
        // }
        // findNeedDelete(oldNodes, newNodes);
        // if (!this.nodesNeedDelete.isEmpty())
        // {
        // hasChange = true;
        // }
        // findNeedUpdate(oldNodes, newNodes);
        // if (!this.nodesNeedUpdate.isEmpty())
        // {
        // hasChange = true;
        // }
        
        return hasChange;
    }
    
    /**
     * 检查资源组属性，看两个资源组是否有变化
     * 
     * @param oldGroup
     * @param newGroup
     * @return
     */
    private boolean compareResourceGroupProperty(ResourceGroup oldGroup, ResourceGroup newGroup)
    {
        if (oldGroup.getRuntimeStatus() != newGroup.getRuntimeStatus())
        {
            LOGGER.warn("RuntimeStatus Was Change, New RuntimStatus Is [ " + newGroup.getRuntimeStatus()
                + " ] ");
            return true;
        }
        
        if (oldGroup.getServiceHttpPort() != newGroup.getServiceHttpPort())
        {
            LOGGER.warn("Service Http Port　Was Change.");
            return true;
        }
        
        if (oldGroup.getServiceHttpsPort() != newGroup.getServiceHttpsPort())
        {
            LOGGER.warn("Service Https Port Was Change.");
            return true;
        }
        
        if (!StringUtils.equals(oldGroup.getServicePath(), newGroup.getServicePath()))
        {
            LOGGER.warn("Service　Path Was Change.");
            return true;
        }
        
        if (!StringUtils.equals(oldGroup.getGetProtocol(), newGroup.getGetProtocol()))
        {
            LOGGER.warn("Get Protocol Was Change.");
            return true;
        }
        
        if (!StringUtils.equals(oldGroup.getPutProtocol(), newGroup.getPutProtocol()))
        {
            LOGGER.warn("Put Protocol Was Change..");
            return true;
        }
        
        if (oldGroup.getNodes().size() != newGroup.getNodes().size())
        {
            LOGGER.warn("Node Size Was Change.");
            return true;
        }
        
        return false;
    }
    
    // private Map<String, ResourceGroupNode> transToMap(List<ResourceGroupNode> nodes)
    // {
    // Map<String, ResourceGroupNode> nodeMap = new HashMap<String, ResourceGroupNode>(
    // BusinessConstants.INITIAL_CAPACITIES);
    // if (null == nodes)
    // {
    // return nodeMap;
    // }
    // for (ResourceGroupNode node : nodes)
    // {
    // nodeMap.put(node.getName(), node);
    // }
    //
    // return nodeMap;
    // }
    //
    // /**
    // * 查找需要新增的节点
    // *
    // * @param oldNodes
    // * @param newNodes
    // * @return
    // */
    // private void findNeedAdd(Map<String, ResourceGroupNode> oldNodes, Map<String,
    // ResourceGroupNode> newNodes)
    // {
    // for (ResourceGroupNode node : newNodes.values())
    // {
    // if (!oldNodes.containsKey(node.getName()))
    // {
    // nodesNeedAdd.add(node);
    // }
    // }
    // }
    //
    // /**
    // * 查找需要删除的节点
    // *
    // * @param oldNodes
    // * @param newNodes
    // * @return
    // */
    // private void findNeedDelete(Map<String, ResourceGroupNode> oldNodes,
    // Map<String, ResourceGroupNode> newNodes)
    // {
    // for (ResourceGroupNode node : oldNodes.values())
    // {
    // if (!newNodes.containsKey(node.getName()))
    // {
    // nodesNeedDelete.add(node);
    // }
    // }
    // }
    //
    // /**
    // * 查找需要更新的节点
    // *
    // * @param oldNodes
    // * @param newNodes
    // * @return
    // */
    // private void findNeedUpdate(Map<String, ResourceGroupNode> oldNodes,
    // Map<String, ResourceGroupNode> newNodes)
    // {
    // ResourceGroupNode newNode = null;
    // for (ResourceGroupNode oldNode : oldNodes.values())
    // {
    // newNode = newNodes.get(oldNode.getName());
    // if (null == newNode)
    // {
    // continue;
    // }
    // if (!oldNode.getManagerIp().equals(newNode.getManagerIp()))
    // {
    // nodesNeedUpdate.add(newNode);
    // continue;
    // }
    //
    // if (oldNode.getManagerPort() != newNode.getManagerPort())
    // {
    // nodesNeedUpdate.add(newNode);
    // continue;
    // }
    //
    // if (!oldNode.getInnerAddr().equals(newNode.getInnerAddr()))
    // {
    // nodesNeedUpdate.add(newNode);
    // continue;
    // }
    //
    // if (!oldNode.getServiceAddr().equals(newNode.getServiceAddr()))
    // {
    // nodesNeedUpdate.add(newNode);
    // continue;
    // }
    //
    // if (oldNode.getRuntimeStatus() != newNode.getRuntimeStatus())
    // {
    // nodesNeedUpdate.add(newNode);
    // continue;
    // }
    // }
    // }
}
