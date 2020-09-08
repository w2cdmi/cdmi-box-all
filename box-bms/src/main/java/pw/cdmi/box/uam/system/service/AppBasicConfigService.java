package pw.cdmi.box.uam.system.service;

import pw.cdmi.common.domain.AppBasicConfig;

public interface AppBasicConfigService
{
    AppBasicConfig getAppBasicConfig(String appId);
    
    /**
     * 
     * @param accessAddressConfig
     */
    void saveAppBasicConfig(AppBasicConfig appBasicConfig);
}
