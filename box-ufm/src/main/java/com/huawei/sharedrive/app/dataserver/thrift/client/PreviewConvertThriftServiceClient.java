package com.huawei.sharedrive.app.dataserver.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.plugins.preview.convert.PreviewConvertThriftService;
import com.huawei.sharedrive.thrift.plugins.preview.convert.PreviewConvertThriftService.Iface;

import pw.cdmi.common.thrift.client.BaseThriftClient;

import com.huawei.sharedrive.thrift.plugins.preview.convert.TConvertTask;

public class PreviewConvertThriftServiceClient extends BaseThriftClient implements Iface
{
    private static final String THRIFT_SERVICE_NAME = "PreviewConvertThriftService";
    
    private PreviewConvertThriftService.Client client;
    
    public PreviewConvertThriftServiceClient(String serverIp, int serverPort) throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, THRIFT_SERVICE_NAME);
        client = new PreviewConvertThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public void addTask(TConvertTask task) throws TException
    {
        client.addTask(task);
    }
    
}
