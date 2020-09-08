package pw.cdmi.box.disk.system.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.service.AccessAddressService;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.restrpc.RestClient;

@Component("accessAddressServiceImpl")
public class AccessAddressServiceImpl implements AccessAddressService, ConfigListener
{
    private AccessAddressConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private static final String GLOBAL_CONFIG_APP_ID = "-1";
    
    @Autowired
    private RestClient ufmClientService;
    
    @Autowired
    private RestClient uamClientService;
    
    @Override
    public AccessAddressConfig getAccessAddress()
    {
        if (localCache == null)
        {
            loadAccessAddressFromDb();
        }
        AccessAddressConfig addressConfig = new AccessAddressConfig();
        addressConfig.setAppId(localCache.getAppId());
        addressConfig.setUamOuterAddress(localCache.getUamOuterAddress());
        addressConfig.setUamInnerAddress(localCache.getUamInnerAddress());
        addressConfig.setUfmInnerAddress(localCache.getUfmInnerAddress());
        addressConfig.setUfmOuterAddress(localCache.getUfmOuterAddress());
        return addressConfig;
    }
    
    public void loadAccessAddressFromDb()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(GLOBAL_CONFIG_APP_ID,
            null,
            AccessAddressConfig.ACCESS_ADDRESS_CONFIG_PREFIX);
        localCache = AccessAddressConfig.buildAccessAddressConfig(itemList);
        ufmClientService.setServiceDomain(localCache.getUfmInnerAddress());
        uamClientService.setServiceDomain(localCache.getUamInnerAddress());
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (StringUtils.equals(key, "AccessAddressConfig"))
        {
            loadAccessAddressFromDb();
        }
    }
    
}
