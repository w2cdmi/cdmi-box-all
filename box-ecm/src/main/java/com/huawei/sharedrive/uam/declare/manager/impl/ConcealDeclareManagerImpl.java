package com.huawei.sharedrive.uam.declare.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.declare.manager.ConcealDeclareManager;
import com.huawei.sharedrive.uam.declare.service.ConcealDeclareService;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

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
    public ConcealDeclare getDeclarationById(String declarationId)
    {
        return concealDeclareService.getDeclarationById(declarationId);
    }
    
}
