package pw.cdmi.box.disk.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.domain.Customize;
import pw.cdmi.box.disk.system.service.CustomizeService;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class CustomizeServiceImpl implements CustomizeService
{
    private Customize localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public Customize getCustomize()
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(authAppService.getCurrentAppId(),
                null,
                "customize");
            localCache = Customize.buildMailServer(itemList);
        }
        return localCache;
    }
    
}
