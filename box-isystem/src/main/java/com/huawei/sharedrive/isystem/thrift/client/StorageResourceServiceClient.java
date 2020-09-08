package com.huawei.sharedrive.isystem.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.filesystem.StorageInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageResouceThriftServiceOnUfm;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * 
 * @author d00199602
 *         
 */
public class StorageResourceServiceClient extends AbstractThriftClient
    implements StorageResouceThriftServiceOnUfm.Iface
{
    
    private StorageResouceThriftServiceOnUfm.Client client;
    
    public StorageResourceServiceClient(TTransport transport)
    {
        super(transport, "storageResourceService");
        client = new StorageResouceThriftServiceOnUfm.Client(getProtocol());
    }
    
    @Override
    public List<StorageInfo> getAllStorageResource(int dcId) throws TException
    {
        return client.getAllStorageResource(dcId);
    }
    
    @Override
    public String addStorageResource(int dcId, StorageInfo storageInfo) throws TException
    {
        return client.addStorageResource(dcId, storageInfo);
    }
    
    @Override
    public void changeStorageResource(int dcId, StorageInfo storageInfo) throws TException
    {
        client.changeStorageResource(dcId, storageInfo);
    }
    
    @Override
    public void deleteStorageResource(int dcId, String storageResId) throws TException
    {
        client.deleteStorageResource(dcId, storageResId);
    }
    
    @Override
    public void disableStorageResource(int dcId, String storageResId) throws TException
    {
        client.disableStorageResource(dcId, storageResId);
    }
    
    @Override
    public void enableStorageResource(int dcId, String storageResId) throws TException
    {
        client.enableStorageResource(dcId, storageResId);
    }
    
    @Override
    public StorageInfo getStorageResource(int dcId, String storageResId) throws TException
    {
        return client.getStorageResource(dcId, storageResId);
    }
    
}
