package com.huawei.sharedrive.isystem.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.app2isystem.DCThriftService;
import com.huawei.sharedrive.thrift.app2isystem.DCThriftService.Iface;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

import com.huawei.sharedrive.thrift.app2isystem.ResourceGroup;
import com.huawei.sharedrive.thrift.app2isystem.ResourceGroupCreateInfo;
import com.huawei.sharedrive.thrift.app2isystem.ResourceGroupNode;
import com.huawei.sharedrive.thrift.app2isystem.TBusinessException;

/**//**
     * 
     * @author d00199602
     *         
     */
public class DCManageServiceClient extends AbstractThriftClient implements Iface
{
    
    private DCThriftService.Client client;
    
    public DCManageServiceClient(TTransport transport)
    {
        super(transport, "dcService");
        client = new DCThriftService.Client(getProtocol());
    }
    
    @Override
    public void activeResourceGroup(int dcid) throws TException
    {
        client.activeResourceGroup(dcid);
    }
    
    @Override
    public void addResourceGroup(ResourceGroupCreateInfo createInfo) throws TException
    {
        client.addResourceGroup(createInfo);
    }
    
    @Override
    public void deleteResourceGroup(int dcid) throws TException
    {
        client.deleteResourceGroup(dcid);
    }
    
    @Override
    public void modifyResourceGroup(int dcid, String domainName) throws TException
    {
        client.modifyResourceGroup(dcid, domainName);
    }
    
    @Override
    public List<ResourceGroup> getResourceGroupList(int dcid) throws TBusinessException, TException
    {
        return client.getResourceGroupList(dcid);
    }
    
    @Override
    public List<ResourceGroupNode> getResourceGroupNodeList(int dcid) throws TBusinessException, TException
    {
        return client.getResourceGroupNodeList(dcid);
    }
    
}