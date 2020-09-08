package pw.cdmi.box.disk.declare.manager;

import pw.cdmi.common.domain.ConcealDeclare;

public interface ConcealDeclareManager
{
    ConcealDeclare getDeclaration(ConcealDeclare declare);
    
    void create(ConcealDeclare declare);
    
    void delete(ConcealDeclare declare);
    
    ConcealDeclare getDeclarationById(String declarationId);
}
