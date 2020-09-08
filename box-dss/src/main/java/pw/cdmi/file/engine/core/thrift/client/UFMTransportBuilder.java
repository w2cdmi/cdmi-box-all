package pw.cdmi.file.engine.core.thrift.client;

import pw.cdmi.common.thrift.client.TransportBuilder;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;

public class UFMTransportBuilder extends TransportBuilder
{
    @Override
    public int getTransportTimeout()
    {
        return SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_REQUEST_TIMEOUT, 10000);
    }
}
