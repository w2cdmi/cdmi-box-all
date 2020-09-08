/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service;

import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.log.LogLanguage;
import pw.cdmi.common.log.syslog.Syslog;

/**
 * @author d00199602
 * 
 */
public interface SyslogServerService
{
    
    /**
     * 获取当前的Syslog服务器配置
     * 
     * @return
     */
    SysLogServer getSyslogServer();
    
    /**
     * 保存Syslog服务器配置信息
     * 
     * @param sysLogServer
     */
    void saveSysLogServer(SysLogServer sysLogServer);
    
    Syslog getLogger();
    
    String getSyslogSplit();
    
    /**
     * 设置用户语日志语言
     * @param language
     * @return
     */
    boolean setUserLanguage(LogLanguage logLanguage);
    LogLanguage getLogLanguage();
}
