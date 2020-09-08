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
package com.huawei.sharedrive.isystem.logfile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupType;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.logfile.domain.FSEndpoint;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgent;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgentNode;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentBaseService;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentService;

import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 
 * @author s90006125
 *         
 */
@Service("logAgentService")
public class LogAgentServiceImpl extends LogAgentBaseService implements LogAgentService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAgentService.class);
    
    @Autowired
    private DCService dcService;
    
    @Override
    public FSEndpoint getFSEndpointByClusterId(int clusterId)
    {
        LogAgent logAgent = logAgentDao.selectByClusterId(clusterId);
        if (null == logAgent)
        {
            // 如果还没有对接logAgent，则肯定获取不到想要的存储信息
            return null;
        }
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        TextResponse response = logAgentClientService.performGetTextByUri(
            getCenterAgentUri() + "/api/v2/logagent/manage/fsendpoint/" + logAgent.getId(), headers);
            
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "get fsendpoint for logagent failed [ "
                + ReflectionToStringBuilder.toString(response) + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        return JsonUtils.stringToObject(response.getResponseBody(), FSEndpoint.class);
    }
    
    @Override
    public void setFSEndpointForCluster(int clusterId, String fsType, String endpoint)
    {
        LogAgent logAgent = logAgentDao.selectByClusterId(clusterId);
        if (null == logAgent)
        {
            // 先对接，将制定的logAgent加入到库中
            logAgent = addLogAgent(clusterId);
        }
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        SetFSEndpointRequest request = new SetFSEndpointRequest();
        request.setFsType(fsType);
        request.setEndpoint(endpoint);
        
        // 发起设置存储的请求 ， 通过中心侧的LogAgent转发
        String url = getCenterAgentUri() + "/api/v2/logagent/manage/fsendpoint/" + logAgent.getId();
        TextResponse response = logAgentClientService.performJsonPutTextResponseByUri(url, headers, request);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "set fsendpoint for cluster " + clusterId + " failed [ "
                + ReflectionToStringBuilder.toString(response) + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<LogAgentNode> getLogAgentNodeList(int clusterId)
    {
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        String url = getCenterAgentUri() + "/api/v2/logagent/manage/logAgent/" + clusterId + "/nodes";
        TextResponse response = logAgentClientService.performGetTextByUri(url, headers);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND.value())
        {
            return null;
        }
        else if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "get log agent nodes failed [ " + ReflectionToStringBuilder.toString(response)
                + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        return (List<LogAgentNode>) JsonUtils.stringToList(response.getResponseBody(),
            List.class,
            LogAgentNode.class);
    }
    
    /**
     * 对接一个新的LogAgent
     * 
     * @param clusterId
     * @return
     */
    private LogAgent addLogAgent(int clusterId)
    {
        AddLogAgentRequest request = new AddLogAgentRequest();
        
        request.setClusterId(clusterId);
        if (LogAgent.DEFAULT_CLUSTERID == clusterId)
        {
            // 如果集群是AC集群，则直接通过127.0.0.1访问
            request.setProtocol(acProtocol);
            request.setClusterAddress(acAddress);
            request.setPort(acPort);
            request.setServiceContextPath(serviceContextPath);
        }
        else
        {
            // 如果是DC集群，则选择一个可用的IP
            DataCenter dc = dcService.getDataCenter(clusterId);
            if (null == dc || null == dc.getResourceGroup())
            {
                String message = "dc [ " + clusterId + " ] not exist.";
                LOGGER.warn(message);
                throw new BusinessException(message);
            }
            request.setProtocol(dcProtocol);
            request.setClusterAddress(dc.getResourceGroup().getManageIp());
            request.setPort(dcPort);
            request.setServiceContextPath(serviceContextPath);
        }
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        String url = getCenterAgentUri() + "/api/v2/logagent/manage/addAgent";
        TextResponse response = logAgentClientService.performJsonPutTextResponseByUri(url, headers, request);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "add log agent failed [ " + ReflectionToStringBuilder.toString(response) + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        return JsonUtils.stringToObject(response.getResponseBody(), LogAgent.class);
    }
    
    @Override
    public boolean isMergeDC(int clusterId)
    {
        // 如果是DC集群，则选择一个可用的IP
        DataCenter dc = dcService.getDataCenter(clusterId);
        
        return isMergeDC(dc);
    }
    
    @Override
    public List<DataCenter> listDataCenterWithoutMergeDC(int regionId)
    {
        List<DataCenter> all = dcService.listDataCenter(regionId);
        
        if (null == all)
        {
            return new ArrayList<DataCenter>(0);
        }
        
        List<DataCenter> dssList = new ArrayList<DataCenter>(all.size());
        
        for (DataCenter d : all)
        {
            if (!isMergeDC(d))
            {
                dssList.add(d);
            }
        }
        
        return dssList;
    }
    
    private boolean isMergeDC(DataCenter dc)
    {
        if (null == dc || null == dc.getResourceGroup())
        {
            String message = "dc not exist.";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        
        // 如果是DC集群，则选择一个可用的IP
        ResourceGroup resourceGroup = dc.getResourceGroup();
        return resourceGroup.getType() == ResourceGroupType.Merge.getValue();
    }
}
