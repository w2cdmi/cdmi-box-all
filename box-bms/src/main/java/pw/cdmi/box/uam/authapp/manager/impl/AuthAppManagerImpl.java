package pw.cdmi.box.uam.authapp.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.box.uam.authapp.manager.AuthAppManager;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class AuthAppManagerImpl implements AuthAppManager
{
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public List<AuthApp> getAuthAppList(AuthAppExtend filter, Order order, Limit limit)
    {
        
        return authAppService.getAuthAppList(filter, order, limit);
    }
    
    @Override
    public Page<AuthApp> getByAuthentication(long enterpriseId, PageRequest pageRequest)
    {
        int total = authAppService.getCountByAuthentication(enterpriseId);
        List<AuthApp> content = authAppService.getByAuthentication(enterpriseId, pageRequest.getLimit());
        Page<AuthApp> page = new PageImpl<AuthApp>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        return authAppService.getByAuthAppID(authAppId);
    }
    
}
