/**
 * 
 */
package com.huawei.sharedrive.app.system.service;

import pw.cdmi.common.domain.SecurityConfig;

/**
 * @author s00108907
 * 
 */
public interface SecurityService
{
    
    /**
     * 获取安全配置对象
     * 
     * @return
     */
    SecurityConfig getSecurityConfig();
    
    String changePort(String currentPort, String protocolType);
}
