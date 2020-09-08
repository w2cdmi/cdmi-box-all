package pw.cdmi.box.disk.declare.manager;

import pw.cdmi.common.domain.UserSignDeclare;

public interface UserSignDeclareManager
{
    void create(UserSignDeclare declare);
    
    UserSignDeclare getUserSignDeclare(UserSignDeclare declare);
    
    boolean isNeedDeclaration(UserSignDeclare declare, String appId);
    
    void delete(UserSignDeclare declare);
}
