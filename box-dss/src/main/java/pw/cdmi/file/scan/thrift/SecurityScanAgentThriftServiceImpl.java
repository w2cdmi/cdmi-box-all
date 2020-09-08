/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.scan.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.plugins.agent.PluginServiceAgentThriftService.Iface;
import com.huawei.sharedrive.thrift.plugins.agent.TAccessKey;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.ZookeeperUtil;
import pw.cdmi.core.zk.ZookeeperServer;

public class SecurityScanAgentThriftServiceImpl implements Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanAgentThriftServiceImpl.class);
    
    public static final String ACCESSKEY_CHANGED_KEY = "SecurityScanAppAccessKeyChanged";
    
    private ConfigManager configManager;
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private String clusterRoot;
    
    public void destroy()
    {
        if (client != null)
        {
            client.close();
        }
    }
    
    @Override
    public List<TWorkerNode> getWrokerList() throws TException
    {
        if (client.getState() == CuratorFrameworkState.STOPPED)
        {
            throw new TException("can not connect to zookeeper");
        }
        List<TWorkerNode> nodeList = new ArrayList<TWorkerNode>(10);
        try
        {
            List<String> list = client.getChildren().forPath(clusterRoot);
            String path = null;
            byte[] data = null;
            TWorkerNode node = null;
            for (String name : list)
            {
                path = clusterRoot + '/' + name;
                data = client.getData().forPath(path);
                if (data == null || data.length == 0)
                {
                    continue;
                }
                node = new TWorkerNode();
                node.setIp(name);
                node.setName(name);
                node.setStatus(data[0]);
                nodeList.add(node);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error occur when get worker list from zookeeper", e);
            throw new TException("error occur when get worker list from zookeeper",e);
        }
        return nodeList;
    }
    
    public void init() throws InnerException
    {
        try
        {
            client = zookeeperServer.getClient();
            ZookeeperUtil.safeCreateNode(client, clusterRoot, new byte[0]);
            LOGGER.info("Security scan agent init successfully");
        }
        catch(Exception e)
        {
            throw new InnerException("Error when init", e);
        }
    }
    
    @Override
    public void setAccessKey(TAccessKey accessKey) throws TException
    {
        LOGGER.info("Set accesskey {} ", accessKey.getId());
        configManager.setConfig(ACCESSKEY_CHANGED_KEY, accessKey.getId());
    }
    
    public void setClusterRoot(String clusterRoot)
    {
        this.clusterRoot = clusterRoot;
    }
    
    public void setConfigManager(ConfigManager configManager)
    {
        this.configManager = configManager;
    }
    
    public void setZookeeperServer(ZookeeperServer zookeeperServer)
    {
        this.zookeeperServer = zookeeperServer;
    }
    
}
