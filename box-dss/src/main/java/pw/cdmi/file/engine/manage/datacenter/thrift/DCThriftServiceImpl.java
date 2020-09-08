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
package pw.cdmi.file.engine.manage.datacenter.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.thrift.dc2app.DCThriftService;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroup;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.manage.datacenter.domain.RuntimeStatus;
import pw.cdmi.file.engine.manage.datacenter.service.DCService;

/**
 * 
 * @author s90006125
 * 
 */
public class DCThriftServiceImpl implements DCThriftService.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCThriftServiceImpl.class);
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    @Autowired
    private DCService dcService;
    
    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public ResourceGroup init(int dcid, String reportip, int reportport, String getProtocol,
        String putProtocol) throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        LOGGER.info("Start Init ResourceGroup [ " + dcid + ", " + reportip + ", " + reportport + ", "
            + getProtocol + ", " + putProtocol + " ]");
        try
        {
            pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup group = dcService.init(dcid,
                reportip,
                reportport,
                getProtocol,
                putProtocol);
            
            ResourceGroup tgroup = initResourceGroup(group);
            
            List<ResourceGroupNode> nodes = new ArrayList<ResourceGroupNode>(group.getNodes().size());
            ResourceGroupNode n = null;
            for (ClusterNode node : group.getNodes())
            {
                n = new ResourceGroupNode();
                n.setName(node.getName());
                n.setManagerIp(node.getManageAddr());
                n.setManagerPort(node.getManagePort());
                n.setInnerAddr(node.getInnerAddr());
                n.setServiceAddr(node.getServiceAddr());
                nodes.add(n);
            }
            tgroup.setNodes(nodes);
            return tgroup;
        }
        catch (Exception e)
        {
            LOGGER.warn("Init ResourceGroup Failed.", e);
            throw new TBusinessException(500, e.getMessage());
        }
        
    }
    
    private ResourceGroup initResourceGroup(
        pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup group)
    {
        ResourceGroup tgroup = new ResourceGroup();
        tgroup.setId(group.getId());
        tgroup.setManagerIp(group.getManagerAddr());
        tgroup.setManagerPort(group.getManagerPort());
        tgroup.setServiceHttpPort(group.getServiceHttpPort());
        tgroup.setServiceHttpsPort(group.getServiceHttpsPort());
        tgroup.setServicePath(group.getServicePath());
        tgroup.setGetProtocol(group.getGetProtocol());
        tgroup.setPutProtocol(group.getPutProtocol());
        tgroup.setType(group.getType());
        return tgroup;
    }
    
    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void reset() throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        LOGGER.info("Start reset ResourceGroup");
        try
        {
            dcService.reset();
        }
        catch (Exception e)
        {
            LOGGER.warn("Reset ResourceGroup Failed.", e);
            throw new TBusinessException(500, e.getMessage());
        }
    }
    
    /**
     * 启用资源组
     */
    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void active() throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        LOGGER.info("Start active ResourceGroup");
        try
        {
            dcService.active();
        }
        catch (Exception e)
        {
            LOGGER.warn("Active ResourceGroup Failed.", e);
            throw new TBusinessException(500, e.getMessage());
        }
    }
    
    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void changeSystemConfig(String key, String value, String desc) throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            SystemConfig sc = new SystemConfig(key, value, desc);
            systemConfigManager.changeSystemConfig(sc);
        }
        catch (Exception e)
        {
            LOGGER.warn("Change SysteConfig Failed.", e);
            throw new TBusinessException(500, e.getMessage());
        }
    }
    
    /**
     * 获取DC详情，提供给AC使用，当AC长时间未收到DC上报之后，将尝试主动发送请求到DC获取信息
     */
    @Override
    public ResourceGroup getResourceGroupInfo() throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        LOGGER.info("Ac Get Dc Info.");
        
        pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup group = dcService.getAndRefreshStatus();
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("ResourceGroup Info : " + group);
        }
        
        ResourceGroup tgroup = initResourceGroup(group);
        // 设置资源组状态
        tgroup.setRuntimeStatus(hasAbnormal(group) ? RuntimeStatus.StorageAbnormal.getCode()
            : RuntimeStatus.Normal.getCode());
        
        return tgroup;
    }
    
    /**
     * 检查是否有异常
     * 
     * @param group
     */
    private boolean hasAbnormal(pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup group)
    {
        if (null == group.getNodes())
        {
            return true;
        }
        
        // 如果其中任何一个节点有异常，则该集群状态也为异常
        for (pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode node : group.getNodes())
        {
            if (RuntimeStatus.Normal != node.getRuntimeStatus())
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public List<ResourceGroupNode> getResourceGroupNodeList() throws TBusinessException, TException
    {
        pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup group = dcService.getAndRefreshStatus();
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("ResourceGroup Info : " + group);
        }
        
        List<ResourceGroupNode> nodes = new ArrayList<ResourceGroupNode>(group.getNodes().size());
        ResourceGroupNode tempNode = null;
        for (pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode node : group.getNodes())
        {
            tempNode = new ResourceGroupNode();
            tempNode.setName(node.getName());
            tempNode.setManagerIp(node.getManageAddr());
            tempNode.setManagerPort(node.getManagePort());
            tempNode.setInnerAddr(node.getInnerAddr());
            tempNode.setServiceAddr(node.getServiceAddr());
            tempNode.setRuntimeStatus(node.getRuntimeStatus().getCode());
            nodes.add(tempNode);
        }
        return nodes;
    }
}
