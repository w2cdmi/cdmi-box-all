package com.huawei.sharedrive.app.plugins.scan.manager;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.SecurityScanThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.ClusterNotFoundException;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginServiceClusterService;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTaskParser;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.thrift.plugins.scan.TSecurityScanTask;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;

public class SecurityScanTaskListener implements MessageListener
{
    private static final String SCAN_APP_ID = Constants.APPID_SECURITYSCAN;
    
    private static final int THRIFT_TIME_OUT = Integer.parseInt(PropertiesUtils.getProperty("security.scan.job.thrift.timeout",
        "5000"));
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanTaskConsumer.class);
    
    private PluginServiceClusterService pluginServiceClusterService;
    
    private ResourceGroupService resourceGroupService;
    
    private DssDomainService dssDomainService;
    
    public SecurityScanTaskListener(PluginServiceClusterService pluginServiceClusterService,
        ResourceGroupService resourceGroupService, DssDomainService dssDomainService)
    {
        this.pluginServiceClusterService = pluginServiceClusterService;
        this.resourceGroupService = resourceGroupService;
        this.dssDomainService = dssDomainService;
    }
    
    @Override
    @MethodLogAble(value = Level.INFO)
    public void onMessage(Message message)
    {
        if (message instanceof BytesMessage)
        {
            BytesMessage bytesMessage = (BytesMessage) message;
            SecurityScanThriftServiceClient client = null;
            try
            {
                byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(data);
                SecurityScanTask task = SecurityScanTaskParser.bytesToSecurityScanTask(data, 0, data.length);
                LOGGER.info("Receive a security scan task message, node id:{}, object id:{}, owner id:{}",
                    task.getNodeId(),
                    task.getObjectId(),
                    task.getOwnedBy());
                
                // 查询处理该文件所属region的cluster
                PluginServiceCluster cluster = pluginServiceClusterService.getClusterByObjectId(task.getObjectId(),
                    SCAN_APP_ID);
                if (cluster == null)
                {
                    LOGGER.error("Can not found the cluster by [{}, {}]", task.getObjectId(), SCAN_APP_ID);
                    throw new ClusterNotFoundException("Can not found the cluster");
                }
                
                // 发送任务到DSS
                ResourceGroup group = resourceGroupService.getResourceGroup(cluster.getDssId());
                String domain = dssDomainService.getDomainByDssId(group);
                TSecurityScanTask tTask = SecurityScanTaskParser.bytesToTSecurityScanTask(data,
                    0,
                    data.length);
                client = new SecurityScanThriftServiceClient(domain, group.getManagePort(), THRIFT_TIME_OUT);
                client.addTask(tTask);
            }
            catch (BusinessException e)
            {
                LOGGER.error("Consume security scan task failed!", e);
            }
            catch (Exception e)
            {
                LOGGER.error("Consume security scan task failed!", e);
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
        else
        {
            LOGGER.info("Receive a unknown message, type is " + message.getClass().getName());
        }
        
    }
    
}
