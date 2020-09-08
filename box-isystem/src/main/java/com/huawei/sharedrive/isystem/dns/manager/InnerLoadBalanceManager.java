package com.huawei.sharedrive.isystem.dns.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.monitor.domain.ServiceNode;
import pw.cdmi.common.monitor.manager.MonitorLocalCacheConsumer;
import pw.cdmi.common.monitor.service.ServiceNodeService;

@Service("innerLoadBalanceManager")
public class InnerLoadBalanceManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InnerLoadBalanceManager.class);
    
    private static final String SYS_INNER_LOADBLANCE_CONFIG = "system.inner.loadbalance.enable";
    
    private static final String SYS_INNER_LOADBLANCE_TRY_COUNTS = "system.inner.loadbalance.try.counts";
    
    private final static String THRIFT_IP = PropertiesUtils.getProperty("thrift.app.ip");
    
    @Autowired
    private MonitorLocalCacheConsumer monitorLocalCacheConsumer;
    
    
    @Autowired
    private ServiceNodeService serviceNodeService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    public String getBestUFMIp()
    {
        if (isSysInnerLoadblanceConfig())
        {
           ServiceNode node =  monitorLocalCacheConsumer.getBestUFMService();
           if(null != node)
           {
               LOGGER.info("inner loadBalance,get ip:"+node.getInnerAddr());
               return node.getInnerAddr();
           }
        }
        
        return THRIFT_IP;
    }
    
    /**
     * 重试次数，如果是内部负载均衡才使用
     * 
     * @return
     */
    public int getTryCounts()
    {
        if (isSysInnerLoadblanceConfig())
        {
            
            SystemConfig config = systemConfigDAO.get(SYS_INNER_LOADBLANCE_TRY_COUNTS);
            if (null == config)
            {
                List<SystemConfig> configList = systemConfigDAO.getByPrefix(null, SYS_INNER_LOADBLANCE_CONFIG);
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
     * 获取是否开启负载均衡
     * 
     * @return
     */
    public boolean isSysInnerLoadblanceConfig()
    {
        SystemConfig config = systemConfigDAO.get(SYS_INNER_LOADBLANCE_CONFIG);
        if (null == config)
        {
            List<SystemConfig> configList = systemConfigDAO.getByPrefix(null, SYS_INNER_LOADBLANCE_CONFIG);
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
    
    /**
     * 更新UFM节点
     * 
     * @param groupId
     * @param ip
     */
    public void updateUFMNodeByInnerIp(String ip)
    {
        if (isSysInnerLoadblanceConfig())
        {
            //
            ServiceNode node= serviceNodeService.getNodeByInnerAdd(ip);
            if(null != node)
            {
                //需要查询出来，使用管理IP
                monitorLocalCacheConsumer.updateNodeForThriftFailed(ServiceNode.CLUSTER_TYPE_UFM, 0, node.getManagerIp());
            }
            
          
        }
    }
    
}
