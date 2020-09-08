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
import com.huawei.sharedrive.thrift.dc2app.DCThriftService;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroup;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;

import pw.cdmi.common.thrift.client.BaseThriftClient;

/**
 * 用于访问DC提供给APP的Thrift接口
 * 
 * @author s90006125
 *         
 */
public class DCThriftServiceClient extends BaseThriftClient implements DCThriftService.Iface
{
    private static final String DC_THRIFT_NAME = "DCThriftService";
    
    private DCThriftService.Client client;
    
    public DCThriftServiceClient(String serverIp, int serverPort) throws TTransportException
    {
        super(serverIp, serverPort, Constants.THRIFT_DSS_SOCKET_TIMEOUT, DC_THRIFT_NAME);
        client = new DCThriftService.Client(getProtocol());
        this.open();
    }
    
    @Override
    public void active() throws TException
    {
        client.active();
    }
    
    @Override
    public void changeSystemConfig(String key, String value, String desc) throws TException
    {
        client.changeSystemConfig(key, value, desc);
    }
    
    @Override
    public ResourceGroup getResourceGroupInfo() throws TException
    {
        return client.getResourceGroupInfo();
    }
    
    @Override
    public ResourceGroup init( int dcid, String reportip,
        int reportport, String getProtocol, String putProtocol) throws TException
    {
        return client.init(dcid,
            reportip,
            reportport,
            getProtocol,
            putProtocol);
    }
    
    @Override
    public void reset() throws TException
    {
        client.reset();
    }
    
    @Override
    public List<ResourceGroupNode> getResourceGroupNodeList() throws TBusinessException, TException
    {
        return client.getResourceGroupNodeList();
    }
    
}
