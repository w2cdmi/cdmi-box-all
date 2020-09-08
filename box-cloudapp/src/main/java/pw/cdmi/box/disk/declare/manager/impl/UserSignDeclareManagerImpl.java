package pw.cdmi.box.disk.declare.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.declare.manager.ConcealDeclareManager;
import pw.cdmi.box.disk.declare.manager.UserSignDeclareManager;
import pw.cdmi.box.disk.declare.service.UserSignDeclareService;
import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.UserSignDeclare;

@Component
public class UserSignDeclareManagerImpl implements UserSignDeclareManager
{
    @Autowired
    private UserSignDeclareService userSignDeclareService;
    
    @Autowired
    private ConcealDeclareManager concealDeclareManager;
    
    @Override
    public void create(UserSignDeclare declare)
    {
        userSignDeclareService.create(declare);
    }
    
    @Override
    public UserSignDeclare getUserSignDeclare(UserSignDeclare declare)
    {
        return null;
    }
    
    @Override
    public void delete(UserSignDeclare declare)
    {
        
    }
    
    @Override
    public boolean isNeedDeclaration(UserSignDeclare declare, String appId)
    {
        ConcealDeclare conceal = new ConcealDeclare();
        conceal.setAppId(appId);
        conceal.setClientType(declare.getClientType());
        ConcealDeclare concealDeclare = concealDeclareManager.getDeclaration(conceal);
        if (concealDeclare == null)
        {
            return false;
        }
        
        UserSignDeclare signDeclare = userSignDeclareService.getUserSignByClientType(declare);
        if (signDeclare == null || StringUtils.isEmpty(signDeclare.getDeclareId()))
        {
            return true;
        }
        
        if (!concealDeclare.getId().equals(signDeclare.getDeclareId()))
        {
            return true;
        }
        
        return false;
    }
}
