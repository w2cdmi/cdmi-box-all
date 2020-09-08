package com.huawei.sharedrive.app.dns.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.monitor.domain.ServiceNode;
import pw.cdmi.common.monitor.manager.MonitorLocalCacheConsumer;
import pw.cdmi.core.utils.SpringContextUtil;


@Service("innerLoadBalanceManager")
public class InnerLoadBalanceManager
{
    @Autowired
    private MonitorLocalCacheConsumer monitorLocalCacheConsumer;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;

    
    private static final String SYS_INNER_LOADBLANCE_CONFIG="system.inner.loadbalance.enable";
    
    private static final String SYS_INNER_LOADBLANCE_TRY_COUNTS = "system.inner.loadbalance.try.counts";
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InnerLoadBalanceManager.class);
    
    /**
     * 获取是否开启负载均衡
     * @return
     */
    public boolean isSysInnerLoadblanceConfig()
    { 
         try
         {
             SystemConfig config = systemConfigService.getConfig(SYS_INNER_LOADBLANCE_CONFIG);
             if (null == config)
             {
                 List<SystemConfig> configList = systemConfigService.getByPrefix(null, SYS_INNER_LOADBLANCE_CONFIG);
                 if (null != configList && !configList.isEmpty())
                 {
                     config = configList.get(0);
                 }
             }
             
             if (null != config)
             {
                 
                 return Boolean.parseBoolean(config.getValue());
             }
             return false;
         }
         catch (Exception e)
         {
             LOGGER.error(e.getMessage(),e);
             return false;
         }
         
     }
    
    /**
     * 重试次数，如果是内部负载均衡才使用
     * @return
     */
    public  int getTryCounts()
    {
        if (isSysInnerLoadblanceConfig())
        {
            
            
            SystemConfig config = systemConfigService.getConfig(SYS_INNER_LOADBLANCE_TRY_COUNTS);
            if (null == config)
            {
                List<SystemConfig> configList = systemConfigService.getByPrefix(null, SYS_INNER_LOADBLANCE_CONFIG);
                if (null != configList && !configList.isEmpty())
                {
                    config = configList.get(0);
                }
            }
            if (null != config)
            {
                return Integer.parseInt(config.getValue());
            }
       
        }
        
        return 1;
    }
    

    
    /**
     * 更新DSS节点
     * @param groupId
     * @param ip
     */
    public  void updateDssNode(String ip)
    {
        if (isSysInnerLoadblanceConfig())
        {
            if (null == monitorLocalCacheConsumer)
            {
                monitorLocalCacheConsumer = (MonitorLocalCacheConsumer) SpringContextUtil.getBean("monitorLocalCacheConsumer");
            }
            
            ResourceGroupNode node =  resourceGroupService.getResourceGroupNodeByManagerIp(ip);
            if(null == node)
            {
                LOGGER.warn("not found group node,ip:"+ip);
                return;
            }
            
            monitorLocalCacheConsumer.updateNodeForThriftFailed(ServiceNode.CLUSTER_TYPE_DSS,node.getResourceGroupId(),ip);
        }
       
    }
    
    /**
     * 获取DSS IP
     * @param groupId
     * @return
     */
    public String getBestDSSServiceByGroupId(int groupId)
    {
        ServiceNode node = monitorLocalCacheConsumer.getBestDSSServiceByGroupId(groupId);
        if(null != node)
        {
            LOGGER.info("inner loadBalance,get ip:"+node.getManagerIp());
            return node.getServiceAddr();
        }    
        return null;
    }
    
    
   
    
    
}
