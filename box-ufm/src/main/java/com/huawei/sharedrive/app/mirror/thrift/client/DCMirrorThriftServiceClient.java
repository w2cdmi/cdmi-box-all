package com.huawei.sharedrive.app.mirror.thrift.client;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.thrift.mirror.dc2app.CopyTaskInfo;
import com.huawei.sharedrive.thrift.mirror.dc2app.DCMirrorThriftService;

import pw.cdmi.common.thrift.client.BaseThriftClient;

public class DCMirrorThriftServiceClient extends BaseThriftClient implements DCMirrorThriftService.Iface
{
    
    private static final String DC_MIRROR_THRIFT_NAME = "DCMirrorThriftService";
    
    private DCMirrorThriftService.Client client;
    
    public DCMirrorThriftServiceClient(String serverIp, int serverPort) throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, DC_MIRROR_THRIFT_NAME);
        client = new DCMirrorThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public void addCopyTask(CopyTaskInfo task) throws TException
    {
        client.addCopyTask(task);
        
    }
    
    @Override
    public void batchAddCopyTask(List<CopyTaskInfo> lstTask) throws TException
    {
        client.batchAddCopyTask(lstTask);
    }
    
    @Override
    public void managerCopyTask(int state, String taskId) throws TException
    {
        client.managerCopyTask(state, taskId);
    }
    
    @Override
    public Map<String, String> getTaskExeInfo() throws TException
    {
        return client.getTaskExeInfo();
    }
    
}
