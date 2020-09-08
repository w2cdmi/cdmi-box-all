package pw.cdmi.file.engine.core.thrift.client;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.common.thrift.client.ThriftServiceAddress;
import pw.cdmi.common.thrift.client.pool.TTransportManagerConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroupType;

public class SZUFMTTransportManagerConfig extends TTransportManagerConfig
{
    @Override
    public ThriftServiceAddress getLoadBalanceAddress()
    {
        String dcType = SystemConfigContainer.getString(SystemConfigKeys.SYSTEM_DATACENTER_TYPE,
            ResourceGroupType.Merge.getType());
        if (StringUtils.equals(dcType, ResourceGroupType.Merge.getType()))
        {
            return null;
        }
        String serverIp = SystemConfigContainer.getString(SystemConfigKeys.SZ_THRIFT_APPSERVER_SERVER_IP,
            "127.0.0.1");
        int port = SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_SERVER_PORT, 13003);
        return new ThriftServiceAddress(serverIp, port);
    }
    
    @Override
    public ThriftServiceAddress getBackupAddress()
    {
        String serverIp = SystemConfigContainer.getString(SystemConfigKeys.SZ_THRIFT_APPSERVER_SERVER_IP,
            "127.0.0.1");
        int port = SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_SERVER_PORT, 13003);
        return new ThriftServiceAddress(serverIp, port);
    }
}
