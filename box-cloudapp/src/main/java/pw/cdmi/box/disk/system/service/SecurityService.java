package pw.cdmi.box.disk.system.service;

import pw.cdmi.common.domain.SecurityConfig;

public interface SecurityService
{
    
    SecurityConfig getSecurityConfig();
    
    String changePort(String currentPort, String protocolType);
}
