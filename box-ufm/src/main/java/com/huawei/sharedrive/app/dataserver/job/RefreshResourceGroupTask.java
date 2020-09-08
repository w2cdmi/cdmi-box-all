package com.huawei.sharedrive.app.dataserver.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.service.impl.ResourceGroupComparer;
import com.huawei.sharedrive.app.dataserver.thrift.client.DCThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.utils.SpringContextUtil;

public class RefreshResourceGroupTask extends Task
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshResourceGroupTask.class);
    
    // 任务对象
    private ResourceGroup resourceGroup;
    
    private ResourceGroupService resourceGroupService;
    
    private DCManager dcManager;
    
    private ConfigManager configManager;
    
    private DssDomainService dssDomainService;
    
    private long resourceGroupTimeout;
    
    public RefreshResourceGroupTask(ResourceGroup resourceGroup, long timeout)
    {
        this.resourceGroup = resourceGroup;
        this.resourceGroupTimeout = timeout;
        dcManager = (DCManager) SpringContextUtil.getBean("dcManager");
        resourceGroupService = (ResourceGroupService) SpringContextUtil.getBean("resourceGroupService");
        configManager = (ConfigManager) SpringContextUtil.getBean("appConfigManager");
        dssDomainService = (DssDomainService) SpringContextUtil.getBean("dssDomainService");
    }
    
    @Override
    public void execute()
    {
        try
        {
            refresh();
        }
        catch (Exception e)
        {
            LOGGER.error("Executing RefreshResourceGroupTask failed. ", e);
        }
    }
    
    private void refresh()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Start Refresh ResourceGroup Status.");
        }
        
        boolean hasChange = false;
        long currentTime = System.currentTimeMillis();
        ResourceGroup newInfo = getResourceGroupInfoFromDC(resourceGroup);
        
        if (null != newInfo)
        {
            // 如果主动获取可以获取到，则认为没有离线
            LOGGER.info("Can Get ResourceGroup Info, It's not Offline.");
            hasChange = resourceGroupService.handleReport(newInfo);
        }
        else
        {
            // 如果资源组之前状态不为离线状态，且长期未上报，就将状态设置为离线
            if (com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RuntimeStatus.Offline != resourceGroup
                .getRuntimeStatus() && currentTime - resourceGroup.getLastReportTime() > resourceGroupTimeout)
            {
                // 如果超时，则设置为离线状态
                LOGGER.warn("ResourceGroup [ " + resourceGroup.getId() + " ] Offline [ " + currentTime + "; "
                    + resourceGroup.getLastReportTime() + "; " + resourceGroupTimeout + " ] ");
                    
                resourceGroup.setRuntimeStatus(
                    com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RuntimeStatus.Offline);
                hasChange = true;
                newInfo = resourceGroup;
                resourceGroupService.updateRuntimeStatus(resourceGroup.getId(),
                    resourceGroup.getRuntimeStatus());
            }
            
        }
        // 通知集群
        if (hasChange && newInfo != null)
        {
            LOGGER.info("ResourceGroup Has Chanage, Need notify.");
            configManager.setConfig(DCManager.CONFIG_ZOOKEEPER_KEY_DC_CHANGE, newInfo);
        }
        
        ResourceGroup cacheData = dcManager.getCacheResourceGroup(resourceGroup.getId());
        if (null == cacheData)
        {
            hasChange = true;
        }
        else
        {
            ResourceGroupComparer comparer = new ResourceGroupComparer(resourceGroup, cacheData);
            hasChange = comparer.isHasChange();
        }
        // 刷新缓存
        if (hasChange)
        {
            LOGGER.info("ResourceGroup Has Chanage, Need RefreshCache.");
            dcManager.reloadCache();
        }
    }
    
    /**
     * 主动去DC获取详细信息
     * 
     * @param group
     * @return
     */
    private ResourceGroup getResourceGroupInfoFromDC(ResourceGroup group)
    {
        // 循环遍历所有DC节点，只要其中任意一个节点获取成功，则无需继续
        com.huawei.sharedrive.thrift.dc2app.ResourceGroup resourceGroup = null;
        String domain = dssDomainService.getDomainByDssId(group);
        DCThriftServiceClient client = null;
        try
        {
            client = new DCThriftServiceClient(domain, group.getManagePort());
            resourceGroup = client.getResourceGroupInfo();
            return transToLoaclObj(resourceGroup);
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Get ResourceGroup Info From  [" + domain + " ] Failed.", e);
        }
        catch (Exception e)
        {
            LOGGER.warn("Get ResourceGroup Info From  [" + domain + " ] Failed.", e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
        
        return null;
    }
    
    private ResourceGroup transToLoaclObj(com.huawei.sharedrive.thrift.dc2app.ResourceGroup tgroup)
    {
        ResourceGroup group = new ResourceGroup();
        group.setId(tgroup.getId());
        group.setServicePath(tgroup.getServicePath());
        group.setServiceHttpPort(tgroup.getServiceHttpPort());
        group.setServiceHttpsPort(tgroup.getServiceHttpsPort());
        group.setGetProtocol(tgroup.getGetProtocol());
        group.setPutProtocol(tgroup.getPutProtocol());
        group.setType(tgroup.getType());
        
        long reportTime = System.currentTimeMillis();
        group.setLastReportTime(reportTime);
        group.setRuntimeStatus(ResourceGroup.RuntimeStatus.parseStatus(tgroup.getRuntimeStatus()));
        return group;
    }
    
    @Override
    public String getName()
    {
        return "RefreshResourceGroupTask";
    }
    
}
