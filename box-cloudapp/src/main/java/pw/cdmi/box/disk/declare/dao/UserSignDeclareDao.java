package pw.cdmi.box.disk.declare.dao;

import pw.cdmi.common.domain.UserSignDeclare;

public interface UserSignDeclareDao
{
    
    void create(UserSignDeclare declare);
    
    UserSignDeclare getUserSignDeclare(UserSignDeclare declare);
    
    UserSignDeclare getUserSignByClientType(UserSignDeclare declare);
    
    void delete(UserSignDeclare declare);
}
