package com.huawei.sharedrive.thrift.app2dc;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.thrift.client.PluginServiceClusterThriftServiceClient;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

public class PluginWorkerListThriftTest
{
    public static void main(String[] args) throws Exception
    {
        PluginServiceClusterThriftServiceClient client = null;
        try
        {
            client = new PluginServiceClusterThriftServiceClient("10.183.36.164", 12346,
                "PreviewConvertPluginThriftService");
            List<TWorkerNode> list = client.getWrokerList();
            for (TWorkerNode node : list)
            {
                System.out.println(" ip[" + node.getIp() + "] name[" + node.getName() + "] status["
                    + node.getStatus() + ']');
            }
            
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
}
