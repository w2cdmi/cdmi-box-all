package com.huawei.sharedrive.uam.declare.manager;

import pw.cdmi.common.domain.ConcealDeclare;

public interface ConcealDeclareManager
{
    ConcealDeclare getDeclaration(ConcealDeclare declare);
    
    ConcealDeclare getDeclarationById(String declarationId);
}
