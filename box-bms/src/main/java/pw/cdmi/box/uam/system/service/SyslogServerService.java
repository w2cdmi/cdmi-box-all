package pw.cdmi.box.uam.system.service;

import pw.cdmi.box.uam.event.service.EventConsumer;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.log.syslog.Syslog;

public interface SyslogServerService extends EventConsumer, ConfigListener
{
    
    SysLogServer getSyslogServer();
    
    /**
     * 
     * @param sysLogServer
     */
    void saveSysLogServer(SysLogServer sysLogServer);
    
    Syslog getLogger();
    
    String getSyslogSplit();
}
