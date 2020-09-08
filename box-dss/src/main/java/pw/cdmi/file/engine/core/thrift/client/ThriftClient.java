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
package pw.cdmi.file.engine.core.thrift.client;

import java.io.Closeable;

import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.common.thrift.client.BaseThriftClient;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;

/**
 * 
 * @author s90006125
 * 
 */
public abstract class ThriftClient implements Closeable
{
    
    private ThriftClientPool pool;
    
    private BaseThriftClient baseThriftClient;
    
    @Autowired
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    protected ThriftClient(String serverIp, int serverPort, int timeOut, String serverName,
        ThriftClientPool pool) throws TTransportException
    {
        try
        {
            baseThriftClient = new BaseThriftClient(serverIp, serverPort, timeOut, serverName);
            this.pool = pool;
        }
        catch (TTransportException e)
        {
            if (null == innerLoadBalanceManager)
            {
                innerLoadBalanceManager = (InnerLoadBalanceManager) BeanHolder.getBean("innerLoadBalanceManager");
            }
            innerLoadBalanceManager.updateNode(serverIp);
            throw e;
        }
    }
    
    public void open() throws TTransportException
    {
        baseThriftClient.open();
    }
    
    @Override
    public void close()
    {
        if (baseThriftClient.isUseSSL())
        {
            realClose();
        }
        else
        {
            this.pool.returnObject(this);
        }
        
    }
    
    public void realClose()
    {
        baseThriftClient.close();
    }
    
    public boolean validate()
    {
        return baseThriftClient.validate();
    }
    
    protected TProtocolDecorator getProtocol()
    {
        return baseThriftClient.getProtocol();
    }
}
