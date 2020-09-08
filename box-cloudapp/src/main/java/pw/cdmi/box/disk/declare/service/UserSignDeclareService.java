package pw.cdmi.box.disk.declare.service;

import pw.cdmi.common.domain.UserSignDeclare;

public interface UserSignDeclareService
{
    void create(UserSignDeclare declare);
    
    UserSignDeclare getUserSignDeclare(UserSignDeclare declare);
    
    UserSignDeclare getUserSignByClientType(UserSignDeclare declare);
    
    void delete(UserSignDeclare declare);
}
