package com.huawei.sharedrive.app.dataserver.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.plugins.agent.PluginServiceAgentThriftService;
import com.huawei.sharedrive.thrift.plugins.agent.PluginServiceAgentThriftService.Iface;

import pw.cdmi.common.thrift.client.BaseThriftClient;

import com.huawei.sharedrive.thrift.plugins.agent.TAccessKey;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

public class PluginServiceClusterThriftServiceClient extends BaseThriftClient implements Iface
{
    
    public static final String PREVIEW_AGENT = "PreviewConvertPluginThriftService";
    
    public static final String KIA_AGENT = "SecurityScanAgentThriftService";
    
    private PluginServiceAgentThriftService.Client client;
    
    public PluginServiceClusterThriftServiceClient(String serverIp, int serverPort, String serviceName)
        throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, serviceName);
        client = new PluginServiceAgentThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public List<TWorkerNode> getWrokerList() throws TException
    {
        // TODO Auto-generated method stub
        return client.getWrokerList();
    }
    
    @Override
    public void setAccessKey(TAccessKey accessKey) throws TException
    {
        client.setAccessKey(accessKey);
        
    }
    
    public static String isKiA(PluginServiceCluster pluginServiceCluster)
    {
        Boolean isKiA = pluginServiceCluster.isAppKIA();
        if (isKiA)
        {
            return PluginServiceClusterThriftServiceClient.KIA_AGENT;
        }
        return PluginServiceClusterThriftServiceClient.PREVIEW_AGENT;
    }
}
