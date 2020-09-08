package pw.cdmi.file.engine.core.thrift.client;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.common.thrift.client.monitor.ThriftServiceMonitorConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroupType;

public class UFMThriftServiceMonitorConfig extends ThriftServiceMonitorConfig
{
    @Override
    public boolean isNeedMonitor()
    {
        String dcType = SystemConfigContainer.getString(SystemConfigKeys.SYSTEM_DATACENTER_TYPE,
            ResourceGroupType.Merge.getType());
        if (StringUtils.equals(dcType, ResourceGroupType.Merge.getType()))
        {
            return true;
        }
        return false;
    }
}
