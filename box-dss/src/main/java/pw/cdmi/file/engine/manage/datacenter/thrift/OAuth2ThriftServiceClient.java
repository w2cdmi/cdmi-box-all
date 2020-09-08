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

import com.huawei.sharedrive.thrift.app2dc.OAuth2ThriftService;
import com.huawei.sharedrive.thrift.app2dc.OAuth2ThriftService.Iface;
import com.huawei.sharedrive.thrift.app2dc.TBusinessException;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

import com.huawei.sharedrive.thrift.app2dc.TokenAuthVaild;

/**
 * 
 * @author s90006125
 *         
 */
public class OAuth2ThriftServiceClient extends AbstractThriftClient implements Iface
{
    
    private OAuth2ThriftService.Client client;
    
    protected OAuth2ThriftServiceClient(TTransport transport)
    {
        super(transport, "OAuth2ThriftService");
        this.client = new OAuth2ThriftService.Client(getProtocol());
    }
    
    @Override
    public String checkTokenAuthVaild(TokenAuthVaild tokenInfo) throws TException
    {
        return this.client.checkTokenAuthVaild(tokenInfo);
    }

	@Override
	public String checkDataTokenAuthVaild(TokenAuthVaild tokenInfo) throws TBusinessException, TException {
		// TODO Auto-generated method stub
		return this.client.checkDataTokenAuthVaild(tokenInfo);
	}

	@Override
	public String deleteDataTokenAuthVaild(TokenAuthVaild tokenInfo) throws TBusinessException, TException {
		// TODO Auto-generated method stub
		return this.client.deleteDataTokenAuthVaild(tokenInfo);
	}
}
