package com.huawei.sharedrive.app.dataserver.thrift.client;

import org.apache.thrift.TException;

import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.dc2app.FileObjectThriftService;

import pw.cdmi.common.thrift.client.BaseThriftClient;

public class FileObjectThriftServiceClient extends BaseThriftClient implements FileObjectThriftService.Iface
{
    
    private static final String DC_OBJECT_SERVICE_NAME = "FileObjectThriftService";
    
    private FileObjectThriftService.Client client;
    
    public FileObjectThriftServiceClient(String serverIp, int serverPort) throws TException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, DC_OBJECT_SERVICE_NAME);
        client = new FileObjectThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public boolean deleteFileObject(String objectID) throws TException
    {
        return client.deleteFileObject(objectID);
    }
}
