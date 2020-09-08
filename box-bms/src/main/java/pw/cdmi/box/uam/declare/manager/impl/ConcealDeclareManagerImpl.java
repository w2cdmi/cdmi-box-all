package pw.cdmi.box.uam.declare.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.declare.manager.ConcealDeclareManager;
import pw.cdmi.box.uam.declare.service.ConcealDeclareService;
import pw.cdmi.box.uam.exception.ErrorCode;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.ClientManage.ClientType;

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
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        return concealDeclareService.getDeclaration(declare);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void create(ConcealDeclare declare)
    {
        update(declare);
        concealDeclareService.create(declare);
    }
    
    @Override
    public ConcealDeclare getDeclarationById(String declarationId)
    {
        return concealDeclareService.getDeclarationById(declarationId);
    }
    
    @Override
    public void update(ConcealDeclare declare)
    {
        concealDeclareService.update(declare);
    }
    
    @Override
    public List<ConcealDeclare> getConcealDeclaresByAppId(String appId)
    {
        List<ConcealDeclare> list = concealDeclareService.getConcealDeclaresByAppId(appId);
        ClientType[] types = ClientManage.ClientType.values();
        if (null == list)
        {
            list = new ArrayList<ConcealDeclare>(types.length);
        }
            
        if (list.isEmpty())
        {
            ConcealDeclare declare = null;
            for (ClientType clientType : types)
            {
                declare = new ConcealDeclare();
                declare.setAppId(appId);
                declare.setClientType(clientType.toString());
                list.add(declare);
            }
        }
        else
        {
            ConcealDeclare declare = null;
            for (ClientType clientType : types)
            {
                if (isInConcealDeclare(list, clientType))
                {
                    continue;
                }
                declare = new ConcealDeclare();
                declare.setAppId(appId);
                declare.setClientType(clientType.toString());
                list.add(declare);
            }
        }
        return list;
    }

    private boolean isInConcealDeclare(List<ConcealDeclare> list, ClientType clientType)
    {
        boolean temp = false;
        for (ConcealDeclare declare : list)
        {
            if (declare.getClientType().trim().equalsIgnoreCase(clientType.toString()))
            {
                temp = true;
                break;
            }
        }
        return temp;
    }
    
}
