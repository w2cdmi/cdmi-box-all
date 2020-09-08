package pw.cdmi.box.uam.declare.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.declare.dao.ConcealDeclareDao;
import pw.cdmi.box.uam.declare.service.ConcealDeclareService;
import pw.cdmi.common.domain.ConcealDeclare;

@Service
public class ConcealDeclareServiceImpl implements ConcealDeclareService
{
    
    @Autowired
    private ConcealDeclareDao concealDeclareDao;
    
    @Override
    public ConcealDeclare getDeclaration(ConcealDeclare declare)
    {
        return concealDeclareDao.getDeclaration(declare);
    }
    
    @Override
    public void create(ConcealDeclare declare)
    {
        concealDeclareDao.create(declare);
    }
    
    @Override
    public ConcealDeclare getDeclarationById(String declarationId)
    {
        return concealDeclareDao.getDeclarationById(declarationId);
    }
    
    @Override
    public void update(ConcealDeclare declare)
    {
        concealDeclareDao.update(declare);
    }
    
    @Override
    public List<ConcealDeclare> getConcealDeclaresByAppId(String appId)
    {
        return concealDeclareDao.getConcealDeclaresByAppId(appId);
    }
    
}
