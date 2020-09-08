package pw.cdmi.box.disk.declare.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.declare.manager.ConcealDeclareManager;
import pw.cdmi.box.disk.declare.service.ConcealDeclareService;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.ClientManage.ClientType;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.InvalidParamException;

@Component
public class ConcealDeclareManagerImpl implements ConcealDeclareManager
{
    @Autowired
    private ConcealDeclareService concealDeclareService;
    
    @Override
    public ConcealDeclare getDeclaration(ConcealDeclare declare)
    {
        String clientType = declare.getClientType();
        ClientType[] clients = ClientManage.ClientType.values();
        boolean flag = true;
        for (ClientType type : clients)
        {
            if (clientType.equalsIgnoreCase(type.toString()))
            {
                flag = false;
            }
        }
        if (flag)
        {
            throw new InvalidParamException(ErrorCode.INVALID_PARAMETER.getMessage());
        }
        return concealDeclareService.getDeclaration(declare);
    }
    
    @Override
    public void create(ConcealDeclare declare)
    {
        concealDeclareService.create(declare);
    }
    
    @Override
    public void delete(ConcealDeclare declare)
    {
        concealDeclareService.delete(declare);
    }
    
    @Override
    public ConcealDeclare getDeclarationById(String declarationId)
    {
        return concealDeclareService.getDeclarationById(declarationId);
    }
    
}
