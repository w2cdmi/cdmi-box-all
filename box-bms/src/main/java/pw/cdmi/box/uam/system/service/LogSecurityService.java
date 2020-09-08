package pw.cdmi.box.uam.system.service;

public interface LogSecurityService
{
    
    Boolean isUserLogVisible();
    
    /**
     * 
     * @param logSecurity
     */
    void saveLogSecurityConfig(String logSecurity);
}
