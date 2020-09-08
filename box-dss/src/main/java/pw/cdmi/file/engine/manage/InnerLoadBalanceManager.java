package pw.cdmi.file.engine.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.monitor.domain.ServiceNode;
import pw.cdmi.common.monitor.manager.MonitorLocalCacheConsumer;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;

@Service("innerLoadBalanceManager")
public class InnerLoadBalanceManager
{
    @Autowired
    private MonitorLocalCacheConsumer monitorLocalCacheConsumer;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InnerLoadBalanceManager.class);
    /**
     * 重试次数，如果是内部负载均衡才使用
     * @return
     */
    public static int getTryCounts()
    {
        if (isGetSysInnerLoadblanceConfig())
        {
            SystemConfigContainer.getInteger(SystemConfigKeys.SYS_INNER_LOADBLANCE_TRY_COUNTS, 3);
        }
        return 1;
    }
    
    /**
     * 
     * @return
     */
    public static boolean isGetSysInnerLoadblanceConfig()
    {
        return SystemConfigContainer.getBoolean(SystemConfigKeys.SYS_INNER_LOADBLANCE_CONFIG, false);
    }
    

    public  void updateNode(String ip)
    {
        if (isGetSysInnerLoadblanceConfig())
        {
            if (null == monitorLocalCacheConsumer)
            {
                monitorLocalCacheConsumer = (MonitorLocalCacheConsumer) BeanHolder.getBean("monitorLocalCacheConsumer");
            }
            monitorLocalCacheConsumer.updateNodeForThriftFailed(ServiceNode.CLUSTER_TYPE_UFM,0,ip);
        }
       
    }
    
    
    public String  getUFMIp()
    {

        if (isGetSysInnerLoadblanceConfig())
        {
            if (null == monitorLocalCacheConsumer)
            {
                monitorLocalCacheConsumer = (MonitorLocalCacheConsumer) BeanHolder.getBean("monitorLocalCacheConsumer");
            }
            
            ServiceNode node = monitorLocalCacheConsumer.getBestUFMService();
            if (null != node)
            {
                LOGGER.info("inner loadBalance,get ip:"+node.getManagerIp());
                return node.getManagerIp();
            }
            LOGGER.warn("inner loadBalance,node null ");
        }
        else
        {
            LOGGER.info("inner loadBalance enable:"+isGetSysInnerLoadblanceConfig());
        }
        
        return null;
    }
    
    
}
