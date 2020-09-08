package pw.cdmi.box.disk.declare.dao;

import pw.cdmi.common.domain.ConcealDeclare;

public interface ConcealDeclareDao
{
    ConcealDeclare getDeclaration(ConcealDeclare declare);
    
    ConcealDeclare getDeclarationById(String declarationId);
    
    void create(ConcealDeclare declare);
    
    void update(ConcealDeclare declare);
    
    void delete(ConcealDeclare declare);
}
