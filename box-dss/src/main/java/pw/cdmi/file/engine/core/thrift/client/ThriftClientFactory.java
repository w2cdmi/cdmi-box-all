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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroupType;

/**
 * 
 * @author s90006125
 * 
 */
public abstract class ThriftClientFactory<T extends ThriftClient> implements
    PoolableObjectFactory<ThriftClient>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftClientFactory.class);
    
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    private ThriftClientPool pool;
    
    private String serverName;
    
    public void setPool(ThriftClientPool pool)
    {
        this.pool = pool;
    }
    
    public ThriftClientPool getPool()
    {
        return pool;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    @Override
    public void activateObject(ThriftClient client) throws TTransportException
    {
        client.open();
    }
    
    @Override
    public void destroyObject(ThriftClient client)
    {
        client.realClose();
    }
    
    @Override
    public abstract T makeObject() throws TTransportException;
    
    @Override
    public void passivateObject(ThriftClient client)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("do nothing...");
        }
    }
    
    @Override
    public boolean validateObject(ThriftClient client)
    {
        return client.validate();
    }
    
    public abstract Class<T> supportType();
    
    public String getServerIp()
    {
        String dcType = SystemConfigContainer.getString(SystemConfigKeys.SYSTEM_DATACENTER_TYPE,
            ResourceGroupType.Merge.getType());
        String serverIp;
        if (StringUtils.equals(dcType, ResourceGroupType.Merge.getType()))
        {
            if (null == innerLoadBalanceManager)
            {
                innerLoadBalanceManager = (InnerLoadBalanceManager) BeanHolder.getBean("innerLoadBalanceManager");
            }
            
            serverIp = innerLoadBalanceManager.getUFMIp();
            if (StringUtils.isNotBlank(serverIp))
            {
                return serverIp;
            }
        }
        serverIp = SystemConfigContainer.getString(SystemConfigKeys.THRIFT_APPSERVER_SERVER_IP, "127.0.0.1");
        LOGGER.info("lvs loadBalance,get ip:" + serverIp);
        return serverIp;
    }
    
    public int getServerPort()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_SERVER_PORT, 12345);
    }
    
    public int getTimeout()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_REQUEST_TIMEOUT, 10000);
    }
    
    public int getDCType()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_REQUEST_TIMEOUT, 10000);
    }
}
