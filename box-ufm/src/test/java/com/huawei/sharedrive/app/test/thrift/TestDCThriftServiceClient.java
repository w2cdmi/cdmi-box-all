/**
 * 
 */
package com.huawei.sharedrive.app.test.thrift;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.dc2app.DCThriftService;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroup;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * @author q90003805
 *         
 */
public class TestDCThriftServiceClient extends AbstractThriftClient implements DCThriftService.Iface
{
    private final static String SERVICE_NAME = "DCThriftService";
    
    private DCThriftService.Client client;
    
    public TestDCThriftServiceClient(TTransport transport)
    {
        super(transport, SERVICE_NAME);
        client = new DCThriftService.Client(getProtocol());
    }
    
    @Override
    public void active() throws TBusinessException, TException
    {
        client.active();
    }
    
    @Override
    public void changeSystemConfig(String arg0, String arg1, String arg2)
        throws TBusinessException, TException
    {
        client.changeSystemConfig(arg0, arg1, arg2);
    }
    
    @Override
    public ResourceGroup getResourceGroupInfo() throws TBusinessException, TException
    {
        return client.getResourceGroupInfo();
    }
    
    @Override
    public List<ResourceGroupNode> getResourceGroupNodeList() throws TBusinessException, TException
    {
        return client.getResourceGroupNodeList();
    }
    
    @Override
    public ResourceGroup init(int arg0, String arg1, int arg2, String arg3, String arg4)
        throws TBusinessException, TException
    {
        return client.init(arg0, arg1, arg2, arg3, arg4);
    }
    
    @Override
    public void reset() throws TBusinessException, TException
    {
        client.reset();
    }
    
}
