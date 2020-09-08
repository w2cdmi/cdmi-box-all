/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service;

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
    
    /**
     * 保存安全配置对象
     * 
     * @param securityConfig
     */
    void saveSecurityConfig(SecurityConfig securityConfig);
}
