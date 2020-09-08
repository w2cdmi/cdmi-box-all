package com.huawei.sharedrive.uam.system.service;

public interface LogSecurityService
{
    
    boolean isUserLogVisible();
    
    /**
     * 
     * @param logSecurity
     */
    void saveLogSecurityConfig(String logSecurity);
}
