package com.huawei.sharedrive.isystem.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.app2isystem.UserThriftService;
import com.huawei.sharedrive.thrift.app2isystem.UserThriftService.Iface;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * 
 * @author d00199602
 *         
 */
public class UserManageServiceClient extends AbstractThriftClient implements Iface
{
    
    private UserThriftService.Client client;
    
    public UserManageServiceClient(TTransport transport)
    {
        super(transport, "userManageService");
        client = new UserThriftService.Client(getProtocol());
    }
    
    @Override
    public long getUsedSpace(long userId) throws TException
    {
        // TODO Auto-generated method stub
        return client.getUsedSpace(userId);
    }
    
}
