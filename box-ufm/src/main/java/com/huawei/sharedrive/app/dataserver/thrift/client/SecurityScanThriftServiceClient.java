package com.huawei.sharedrive.app.dataserver.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.plugins.scan.SecurityScanThriftService;
import com.huawei.sharedrive.thrift.plugins.scan.TSecurityScanTask;

import pw.cdmi.common.thrift.client.BaseThriftClient;

public class SecurityScanThriftServiceClient extends BaseThriftClient implements
    SecurityScanThriftService.Iface
{
    private static final String THRIFT_SERVICE_NAME = "SecurityScanThriftService";
    
    private SecurityScanThriftService.Client client;
    
    public SecurityScanThriftServiceClient(String serverIp, int serverPort) throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, THRIFT_SERVICE_NAME);
        client = new SecurityScanThriftService.Client(getProtocol());
        this.open();
    }
    
    public SecurityScanThriftServiceClient(String serverIp, int serverPort, int timeout)
        throws TTransportException
    {
        super(serverIp, serverPort, timeout, THRIFT_SERVICE_NAME);
        client = new SecurityScanThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public void addTask(TSecurityScanTask task) throws TException
    {
        client.addTask(task);
    }
    
}
