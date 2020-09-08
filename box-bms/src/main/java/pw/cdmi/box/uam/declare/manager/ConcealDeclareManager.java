package pw.cdmi.box.uam.declare.manager;

import java.util.List;

import pw.cdmi.common.domain.ConcealDeclare;

public interface ConcealDeclareManager
{
    ConcealDeclare getDeclaration(ConcealDeclare declare);
    
    void create(ConcealDeclare declare);
    
    ConcealDeclare getDeclarationById(String declarationId);
    
    void update(ConcealDeclare declare);
    
    List<ConcealDeclare> getConcealDeclaresByAppId(String appId);
}
