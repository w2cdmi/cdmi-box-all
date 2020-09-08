package com.huawei.sharedrive.app.share.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.share.dao.INodeLinkDynamicDao;
import com.huawei.sharedrive.app.share.domain.INodeLinkDynamic;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("iNodeLinkDynamicDao")
@SuppressWarnings("deprecation")
public class INodeLinkDynamicDaoImpl extends AbstractDAOImpl implements INodeLinkDynamicDao
{
    
    @Override
    public void create(INodeLinkDynamic iNodeLinkDynamic)
    {
        sqlMapClientTemplate.insert("INodeLinkDynamic.insert", iNodeLinkDynamic);
    }
    
    @Override
    public int delete(String linkCode, String identity)
    {
        INodeLinkDynamic iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(linkCode);
        iNodeLinkDynamic.setIdentity(identity);
        return sqlMapClientTemplate.delete("INodeLinkDynamic.deleteByIdentity", iNodeLinkDynamic);
    }
    
    @Override
    public int deleteAll(String linkCode)
    {
        INodeLinkDynamic iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(linkCode);
        return sqlMapClientTemplate.delete("INodeLinkDynamic.deleteAll", iNodeLinkDynamic);
        
    }
    
    @Override
    public INodeLinkDynamic get(String linkCode, String identity)
    {
        INodeLinkDynamic iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(linkCode);
        iNodeLinkDynamic.setIdentity(identity);
        
        return (INodeLinkDynamic) sqlMapClientTemplate.queryForObject("INodeLinkDynamic.getByIdentity",
            iNodeLinkDynamic);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeLinkDynamic> list(INodeLinkDynamic iNodeLinkDynamic)
    {
        return sqlMapClientTemplate.queryForList("INodeLinkDynamic.list", iNodeLinkDynamic);
    }
    
    @Override
    public int updateExpiredAt(INodeLinkDynamic iNodeLinkDynamic)
    {
        return sqlMapClientTemplate.update("INodeLinkDynamic.updateExpiredAt", iNodeLinkDynamic);
    }
    
    @Override
    public int updatePassword(INodeLinkDynamic iNodeLinkDynamic)
    {
        return sqlMapClientTemplate.update("INodeLinkDynamic.updatePassword", iNodeLinkDynamic);
    }
    
    @Override
    public int upgradePassword(INodeLinkDynamic iNodeLinkDynamic)
    {
        return sqlMapClientTemplate.update("INodeLinkDynamic.upgradePassword", iNodeLinkDynamic);
    }
    
}
