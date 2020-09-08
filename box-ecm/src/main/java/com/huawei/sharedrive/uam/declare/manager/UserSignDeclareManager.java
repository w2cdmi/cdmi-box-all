package com.huawei.sharedrive.uam.declare.manager;

import pw.cdmi.common.domain.UserSignDeclare;

public interface UserSignDeclareManager
{
    void create(UserSignDeclare declare);
    
    boolean isNeedDeclaration(UserSignDeclare declare, String appId);
}
