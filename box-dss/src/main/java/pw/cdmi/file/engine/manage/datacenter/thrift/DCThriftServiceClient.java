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
package pw.cdmi.file.engine.manage.datacenter.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.app2dc.DCThriftService;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * APP提供给DC的Thrift接口
 * 
 * @author s90006125
 *         
 */
public class DCThriftServiceClient extends AbstractThriftClient implements DCThriftService.Iface
{
    
    private DCThriftService.Client client;
    
    protected DCThriftServiceClient(TTransport transport)
    {
        super(transport, "DCThriftService");
        this.client = new DCThriftService.Client(getProtocol());
    }
    
    @Override
    public void reportStatistics(String host, int maxUpload, int maxDownload) throws TException
    {
        client.reportStatistics(host, maxUpload, maxDownload);
        
    }
}
