/**
 * 
 */
package com.huawei.sharedrive.app.test.thrift;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.filesystem.StorageInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageResouceThriftServiceOnDss;
import com.huawei.sharedrive.thrift.filesystem.TBusinessException;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * @author q90003805
 *         
 */
public class TestStorageResourceServiceClient extends AbstractThriftClient
    implements StorageResouceThriftServiceOnDss.Iface
{
    private final static String SERVICE_NAME = "StorageResouceThriftService";
    
    private StorageResouceThriftServiceOnDss.Client client;
    
    public TestStorageResourceServiceClient(TTransport transport)
    {
        super(transport, SERVICE_NAME);
        client = new StorageResouceThriftServiceOnDss.Client(getProtocol());
    }
    
    @Override
    public String addStorageResource(StorageInfo arg0) throws TBusinessException, TException
    {
        return client.addStorageResource(arg0);
    }
    
    @Override
    public void changeStorageResource(StorageInfo arg0) throws TBusinessException, TException
    {
        client.changeStorageResource(arg0);
        
    }
    
    @Override
    public void deleteStorageResource(String arg0) throws TBusinessException, TException
    {
        client.deleteStorageResource(arg0);
    }
    
    @Override
    public void disableStorageResource(String arg0) throws TBusinessException, TException
    {
        client.disableStorageResource(arg0);
    }
    
    @Override
    public void enableStorageResource(String arg0) throws TBusinessException, TException
    {
        client.enableStorageResource(arg0);
    }
    
    @Override
    public List<StorageInfo> getAllStorageResource() throws TBusinessException, TException
    {
        
        return client.getAllStorageResource();
    }
    
    @Override
    public StorageInfo getStorageResource(String arg0) throws TBusinessException, TException
    {
        return client.getStorageResource(arg0);
    }
    
}
