/**
 * 
 */
package com.huawei.sharedrive.app.system.service;

import com.huawei.sharedrive.app.event.service.EventConsumer;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.log.syslog.Syslog;

/**
 * @author d00199602
 * 
 */
public interface SyslogServerService extends EventConsumer, ConfigListener
{
    
    Syslog getLogger();
    
    /**
     * 获取当前的Syslog服务器配置
     * 
     * @return
     */
    SysLogServer getSyslogServer();
}
