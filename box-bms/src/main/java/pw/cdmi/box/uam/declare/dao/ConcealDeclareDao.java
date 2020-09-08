package pw.cdmi.box.uam.declare.dao;

import java.util.List;

import pw.cdmi.common.domain.ConcealDeclare;

public interface ConcealDeclareDao
{
    ConcealDeclare getDeclaration(ConcealDeclare declare);
    
    ConcealDeclare getDeclarationById(String declarationId);
    
    void create(ConcealDeclare declare);
    
    void update(ConcealDeclare declare);
    
    List<ConcealDeclare> getConcealDeclaresByAppId(String appId);
}
