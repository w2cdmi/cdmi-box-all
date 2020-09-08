package pw.cdmi.box.disk.system.service;

import pw.cdmi.box.disk.event.service.EventConsumer;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.log.syslog.Syslog;

public interface SyslogServerService extends EventConsumer, ConfigListener
{
    
    Syslog getLogger();
    
    SysLogServer getSyslogServer();
}
