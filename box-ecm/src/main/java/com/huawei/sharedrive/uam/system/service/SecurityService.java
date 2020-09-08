package com.huawei.sharedrive.uam.system.service;

import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.SystemConfig;

public interface SecurityService
{
    
    SecurityConfig getSecurityConfig(String appId);
    
    /**
     * 
     * @param securityConfig
     */
    void saveSecurityConfig(SecurityConfig securityConfig);

	void saveSystemConfig(SystemConfig systemConfig);
}
