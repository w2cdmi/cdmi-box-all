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
package pw.cdmi.file.engine.manage.datacenter.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;
import pw.cdmi.file.engine.filesystem.support.service.FSEndpointService;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.manage.datacenter.dao.DCDao;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.manage.datacenter.domain.DcConfig;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroupType;
import pw.cdmi.file.engine.manage.datacenter.domain.RuntimeStatus;
import pw.cdmi.file.engine.manage.datacenter.service.DCService;

/**
 * 
 * @author s90006125
 * 
 */
@Service("dcService")
public class DCServiceImpl implements DCService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCServiceImpl.class);
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private FSEndpointService fsEndpointService;
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    private static final String DEFAULT_ACCESS_PROTOCOL = "https";
    
    private static final int DEFAULT_DATACENTER_ID = -1;
    
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @MethodLogAble(Level.INFO)
    public ResourceGroup init(int dcid, 
        String reportip, 
        int reportport, 
        String getProtocol, 
        String putProtocol)
    {
        if (SystemConfigContainer.getBoolean(SystemConfigKeys.SYSTEM_DATACENTER_BEEN_INITIALIZED, false))
        {
            LOGGER.warn("DataCenter Already Init.");
            // 如果已经初始化，就不变更ID
            dcid = SystemConfigContainer.getInteger(SystemConfigKeys.SYSTEM_DATACENTER_ID, dcid);
        }
        DcConfig dcConfig = new DcConfig(dcid, reportip, reportport, getProtocol, putProtocol);
        updateDCConfig(true, dcConfig);
        return getResourceGroup();
    }
    
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @MethodLogAble(Level.INFO)
    public void reset()
    {
        DcConfig dcConfig = new DcConfig(null, null, -1, DEFAULT_ACCESS_PROTOCOL, DEFAULT_ACCESS_PROTOCOL);
        updateDCConfig(false,dcConfig);
    }
    
    @Override
    @MethodLogAble(Level.INFO)
    public void active()
    {
        List<FSEndpoint> allEndpoints = fsEndpointService.getAllFSEndpointsForCurrentDevice();
        if (null == allEndpoints || allEndpoints.isEmpty())
        {
            String message = "FSEndpoint Is Null, Cann't Active.";
            LOGGER.warn(message);
            throw new InnerException(message);
        }
        
        for(FSEndpoint endpoint : allEndpoints)
        {
            // 只要有任意一个存储启用了，就可以激活DC
            if(FSEndpointStatus.ENABLE == endpoint.getStatus())
            {
                return;
            }
        }
        
        String message = "FSEndpoint Is Enable, Cann't Active DC.";
        LOGGER.warn(message);
        throw new InnerException(message);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceGroup getResourceGroup()
    {
        ResourceGroup group = new ResourceGroup();
        group.setId(getDCID());
        group.setServiceHttpPort(SystemConfigContainer.getInteger(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_PORT_HTTP,
            8080));
        group.setServiceHttpsPort(SystemConfigContainer.getInteger(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_PORT_HTTPS,
            8443));
        group.setServicePath(SystemConfigContainer.getConfigValue(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_PATH));
        group.setGetProtocol(SystemConfigContainer.getConfigValue(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_GET_PROTOCOL));
        group.setPutProtocol(SystemConfigContainer.getConfigValue(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_PUT_PROTOCOL));
        group.setNodes(getClusterNodeList());
        String dcType = SystemConfigContainer.getString(SystemConfigKeys.SYSTEM_DATACENTER_TYPE,
            ResourceGroupType.Merge.getType());
        
        ResourceGroupType type = ResourceGroupType.getResourceGroupType(dcType);
        if(null == type)
        {
            String message = "cann't find groupType by type [ " + dcType +" ].";
            LOGGER.warn(message);
            throw new InnerException(message);
        }
        
        group.setType(type.getValue());
        return group;
    }
    
    @Override
    public ResourceGroup getAndRefreshStatus()
    {
        ResourceGroup group = getResourceGroup();
        
        checkNodes(group.getNodes());
        
        return group;
    }
    
    @Override
    public List<ClusterNode> getClusterNodeList()
    {
        return dcDao.selectAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateNodeStatus(ClusterNode node)
    {
        dcDao.update(node);
    }
    
    @Override
    public Integer getDCID()
    {
        String id = SystemConfigContainer.getConfigValue(SystemConfigKeys.SYSTEM_DATACENTER_ID);
        if (StringUtils.isBlank(id))
        {
            return DEFAULT_DATACENTER_ID;
        }
        return Integer.valueOf(id);
    }
    
    @Override
    public ClusterNode getClusterNode(String name)
    {
        return dcDao.get(new ClusterNode(name));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveClusterNode(ClusterNode node)
    {
        dcDao.create(node);
    }
    
    @Override
    public String getReportAddr()
    {
        return SystemConfigContainer.getConfigValue(SystemConfigKeys.SYSTEM_DATACENTER_REPORT_ADDR);
    }
    
    @Override
    public int getReportPort()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.SYSTEM_DATACENTER_REPORT_PORT, -1);
    }
    
    private void updateDCConfig(boolean beenInitialized, DcConfig dcConfig)
    {
        String logMessage = new StringBuffer("update dc config to [beenInitialized:")
            .append(beenInitialized)
            .append(", dcid:").append(dcConfig.getDataserverCenterId())
            .append(", reportip:").append(dcConfig.getReportip())
            .append(", reportport:").append(dcConfig.getReportport())
            .append(", getProtocol:").append(dcConfig.getGetProtocol())
            .append(", putProtocol:").append(dcConfig.getPutProtocol()).toString();
        
        LOGGER.info(logMessage);
        
        SystemConfig initConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_DATACENTER_BEEN_INITIALIZED);
        SystemConfig dcidConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_DATACENTER_ID);
        SystemConfig reportIpConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_DATACENTER_REPORT_ADDR);
        SystemConfig reportPortConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_DATACENTER_REPORT_PORT);
        SystemConfig getProtocolConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_GET_PROTOCOL);
        SystemConfig putProtocolConf = SystemConfigContainer.getConfig(SystemConfigKeys.SYSTEM_ENVIRONMENT_SERVICE_PUT_PROTOCOL);
        
        initConf.setValue(String.valueOf(beenInitialized));
        
        dcidConf.setValue(null == dcConfig.getDataserverCenterId() ? "" : dcConfig.getDataserverCenterId().toString());
        reportIpConf.setValue(StringUtils.trimToEmpty(dcConfig.getReportip()));
        reportPortConf.setValue(null == dcConfig.getReportport() ? "" : dcConfig.getReportport().toString());
        getProtocolConf.setValue(StringUtils.trimToEmpty(dcConfig.getGetProtocol()));
        putProtocolConf.setValue(StringUtils.trimToEmpty(dcConfig.getPutProtocol()));
        
        // 修改系统配置
        systemConfigManager.changeSystemConfigs(initConf,
            dcidConf,
            reportIpConf,
            reportPortConf,
            getProtocolConf,
            putProtocolConf);
    }
    
    private void checkNodes(List<ClusterNode> nodes)
    {
        long timeOut = SystemConfigContainer.getLong(SystemConfigKeys.SYSTEM_ENVIRONMENT_CLUSTER_NODE_TIMEOUT,
            30 * 1000L);
        long currentTime = System.currentTimeMillis();
        
        for (ClusterNode node : nodes)
        {
            if (currentTime - node.getLastReportTime() > timeOut)
            {
                // 如果超时，则设置为离线状态
                LOGGER.warn("Node [ " + node + " ] Was Offline.");
                node.setRuntimeStatus(RuntimeStatus.Offline);
            }
        }
    }
}
