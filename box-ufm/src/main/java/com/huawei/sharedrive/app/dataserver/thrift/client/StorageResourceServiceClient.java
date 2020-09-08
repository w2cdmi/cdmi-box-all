/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.dataserver.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageResouceThriftServiceOnDss;

import pw.cdmi.common.thrift.client.BaseThriftClient;

/**
 * DC提供给APP的Thrift接口
 * 
 * @author d00199602
 *         
 */
public class StorageResourceServiceClient extends BaseThriftClient
    implements StorageResouceThriftServiceOnDss.Iface
{
    private StorageResouceThriftServiceOnDss.Client client;
    
    public StorageResourceServiceClient(String serverIp, int serverPort) throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT,
            ThriftBaseParamter.STORAGE_RESOUCE_THRIFT_SERVICE);
        client = new StorageResouceThriftServiceOnDss.Client(getProtocol());
        this.open();
    }
    
    @Override
    public String addStorageResource(StorageInfo storageInfo) throws TException
    {
        return client.addStorageResource(storageInfo);
    }
    
    @Override
    public void changeStorageResource(StorageInfo storageInfo) throws TException
    {
        client.changeStorageResource(storageInfo);
    }
    
    @Override
    public void deleteStorageResource(String id) throws TException
    {
        client.deleteStorageResource(id);
    }
    
    @Override
    public void disableStorageResource(String id) throws TException
    {
        client.disableStorageResource(id);
    }
    
    @Override
    public void enableStorageResource(String id) throws TException
    {
        client.enableStorageResource(id);
    }
    
    @Override
    public List<StorageInfo> getAllStorageResource() throws TException
    {
        return client.getAllStorageResource();
    }
    
    @Override
    public StorageInfo getStorageResource(String id) throws TException
    {
        return client.getStorageResource(id);
    }
    
}
