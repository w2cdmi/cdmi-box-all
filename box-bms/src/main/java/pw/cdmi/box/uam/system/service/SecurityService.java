package pw.cdmi.box.uam.system.service;

import pw.cdmi.common.domain.SecurityConfig;

public interface SecurityService
{
    
    SecurityConfig getSecurityConfig(String appId);
    
    /**
     * 
     * @param securityConfig
     */
    void saveSecurityConfig(SecurityConfig securityConfig);
}
