package com.huawei.sharedrive.app.files.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.dao.INodeDeleteDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.INodeDeleteService;

import pw.cdmi.box.domain.Limit;

@Component
public class INodeDeleteServiceImpl implements INodeDeleteService
{
    @Autowired
    private INodeDeleteDAO iNodeDeleteDAO;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createINodeDeleteInfo(INode node)
    {
        iNodeDeleteDAO.create(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteINodeDeleteInfo(INode node)
    {
        iNodeDeleteDAO.delete(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode getINodeDeleteInfo(INode node)
    {
        return iNodeDeleteDAO.get(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<INode> getINodeDeleteInfoByStatus(INode filter, OrderV1 order, Limit limit)
    {
        return iNodeDeleteDAO.getINodeByStatus(filter, order, limit);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateINodeDeleteInfo(INode node)
    {
        iNodeDeleteDAO.update(node);
        
    }
    
}
