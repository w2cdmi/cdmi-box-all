package pw.cdmi.file.engine.core.thrift.client;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;

public class UFMThriftClientProxyFactory extends ThriftClientProxyFactory
{
    @Override
    public String getInitStatus()
    {
        if (!SystemConfigContainer.getBoolean(SystemConfigKeys.SYSTEM_DATACENTER_BEEN_INITIALIZED, false))
        {
            return "dss is not init yet.";
        }
        return super.getInitStatus();
    }
}
