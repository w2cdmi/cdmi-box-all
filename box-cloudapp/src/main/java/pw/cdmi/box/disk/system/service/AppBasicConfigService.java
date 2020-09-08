package pw.cdmi.box.disk.system.service;

import pw.cdmi.common.domain.AppBasicConfig;

public interface AppBasicConfigService
{
    /**
     * 
     * @return
     */
    AppBasicConfig getAppBasicConfig(String appId);
    
}
